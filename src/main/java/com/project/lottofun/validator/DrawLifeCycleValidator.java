package com.project.lottofun.validator;

import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.enums.DrawStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DrawLifeCycleValidator {
    public void validateForTicketPurchase(Draw draw) {
        if (draw.getStatus() != DrawStatus.DRAW_OPEN) {
            throw new IllegalStateException("Tickets can only be purchased for OPEN draws.");
        }
    }

    public void validateForExecution(Draw draw) {
        if (draw.getStatus() != DrawStatus.DRAW_OPEN) {
            throw new IllegalStateException("Only OPEN draws can be executed.");
        }
    }

    public void validateStatusTransition(Draw draw, DrawStatus expectedStatus) {
        if (draw.getStatus() != expectedStatus) {
            throw new IllegalStateException("Draw must be in status " + expectedStatus + " to proceed.");
        }
    }
}
