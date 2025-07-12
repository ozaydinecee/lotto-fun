package com.project.lottofun.initializer;

import com.project.lottofun.factory.DrawFactory;
import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.repository.DrawRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DrawInitializer {
    private final DrawRepository drawRepository;
    private final DrawFactory drawFactory;

    public DrawInitializer(DrawRepository drawRepository, DrawFactory drawFactory) {
        this.drawRepository = drawRepository;
        this.drawFactory = drawFactory;
    }

    @PostConstruct
    public void init() {
        if (drawRepository.count() == 0) {
            Draw initialDraw = drawFactory.createInitialDraw(); // ex: drawId = 1, drawDate = now.plusMinutes(5)
            drawRepository.save(initialDraw);
        }
    }
}
