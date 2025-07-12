package com.project.lottofun.factory.impl;

import com.project.lottofun.factory.DrawFactory;
import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.enums.DrawStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class DefaultDrawFactory implements DrawFactory {

    private static final long DRAW_INTERVAL_HOURS = 1;
    @Override
    public Draw createInitialDraw() {
        Draw draw = new Draw();
        draw.setDrawNumber(1);
        draw.setDrawDate(LocalDateTime.now().plusHours(DRAW_INTERVAL_HOURS));
        draw.setStatus(DrawStatus.DRAW_OPEN);
        return draw;
    }

    @Override
    public Draw createNextDraw(int previousDrawNumber) {
        Draw draw = new Draw();
        draw.setDrawNumber(previousDrawNumber + 1);
        draw.setDrawDate(LocalDateTime.now().plusHours(2)); // 1 day later,10 min later
        draw.setStatus(DrawStatus.DRAW_OPEN);
        return draw;
    }
}
