package com.project.lottofun.scheduler;


import com.project.lottofun.service.interfaces.DrawService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DrawSchedulerTest {

    @Mock
    private DrawService drawService;

    @InjectMocks
    private DrawScheduler drawScheduler;

    @Test
    void shouldCallDrawServiceWhenSchedulerRuns() {
        // when
        drawScheduler.handleDrawExecution();

        // then
        verify(drawService, times(1)).executeDrawIfDue();
    }
}