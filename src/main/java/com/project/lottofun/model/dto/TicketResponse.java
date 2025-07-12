package com.project.lottofun.model.dto;


import com.project.lottofun.model.entity.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class TicketResponse {
    private Long ticketId;
    private Integer drawNumber;
    private Set<Integer> selectedNumbers;
    private LocalDateTime purchaseTime;
    private BigDecimal prizeAmount;
    private String status;
    private String ticketNumber;

    // after draw ended
    private Set<Integer> winningNumbers;
    private Integer matchCount;
    private String drawStatus;

    private String username;
    public TicketResponse(Ticket ticket) {
        this.ticketId = ticket.getId();
        this.ticketNumber = ticket.getTicketNumber();
        this.drawNumber = ticket.getDraw().getDrawNumber();
        this.selectedNumbers = ticket.getSelectedNumbers();
        this.purchaseTime = ticket.getPurchaseTime();
        this.prizeAmount = ticket.getPrizeAmount();
        this.status = String.valueOf(ticket.getStatus());
        this.username = ticket.getUser().getUsername();

        if (ticket.getDraw() != null) {
            this.drawStatus = String.valueOf(ticket.getDraw().getStatus());
            if (ticket.getDraw().getWinningNumbers() != null) {
                this.winningNumbers = ticket.getDraw().getWinningNumbers();
                this.matchCount = (int) ticket.getSelectedNumbers().stream()
                        .filter(ticket.getDraw().getWinningNumbers()::contains)
                        .count();
            }
        }
    }
    /*
    public static TicketResponse fromEntity(Ticket ticket) {
        return new TicketResponse(ticket);
    }*/
}

