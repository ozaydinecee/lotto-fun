package com.project.lottofun.service.impl;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.TicketPurchaseRequest;
import com.project.lottofun.model.dto.TicketResponse;
import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.model.enums.DrawStatus;
import com.project.lottofun.model.enums.PrizeType;
import com.project.lottofun.model.enums.TicketStatus;
import com.project.lottofun.repository.DrawRepository;
import com.project.lottofun.repository.TicketRepository;
import com.project.lottofun.repository.UserRepository;
import com.project.lottofun.service.interfaces.DrawService;
import com.project.lottofun.validator.DrawLifeCycleValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private DrawRepository drawRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private DrawLifeCycleValidator drawLifecycleValidator;

    @Mock
    private DrawService drawService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private User testUser;
    private Draw testDraw;

    @BeforeEach
    void setup() {
        testUser = User.builder()
                .id(1L)
                .username("john")
                .balance(new BigDecimal("100.00"))
                .build();

        testDraw = Draw.builder()
                .id(1L)
                .drawNumber(1)
                .drawDate(LocalDateTime.now().plusDays(1))
                .status(DrawStatus.DRAW_OPEN)
                .build();
    }

    @Test
    void purchaseTicket_validRequest_successfulPurchase() {
        Set<Integer> numbers = Set.of(5, 12, 23, 37, 48);
        TicketPurchaseRequest request = new TicketPurchaseRequest(numbers);

        when(drawService.getActiveDrawForPurchase()).thenReturn(testDraw);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Mockito.doNothing().when(drawLifecycleValidator).validateForTicketPurchase(any());

        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));


        when(ticketRepository.save(any())).thenAnswer(inv -> {
            return Ticket.builder()
                    .id(1L)
                    .user(testUser)
                    .draw(testDraw)
                    .selectedNumbers(numbers)
                    .purchaseTime(LocalDateTime.now())
                    .status(TicketStatus.PENDING)
                    .prize(PrizeType.NO_PRIZE)
                    .build();
        });

        ApiResponse<TicketResponse> response = ticketService.purchaseTicket(1L, request);

        assertTrue(response.isSuccess());
        assertEquals("Ticket purchased successfully", response.getMessage());
        assertEquals(numbers, response.getData().getSelectedNumbers());
        assertEquals(TicketStatus.PENDING.name(), response.getData().getStatus());
      //  assertEquals(PrizeType.NO_PRIZE, response.getData().getPrizeAmount().compareTo(BigDecimal.ZERO) == 0 ? PrizeType.NO_PRIZE : response.getData().getPrize());
    }


    @Test
    void purchaseTicket_insufficientBalance_throwsException() {
        testUser.setBalance(new BigDecimal("5.00")); // < 10

        when(drawService.getActiveDrawForPurchase()).thenReturn(testDraw);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5);
        TicketPurchaseRequest request = new TicketPurchaseRequest(numbers);

        assertThrows(IllegalStateException.class, () -> {
            ticketService.purchaseTicket(1L, request);
        });
    }

    @Test
    void purchaseTicket_invalidNumbers_throwsException() {
        Set<Integer> invalid = Set.of(1, 2, 3, 4); // less than 5
        TicketPurchaseRequest request = new TicketPurchaseRequest(invalid);

        when(drawService.getActiveDrawForPurchase()).thenReturn(testDraw);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.purchaseTicket(1L, request);
        });
    }

    @Test
    void getTicketsForUser_validUser_returnsTickets() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .user(testUser)
                .draw(testDraw)
                .selectedNumbers(Set.of(5, 10, 15, 20, 25))
                .status(TicketStatus.WON)
                .prizeAmount(new BigDecimal("100"))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(ticketRepository.findAllByUser(testUser)).thenReturn(List.of(ticket));

        ApiResponse<List<TicketResponse>> response = ticketService.getTicketsForUser(1L);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(ticket.getSelectedNumbers(), response.getData().get(0).getSelectedNumbers());
        assertEquals("WON", response.getData().get(0).getStatus());
    }

    @Test
    void getTicketsByDraw_validDrawNumber_returnsTickets() {
        Ticket ticket = Ticket.builder()
                .id(2L)
                .user(testUser)
                .draw(testDraw)
                .selectedNumbers(Set.of(2, 4, 6, 8, 10))
                .status(TicketStatus.NOT_WON)
                .prizeAmount(BigDecimal.ZERO)
                .build();

        when(drawRepository.findByDrawNumber(1)).thenReturn(Optional.of(testDraw));
        when(ticketRepository.findAllByDraw(testDraw)).thenReturn(List.of(ticket));

        ApiResponse<List<TicketResponse>> response = ticketService.getTicketsByDraw(1);

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(ticket.getSelectedNumbers(), response.getData().get(0).getSelectedNumbers());
        assertEquals("NOT_WON", response.getData().get(0).getStatus());
    }

}
