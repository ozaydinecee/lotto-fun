package com.project.lottofun.controller;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.DrawResponse;
import com.project.lottofun.service.interfaces.DrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/draws")
@RequiredArgsConstructor
public class DrawController {

    private final DrawService drawService;
    @GetMapping
    public ResponseEntity<ApiResponse<List<DrawResponse>>> getAllDraws() {
        ApiResponse<List<DrawResponse>> response = drawService.getAllDraws();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{drawNumber}")
    public ResponseEntity<ApiResponse<DrawResponse>> getDrawByNumber(@PathVariable Integer drawNumber) {
        ApiResponse<DrawResponse> response = drawService.getDrawByNumber(drawNumber);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<DrawResponse>> getActiveDraw() {
        ApiResponse<DrawResponse> response = drawService.getActiveDrawInfo();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<DrawResponse>>> getExtractedDraws() {
        ApiResponse<List<DrawResponse>> response = drawService.getExtractedDraws();
        return ResponseEntity.ok(response);
    }
}
