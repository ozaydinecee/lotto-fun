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
    @PostMapping("/purchase/{userId}")
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
}
