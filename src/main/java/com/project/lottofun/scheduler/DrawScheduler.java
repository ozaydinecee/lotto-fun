package com.project.lottofun.scheduler;

import com.project.lottofun.service.interfaces.DrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DrawScheduler {

    private final DrawService drawService;

    /**
     * Executes draw process if any draw is due.
     * Runs every minute for demo purposes.
     */
    @Scheduled(fixedRate = 60000) // 60,000 ms = 1 minute
    public void handleDrawExecution() {
        log.info("Draw Execution job started...");
        drawService.executeDrawIfDue();
    }
}