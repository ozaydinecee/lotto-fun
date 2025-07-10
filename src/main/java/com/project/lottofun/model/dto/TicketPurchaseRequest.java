package com.project.lottofun.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor // added for test classes
@NoArgsConstructor  // added for test classes
public class TicketPurchaseRequest {
    private Set<Integer> numbers;
}