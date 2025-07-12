package com.project.lottofun.service.impl;

import com.project.lottofun.factory.DrawFactory;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrawServiceImplTest {
    @Mock private DrawRepository drawRepository;
    @Mock private UserRepository userRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private PrizeCalculationStrategy prizeStrategy;
    @Mock private WinnerSelectionStrategy winnerSelectionStrategy;
    @Mock private DrawFactory drawFactory;
    @Mock private DrawLifeCycleValidator drawLifeCycleValidator;

    private DrawServiceImpl drawService;

    private Draw dueDraw;
    private Draw nextDraw;
    private Set<Integer> winningNumbers;
    private  User user;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        drawService = new DrawServiceImpl(drawRepository, ticketRepository, prizeStrategy, winnerSelectionStrategy, drawFactory, drawLifeCycleValidator, userRepository);
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setBalance(BigDecimal.ZERO);

        ticket = new Ticket();
        ticket.setSelectedNumbers(Set.of(1, 2, 3, 10, 11));
        ticket.setStatus(TicketStatus.PENDING);
        ticket.setUser(user);

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
    @Test
    void shouldReturnActiveDrawForPurchase() {
        Draw draw = new Draw();
        draw.setDrawDate(LocalDateTime.now().plusMinutes(5));
        draw.setStatus(DrawStatus.DRAW_OPEN);

        when(drawRepository.findTopByStatusOrderByDrawDateAsc(DrawStatus.DRAW_OPEN))
                .thenReturn(Optional.of(draw));

        Draw result = drawService.getActiveDrawForPurchase();
        assertThat(result).isEqualTo(draw);
    }

    @Test
    void shouldThrowWhenActiveDrawDateIsInPast() {
        Draw draw = new Draw();
        draw.setDrawDate(LocalDateTime.now().minusMinutes(5));
        draw.setStatus(DrawStatus.DRAW_OPEN);

        when(drawRepository.findTopByStatusOrderByDrawDateAsc(DrawStatus.DRAW_OPEN))
                .thenReturn(Optional.of(draw));

        assertThatThrownBy(() -> drawService.getActiveDrawForPurchase())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Draw is closed");
    }
    @Test
    void shouldReturnActiveDraw() {
        Draw draw = new Draw();
        draw.setDrawDate(LocalDateTime.now().plusMinutes(10));
        draw.setStatus(DrawStatus.DRAW_OPEN);

        when(drawRepository.findTopByStatusOrderByDrawDateAsc(DrawStatus.DRAW_OPEN))
                .thenReturn(Optional.of(draw));

        Draw result = drawService.getActiveDraw();
        assertThat(result).isEqualTo(draw);
    }

    @Test
    void shouldThrowWhenNoActiveDraw() {
        when(drawRepository.findTopByStatusOrderByDrawDateAsc(DrawStatus.DRAW_OPEN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> drawService.getActiveDraw())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No active draw available");
    }
    @Test
    void shouldReturnDrawByNumber() {
        Draw draw = new Draw();
        draw.setDrawNumber(123);

        when(drawRepository.findByDrawNumber(123)).thenReturn(Optional.of(draw));

        var result = drawService.getDrawByNumber(123);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData().getDrawNumber()).isEqualTo(123);
    }

    @Test
    void shouldThrowWhenDrawNotFoundByNumber() {
        when(drawRepository.findByDrawNumber(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> drawService.getDrawByNumber(999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Draw not found");
    }
    @Test
    void shouldReturnExtractedDraws() {
        Draw d1 = new Draw(); d1.setDrawNumber(1); d1.setStatus(DrawStatus.DRAW_EXTRACTED);
        Draw d2 = new Draw(); d2.setDrawNumber(2); d2.setStatus(DrawStatus.DRAW_FINALIZED);

        when(drawRepository.findByStatusInOrderByDrawDateDesc(any()))
                .thenReturn(List.of(d1, d2));

        var result = drawService.getExtractedDraws();
        assertThat(result.getData()).hasSize(2);
    }

    @Test
    void shouldReturnAllDraws() {
        Draw d1 = new Draw(); d1.setDrawNumber(1);
        Draw d2 = new Draw(); d2.setDrawNumber(2);

        when(drawRepository.findAllByOrderByDrawDateDesc()).thenReturn(List.of(d1, d2));

        var result = drawService.getAllDraws();
        assertThat(result.getData()).hasSize(2);
    }

}
