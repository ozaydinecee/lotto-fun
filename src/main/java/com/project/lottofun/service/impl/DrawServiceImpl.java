package com.project.lottofun.service.impl;

import com.project.lottofun.exception.DrawExecutionException;
import com.project.lottofun.factory.DrawFactory;
import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.DrawResponse;
import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.model.enums.DrawStatus;
import com.project.lottofun.model.enums.PrizeType;
import com.project.lottofun.model.enums.TicketStatus;
import com.project.lottofun.repository.DrawRepository;
import com.project.lottofun.repository.TicketRepository;
import com.project.lottofun.repository.UserRepository;
import com.project.lottofun.service.interfaces.DrawService;
import com.project.lottofun.service.strategy.PrizeCalculationStrategy;
import com.project.lottofun.service.strategy.WinnerSelectionStrategy;
import com.project.lottofun.validator.DrawLifeCycleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class DrawServiceImpl implements DrawService {

    private final DrawRepository drawRepository;
    private final TicketRepository ticketRepository;
    private final PrizeCalculationStrategy prizeStrategy;
   // @Autowired
    private final WinnerSelectionStrategy winnerSelectionStrategy;
    private final DrawFactory drawFactory;

    private final DrawLifeCycleValidator drawLifeCycleValidator;

    private final UserRepository userRepository;


    public DrawServiceImpl(DrawRepository drawRepository, TicketRepository ticketRepository, PrizeCalculationStrategy prizeStrategy, WinnerSelectionStrategy winnerSelectionStrategy, DrawFactory drawFactory, DrawLifeCycleValidator drawLifeCycleValidator, UserRepository userRepository) {
        this.drawRepository = drawRepository;
        this.ticketRepository = ticketRepository;
        this.prizeStrategy = prizeStrategy;
        this.winnerSelectionStrategy=winnerSelectionStrategy;
        this.drawFactory = drawFactory;
        this.drawLifeCycleValidator = drawLifeCycleValidator;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional //ACID: atomic , otherwise rollback whole transactions
    public void executeDrawIfDue() {
        try {
            Draw draw = getDueDraw();
            if (draw == null) {
                log.info("No draw due for execution at this time.");
                return;
            }
            drawLifeCycleValidator.validateForExecution(draw);
            log.info("Executing draw with ID: {}", draw.getId());
            // STEP 1: Close the draw for further ticket sales
            markDrawAsClosed(draw); // not meaningful to set again no logic

            markDrawAsExtracted(draw);

            Set<Integer> winningNumbers = winnerSelectionStrategy.selectWinningNumbers();
            draw.setWinningNumbers(winningNumbers);
            drawRepository.save(draw);

            markDrawAsPaymentProcessing(draw);

            processTickets(draw, winningNumbers);
            markDrawAsFinalized(draw);
            log.info("Draw execution complete for draw ID: {}", draw.getId());
           createNextDraw(draw);

        } catch (Exception e) {
            log.error("Error during draw execution", e);
            throw new DrawExecutionException("Failed to execute draw", e);
        }
    }

    private void createNextDraw(Draw previousDraw) {
       // drawRepository.save(drawFactory.createNextDraw(previousDraw.getDrawNumber()));
        Draw nextDraw = drawFactory.createNextDraw(previousDraw.getDrawNumber());
        drawRepository.save(nextDraw);
    }


    private Draw getDueDraw() {
        return drawRepository.findFirstByDrawDateBeforeAndStatus(LocalDateTime.now(), DrawStatus.DRAW_OPEN)
                .orElse(null);
    }

    private void markDrawAsExtracted(Draw draw) {
        draw.setStatus(DrawStatus.DRAW_EXTRACTED);
        drawRepository.save(draw);
        log.info("Draw with ID: {} marked as draw extracted status", draw.getId());

    }
    private void markDrawAsClosed(Draw draw) {
        draw.setStatus(DrawStatus.DRAW_CLOSED);
        drawRepository.save(draw);
        log.info("Draw with ID: {} marked as draw closed status", draw.getId());

    }
    private void markDrawAsPaymentProcessing(Draw draw) {
        draw.setStatus(DrawStatus.PAYMENTS_PROCESSING);
        drawRepository.save(draw);
        log.info("Draw with ID: {} marked as payment processing status", draw.getId());
    }


    private void processTickets(Draw draw, Set<Integer> winningNumbers) {
        log.info("Processing tickets for draw #{} started with winning numbers: {}", draw.getDrawNumber(), winningNumbers);
        List<Ticket> tickets = ticketRepository.findAllByDraw(draw);
        if (tickets.isEmpty()) {
            log.warn("No tickets found for draw #{}. Skipping processing.", draw.getDrawNumber());
            return;
        }
        log.info("Found {} tickets for draw #{}", tickets.size(), draw.getDrawNumber());
        for (Ticket ticket : tickets) {
            int matchCount = (int) ticket.getSelectedNumbers()
                    .stream()
                    .filter(winningNumbers::contains)
                    .count();

            PrizeType prizeType = prizeStrategy.calculatePrize(matchCount);
            BigDecimal prizeAmount = prizeStrategy.calculatePrizeAmount(prizeType);

            ticket.setPrizeAmount(prizeAmount);
            ticket.setStatus(mapPrizeTypeToTicketStatus(prizeType));
            ticketRepository.save(ticket);
            log.debug("Ticket #{} by user '{}' matched {} numbers → Prize: {}, Status: {}",
                    ticket.getTicketNumber(),
                    ticket.getUser().getUsername(),
                    matchCount,
                    prizeAmount,
                    ticket.getStatus());
            
            if (prizeAmount.compareTo(BigDecimal.ZERO) > 0) {
                rewardUser(ticket.getUser(), prizeAmount);
            }
        }

        log.info("Finished processing {} tickets for draw #{}", tickets.size(), draw.getDrawNumber());
    }

    private void rewardUser(User user, BigDecimal prizeAmount) {
        user.setBalance(user.getBalance().add(prizeAmount));
        userRepository.save(user);
    }

    private TicketStatus mapPrizeTypeToTicketStatus(PrizeType prizeType) {
        return prizeType == PrizeType.NO_PRIZE ? TicketStatus.NOT_WON : TicketStatus.WON; // ticket life cycle
    }
    private void markDrawAsFinalized(Draw draw) {
        draw.setStatus(DrawStatus.DRAW_FINALIZED);
        drawRepository.save(draw);
    }
    @Override
    public Draw getActiveDrawForPurchase() {
        Draw draw= drawRepository.findTopByStatusOrderByDrawDateAsc(DrawStatus.DRAW_OPEN)
                .orElseThrow(() -> new IllegalStateException("No active draw available"));
        if (draw.getDrawDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Draw is closed. Ticket purchase is not allowed.");
        }
        log.info("active draw draw id :{} draw tickets : {} draw date:{}", draw.getId(),draw.getTickets(),draw.getDrawDate());
        return draw;
    }

    @Override
    public Draw getActiveDraw() {
        return drawRepository.findTopByStatusOrderByDrawDateAsc(DrawStatus.DRAW_OPEN)
                .orElseThrow(() -> new IllegalStateException("No active draw available"));
    }

    @Override
    public ApiResponse<DrawResponse> getActiveDrawInfo() {
        Draw draw = getActiveDraw();
        DrawResponse response = new DrawResponse(draw);
        return new ApiResponse<>(true, "Active draw retrieved", response);
    }
    @Override
    public ApiResponse<List<DrawResponse>> getExtractedDraws() {
        List<DrawStatus> completedStatuses = List.of(
                DrawStatus.DRAW_EXTRACTED,
                DrawStatus.DRAW_FINALIZED,
                DrawStatus.DRAW_CLOSED
        );
        List<Draw> draws = drawRepository.findByStatusInOrderByDrawDateDesc(completedStatuses);
        List<DrawResponse> responseList = draws.stream()
                .map(DrawResponse::new)
                .toList();
        return new ApiResponse<>(true, "Completed draws listed", responseList);
    }

    @Override
    public ApiResponse<List<DrawResponse>> getAllDraws() {
        List<Draw> draws = drawRepository.findAllByOrderByDrawDateDesc();
        List<DrawResponse> responses = draws.stream()
                .map(DrawResponse::new)
                .toList();
        return new ApiResponse<>(true, "All draws retrieved", responses);
    }

    @Override
    public ApiResponse<DrawResponse> getDrawByNumber(Integer drawNumber) {
        Draw draw = drawRepository.findByDrawNumber(drawNumber)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found"));
        return new ApiResponse<>(true, "Draw retrieved", new DrawResponse(draw));
    }

}
