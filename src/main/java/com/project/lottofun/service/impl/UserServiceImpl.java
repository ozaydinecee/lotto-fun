package com.project.lottofun.service.impl;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.UserLoginRequest;
import com.project.lottofun.model.dto.UserRegisterRequest;
import com.project.lottofun.model.dto.UserResponse;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.repository.TicketRepository;
import com.project.lottofun.repository.UserRepository;
import com.project.lottofun.service.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("100.00");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TicketRepository ticketRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public ApiResponse<UserResponse> registerAndRespond(UserRegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setBalance(new BigDecimal("100.00"));
        userRepository.save(user);

        return new ApiResponse<>(true, "User registered successfully", new UserResponse(user));
    }

    @Override
    public ApiResponse<UserResponse> loginAndRespond(UserLoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return new ApiResponse<>(true, "User login successful", new UserResponse(user));
    }

    @Override
    public ApiResponse<UserResponse> getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Ticket> tickets = ticketRepository.findAllByUser(user);

        return new ApiResponse<>(
                true,
                "User profile retrieved",
                new UserResponse(user, tickets)
        );
    }
}
