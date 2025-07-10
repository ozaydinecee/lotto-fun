package com.project.lottofun.model.dto;

import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.enums.DrawStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DrawResponse {
    private Long drawId;
    private Integer drawNumber;
    private LocalDateTime drawDate;
    private DrawStatus status;
    private int ticketCount;

    public DrawResponse(Draw draw) {
        this.drawId = draw.getId();
        this.drawNumber = draw.getDrawNumber();
        this.drawDate = draw.getDrawDate();
        this.status = draw.getStatus();
        this.ticketCount = draw.getTickets() != null ? draw.getTickets().size() : 0;
    }
}
