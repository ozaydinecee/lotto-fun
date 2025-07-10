package com.project.lottofun.service.impl;

import com.project.lottofun.exception.DrawExecutionException;
import com.project.lottofun.factory.DrawFactory;
import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.enums.DrawStatus;
import com.project.lottofun.model.enums.PrizeType;
import com.project.lottofun.model.enums.TicketStatus;
import com.project.lottofun.repository.DrawRepository;
import com.project.lottofun.repository.TicketRepository;
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


    public DrawServiceImpl(DrawRepository drawRepository, TicketRepository ticketRepository, PrizeCalculationStrategy prizeStrategy, WinnerSelectionStrategy winnerSelectionStrategy, DrawFactory drawFactory, DrawLifeCycleValidator drawLifeCycleValidator) {
        this.drawRepository = drawRepository;
        this.ticketRepository = ticketRepository;
        this.prizeStrategy = prizeStrategy;
        this.winnerSelectionStrategy=winnerSelectionStrategy;
        this.drawFactory = drawFactory;
        this.drawLifeCycleValidator = drawLifeCycleValidator;
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
            markDrawAsClosed(draw);

            markDrawAsExtracted(draw);

            Set<Integer> winningNumbers = winnerSelectionStrategy.selectWinningNumbers();
            draw.setWinningNumbers(winningNumbers);
            drawRepository.save(draw);

            markDrawAsPaymentProcessing(draw);

            processTickets(draw, winningNumbers);
            markDrawAsFinalized(draw);

           createNextDraw(draw);
            log.info("Draw execution complete for draw ID: {}", draw.getId());

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
        List<Ticket> tickets = ticketRepository.findAllByDraw(draw);
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
        }
    }
    private TicketStatus mapPrizeTypeToTicketStatus(PrizeType prizeType) {
        return prizeType == PrizeType.NO_PRIZE ? TicketStatus.NOT_WON : TicketStatus.WON;
    }
    private void markDrawAsFinalized(Draw draw) {
        draw.setStatus(DrawStatus.DRAW_FINALIZED);
        drawRepository.save(draw);
        log.info("Dra");
    }


}
