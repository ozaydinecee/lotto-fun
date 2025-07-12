package com.project.lottofun.service.impl;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.TicketPurchaseRequest;
import com.project.lottofun.model.dto.TicketResponse;
import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.model.enums.TicketStatus;
import com.project.lottofun.repository.DrawRepository;
import com.project.lottofun.repository.TicketRepository;
import com.project.lottofun.repository.UserRepository;
import com.project.lottofun.service.interfaces.DrawService;
import com.project.lottofun.service.interfaces.TicketService;
import com.project.lottofun.validator.DrawLifeCycleValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    private final DrawRepository drawRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final DrawLifeCycleValidator drawLifecycleValidator;
    private final DrawService drawService;

    private static final BigDecimal TICKET_PRICE = new BigDecimal("10");
    public TicketServiceImpl(DrawRepository drawRepository, UserRepository userRepository, TicketRepository ticketRepository, DrawLifeCycleValidator drawLifecycleValidator, DrawService drawService){
        this.drawRepository= drawRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.drawLifecycleValidator = drawLifecycleValidator;
        this.drawService = drawService;
    }
    @Override
    public ApiResponse<TicketResponse> purchaseTicket(Long userId, TicketPurchaseRequest request) {
        Draw activeDraw = drawService.getActiveDrawForPurchase();
        drawLifecycleValidator.validateForTicketPurchase(activeDraw);

        User user = getUser(userId);

        validateNumbers(request.getNumbers());
        validateBalance(user);

        deductBalance(user);
        Ticket ticket = createTicket(user, activeDraw, request.getNumbers());

        TicketResponse response = new TicketResponse(ticket);
        return new ApiResponse<>(true, "Ticket purchased successfully", response);
    }

    @Override
    public ApiResponse<List<TicketResponse>> getTicketsForUser(Long userId) {
        User user = getUser(userId);
        List<Ticket> tickets = ticketRepository.findAllByUser(user);
        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::new)
                .toList();
        return new ApiResponse<>(true, "User's tickets retrieved", responses);
    }

    @Override
    public ApiResponse<List<TicketResponse>> getTicketsByDraw(Integer drawNumber) {
        Draw draw = drawRepository.findByDrawNumber(drawNumber)
                .orElseThrow(() -> new IllegalArgumentException("Draw not found"));
        List<Ticket> tickets = ticketRepository.findAllByDraw(draw);
        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::new)
                .toList();
        return new ApiResponse<>(true, "Tickets for draw retrieved", responses);
    }

    /**
     * Retrieves all tickets purchased by a specific user for the currently active draw
     * that is available for ticket purchase (DRAW_OPEN and not expired).
     *
     * @param userId ID of the user whose tickets are being retrieved
     * @return ApiResponse containing a list of TicketResponse for the active draw
     */
    @Override
    public ApiResponse<List<TicketResponse>> getTicketsForActiveDraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Draw activeDraw = drawService.getActiveDraw();

        List<Ticket> tickets = ticketRepository.findAllByUserAndDraw(user, activeDraw);

        List<TicketResponse> responses = tickets.stream()
                .map(TicketResponse::new)
                .toList();

        return new ApiResponse<>(true, "User's tickets for active draw retrieved", responses);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validateNumbers(Set<Integer> numbers) {
        if (numbers == null || numbers.size() != 5) {
            throw new IllegalArgumentException("Exactly 5 numbers must be selected");
        }
        Set<Integer> unique = new HashSet<>(numbers);
        if (unique.size() != 5) {
            throw new IllegalArgumentException("Numbers must be unique");
        }
        for (Integer num : numbers) {
            if (num < 1 || num > 49) {
                throw new IllegalArgumentException("Numbers must be between 1 and 49");
            }
        }
    }

    private void validateBalance(User user) {
        if (user.getBalance().compareTo(TICKET_PRICE) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
    }

    private void deductBalance(User user) {
        BigDecimal newBalance = user.getBalance().subtract(TICKET_PRICE);
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    private Ticket createTicket(User user, Draw draw, Set<Integer> numbers) {
        String ticketNumber = generateTicketNumber(user, draw);

        Ticket ticket = Ticket.builder()
                .ticketNumber(ticketNumber)
                .user(user)
                .draw(draw)
                .status(TicketStatus.PENDING)
                .selectedNumbers(numbers)
                .purchaseTime(LocalDateTime.now())
                .build();
        return ticketRepository.save(ticket);
    }
    private String generateTicketNumber(User user, Draw draw) {
        return user.getUsername() + "-" + draw.getDrawNumber() + "-" + System.currentTimeMillis();
    }

}
