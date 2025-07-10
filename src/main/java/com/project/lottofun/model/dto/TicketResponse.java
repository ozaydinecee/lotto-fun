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

    public TicketResponse(Ticket ticket) {
        this.ticketId = ticket.getId();
        this.drawNumber = ticket.getDraw().getDrawNumber();
        this.selectedNumbers = ticket.getSelectedNumbers();
        this.purchaseTime = ticket.getPurchaseTime();
        this.prizeAmount = ticket.getPrizeAmount();
        this.status = String.valueOf(ticket.getStatus()); // Enum ise string'e çevir
    }

}
