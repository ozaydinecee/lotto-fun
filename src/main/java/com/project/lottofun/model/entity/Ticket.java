package com.project.lottofun.model.entity;

import com.project.lottofun.model.enums.PrizeType;
import com.project.lottofun.model.enums.TicketStatus;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = true)
    private String ticketNumber;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Draw draw;

    @ElementCollection
    private Set<Integer> selectedNumbers;

    @Column(nullable = false)
    private LocalDateTime purchaseTime;

    private Integer matchCount;

    private BigDecimal prizeAmount;

    @Enumerated(EnumType.STRING)
    private TicketStatus status = TicketStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PrizeType prize;
}
