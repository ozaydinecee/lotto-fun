package com.project.lottofun.service.interfaces;

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

}
