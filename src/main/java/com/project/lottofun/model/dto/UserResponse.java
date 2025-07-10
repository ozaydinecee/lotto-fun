package com.project.lottofun.model.dto;

import com.project.lottofun.model.entity.User;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private BigDecimal balance;

    public UserResponse(Long id, String username, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.balance = user.getBalance();
    }
}
