package com.project.lottofun.service.impl;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.TicketPurchaseRequest;
import com.project.lottofun.model.dto.TicketResponse;
import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.model.enums.DrawStatus;
import com.project.lottofun.model.enums.TicketStatus;
import com.project.lottofun.repository.DrawRepository;
import com.project.lottofun.repository.TicketRepository;
import com.project.lottofun.repository.UserRepository;
import com.project.lottofun.service.interfaces.TicketService;
import com.project.lottofun.validator.DrawLifeCycleValidator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TicketServiceImpl implements TicketService {

    private final DrawRepository drawRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final DrawLifeCycleValidator drawLifecycleValidator;

    private static final BigDecimal TICKET_PRICE = new BigDecimal("10");
    public TicketServiceImpl(DrawRepository drawRepository, UserRepository userRepository, TicketRepository ticketRepository, DrawLifeCycleValidator drawLifecycleValidator){
        this.drawRepository= drawRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.drawLifecycleValidator = drawLifecycleValidator;
    }
    @Override
    public ApiResponse<TicketResponse> purchaseTicket(Long userId, TicketPurchaseRequest request) {
        Draw activeDraw = getActiveDraw();
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

    private Draw getActiveDraw() {
       Draw draw= drawRepository.findTopByStatusOrderByDrawDateAsc(DrawStatus.DRAW_OPEN)
                .orElseThrow(() -> new IllegalStateException("No active draw available"));
        if (draw.getDrawDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Draw is closed. Ticket purchase is not allowed.");
        }
        return draw;
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
        Ticket ticket = Ticket.builder()
                .user(user)
                .draw(draw)
                .status(TicketStatus.PENDING)
                .selectedNumbers(numbers)
                .purchaseTime(LocalDateTime.now())
                .build();
        return ticketRepository.save(ticket);
    }

}
