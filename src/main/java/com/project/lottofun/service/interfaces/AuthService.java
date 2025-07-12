package com.project.lottofun.service.interfaces;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.UserLoginRequest;
import com.project.lottofun.model.dto.UserRegisterRequest;
import com.project.lottofun.model.dto.UserResponse;

public interface AuthService {
    ApiResponse<UserResponse> register(UserRegisterRequest request);
    ApiResponse<UserResponse>  login(UserLoginRequest request);

}
