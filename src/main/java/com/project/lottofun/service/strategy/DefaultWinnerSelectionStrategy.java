package com.project.lottofun.service.strategy;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Component
public class DefaultWinnerSelectionStrategy implements WinnerSelectionStrategy{
    private static final int NUMBER_COUNT = 5;
    private static final int MAX_NUMBER = 49;

    @Override
    public Set<Integer> selectWinningNumbers() {
        Set<Integer> winningNumbers = new HashSet<>();
        SecureRandom random = new SecureRandom();

        while (winningNumbers.size() < NUMBER_COUNT) {
            winningNumbers.add(random.nextInt(MAX_NUMBER) + 1); // 1 to 49
        }

        return winningNumbers;
    }
}
