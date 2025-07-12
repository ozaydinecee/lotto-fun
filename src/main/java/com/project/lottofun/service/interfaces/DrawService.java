package com.project.lottofun.service.interfaces;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.DrawResponse;
import com.project.lottofun.model.entity.Draw;

import java.util.List;

public interface DrawService {
    ApiResponse<List<DrawResponse>> getAllDraws();
    ApiResponse<DrawResponse> getDrawByNumber(Integer drawNumber);
    /**
     * Execute draw if a scheduled draw is due.
     * This includes:
     * - marking draw as extracted
     * - generating winning numbers
     * - calculating match results
     * - distributing prizes
     * - creating next draw
     */
    void executeDrawIfDue();

    ApiResponse<DrawResponse> getActiveDrawInfo();
    /**
     * Returns the current draw for ticket purchase
     */
    Draw getActiveDrawForPurchase();

    /**
     * Returns the current draw with status DRAW_OPEN
     */
    Draw getActiveDraw();

    ApiResponse<List<DrawResponse>> getExtractedDraws();
}
