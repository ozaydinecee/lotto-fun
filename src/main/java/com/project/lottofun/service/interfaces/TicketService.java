package com.project.lottofun.service.interfaces;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.TicketPurchaseRequest;
import com.project.lottofun.model.dto.TicketResponse;

import java.util.List;

public interface TicketService {
    ApiResponse<TicketResponse> purchaseTicket(Long userId, TicketPurchaseRequest request);
    ApiResponse<List<TicketResponse>> getTicketsForUser(Long userId);

    ApiResponse<List<TicketResponse>> getTicketsByDraw(Integer drawNumber);
    ApiResponse<List<TicketResponse>> getTicketsForActiveDraw(Long userId);
}
