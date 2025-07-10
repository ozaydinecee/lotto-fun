package com.project.lottofun.service.interfaces;

import com.project.lottofun.model.dto.ApiResponse;
import com.project.lottofun.model.dto.DrawResponse;
import com.project.lottofun.model.entity.Draw;

public interface DrawService {

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

    Draw getActiveDraw();

}
