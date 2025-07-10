package com.project.lottofun.service.impl;

import com.project.lottofun.factory.DrawFactory;
import com.project.lottofun.validator.DrawLifeCycleValidator;
import org.junit.jupiter.api.BeforeEach;

import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.enums.DrawStatus;
import com.project.lottofun.model.enums.PrizeType;
import com.project.lottofun.model.enums.TicketStatus;
import com.project.lottofun.repository.DrawRepository;
import com.project.lottofun.repository.TicketRepository;
import com.project.lottofun.service.strategy.PrizeCalculationStrategy;
import com.project.lottofun.service.strategy.WinnerSelectionStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
class DrawServiceImplTest {
    @Mock private DrawRepository drawRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private PrizeCalculationStrategy prizeStrategy;
    @Mock private WinnerSelectionStrategy winnerSelectionStrategy;
    @Mock private DrawFactory drawFactory;
    @Mock private DrawLifeCycleValidator drawLifeCycleValidator;

    private DrawServiceImpl drawService;

    private Draw dueDraw;
    private Draw nextDraw;
    private Set<Integer> winningNumbers;

    @BeforeEach
    void setUp() {
        drawService = new DrawServiceImpl(drawRepository, ticketRepository, prizeStrategy, winnerSelectionStrategy, drawFactory, drawLifeCycleValidator);

        dueDraw = new Draw();
        dueDraw.setId(1L);
        dueDraw.setDrawNumber(1);
        dueDraw.setDrawDate(LocalDateTime.now().minusHours(2));
        dueDraw.setStatus(DrawStatus.DRAW_OPEN);

        nextDraw = new Draw();
        nextDraw.setDrawNumber(2);
        nextDraw.setDrawDate(LocalDateTime.now().plusDays(1));
        nextDraw.setStatus(DrawStatus.DRAW_OPEN);

        winningNumbers = Set.of(1, 2, 3, 4, 5);
    }

    @Test
    void shouldExecuteDrawIfDueSuccessfully() {
        Ticket ticket = new Ticket();
        ticket.setSelectedNumbers(Set.of(1, 2, 3, 10, 11));
        ticket.setStatus(TicketStatus.PENDING);

        when(drawRepository.findFirstByDrawDateBeforeAndStatus(any(), eq(DrawStatus.DRAW_OPEN)))
                .thenReturn(Optional.of(dueDraw));
        doNothing().when(drawLifeCycleValidator).validateForExecution(any());
        when(winnerSelectionStrategy.selectWinningNumbers()).thenReturn(winningNumbers);
        when(ticketRepository.findAllByDraw(any())).thenReturn(List.of(ticket));
        when(prizeStrategy.calculatePrize(3)).thenReturn(PrizeType.MEDIUM);
        when(prizeStrategy.calculatePrizeAmount(PrizeType.MEDIUM)).thenReturn(new BigDecimal("1000"));
        when(drawFactory.createNextDraw(dueDraw.getDrawNumber())).thenReturn(nextDraw);

        assertThatCode(() -> drawService.executeDrawIfDue())
                .doesNotThrowAnyException();

        verify(drawRepository, atLeastOnce()).save(any(Draw.class));
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void shouldDoNothingWhenNoDueDraw() {
        when(drawRepository.findFirstByDrawDateBeforeAndStatus(any(), eq(DrawStatus.DRAW_OPEN)))
                .thenReturn(Optional.empty());

        drawService.executeDrawIfDue();

        verify(drawRepository, never()).save(any());
        verify(ticketRepository, never()).save(any());
    }
}
