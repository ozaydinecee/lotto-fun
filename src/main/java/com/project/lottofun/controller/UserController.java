package com.project.lottofun.controller;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.UserLoginRequest;
import com.project.lottofun.model.dto.UserRegisterRequest;
import com.project.lottofun.model.dto.UserResponse;
import com.project.lottofun.model.entity.User;
import com.project.lottofun.service.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.registerAndRespond(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.loginAndRespond(request));
    }
    @GetMapping("/{userId}/details")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails(@PathVariable Long userId) {
        ApiResponse<UserResponse> response = userService.getUserDetails(userId);
        return ResponseEntity.ok(response);
    }

}
