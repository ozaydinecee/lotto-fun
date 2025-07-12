package com.project.lottofun.service.impl;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.UserLoginRequest;
import com.project.lottofun.model.dto.UserRegisterRequest;
import com.project.lottofun.model.dto.UserResponse;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.repository.UserRepository;
import com.project.lottofun.security.JwtUtils;
import com.project.lottofun.service.interfaces.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AuthServiceImpl implements AuthService {
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("100.00");

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authManager,
                           JwtUtils jwtUtils,
                           PasswordEncoder passwordEncoder,
                           UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<UserResponse> register(UserRegisterRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });
        User u = new User();
        u.setUsername(request.getUsername());
        u.setPassword(passwordEncoder.encode(request.getPassword()));
        u.setBalance(DEFAULT_BALANCE);
        userRepository.save(u);
        UserResponse resp = new UserResponse(u);
        return new ApiResponse<>(true, "Registration successful", resp);
    }

    @Override
    public ApiResponse<UserResponse> login(UserLoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );
        UserDetails ud = (UserDetails) auth.getPrincipal();
        String token = jwtUtils.generateToken(ud);

        User user = userRepository.findByUsername(ud.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        UserResponse resp = new UserResponse(user);
        resp.setToken(token);
        return new ApiResponse<>(true, "Login successful", resp);
    }
}