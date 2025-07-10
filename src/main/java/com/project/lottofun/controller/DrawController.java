package com.project.lottofun.controller;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.DrawResponse;
import com.project.lottofun.service.interfaces.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/draws")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;
    @GetMapping("/active-draw")
    public ResponseEntity<ApiResponse<DrawResponse>> getActiveDraw() {
        ApiResponse<DrawResponse> response = drawService.getActiveDrawInfo();
        return ResponseEntity.ok(response);
    }
}
