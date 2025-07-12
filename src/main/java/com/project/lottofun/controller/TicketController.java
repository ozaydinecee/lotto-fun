package com.project.lottofun.controller;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.TicketPurchaseRequest;
import com.project.lottofun.model.dto.TicketResponse;
import com.project.lottofun.service.interfaces.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;


    /**
     * Allows a user to purchase a ticket for an active draw.
     */
    @PostMapping("/users/{userId}/purchase")
    public ResponseEntity<ApiResponse<TicketResponse>> purchaseTicket(
            @PathVariable Long userId,
            @RequestBody TicketPurchaseRequest request) {
        ApiResponse<TicketResponse> response = ticketService.purchaseTicket(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all tickets associated with a specific user.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsForUser(@PathVariable Long userId) {
        ApiResponse<List<TicketResponse>> response = ticketService.getTicketsForUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all tickets associated with a specific draw number.
     */
    @GetMapping("/draws/{drawNumber}")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsByDraw(@PathVariable Integer drawNumber) {
        ApiResponse<List<TicketResponse>> response = ticketService.getTicketsByDraw(drawNumber);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/users/{userId}/active-draw")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsForActiveDraw(@PathVariable Long userId) {
        ApiResponse<List<TicketResponse>> response = ticketService.getTicketsForActiveDraw(userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/draws/{drawNumber}/top-winners")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTopWinners(@PathVariable Integer drawNumber) {
        ApiResponse<List<TicketResponse>> response = ticketService.getTopWinnersByDraw(drawNumber);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/users/{userId}/draws/{drawNumber}")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getTicketsByUserAndDraw(
            @PathVariable Long userId,
            @PathVariable Integer drawNumber) {
        ApiResponse<List<TicketResponse>> response = ticketService.getTicketsByUserAndDraw(userId, drawNumber);
        return ResponseEntity.ok(response);
    }
}
