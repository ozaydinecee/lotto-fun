package com.project.lottofun.model.dto;

import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private BigDecimal balance;

    private List<TicketResponse> tickets;
    private String token;


    public UserResponse(Long id, String username, BigDecimal balance, List<TicketResponse> tickets, String token) {
        this.id = id;
        this.username = username;
        this.balance = balance;
        this.tickets = tickets;
        this.token = token;
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.balance = user.getBalance();
    }

    public UserResponse(User user, List<Ticket> ticketList) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.balance = user.getBalance();
        this.tickets = ticketList.stream()
                .map(TicketResponse::new)
                .collect(Collectors.toList());
    }
}
