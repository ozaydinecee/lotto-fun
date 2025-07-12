package com.project.lottofun.service.strategy;


import com.project.lottofun.model.enums.PrizeType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DefaultPrizeCalculationStrategy implements PrizeCalculationStrategy {
    @Override
    public PrizeType calculatePrize(int matchCount) {
        return switch (matchCount) {
            case 5 -> PrizeType.JACKPOT;
            case 4 -> PrizeType.HIGH;
            case 3 -> PrizeType.MEDIUM;
            case 2 -> PrizeType.LOW;
            default -> PrizeType.NO_PRIZE;
        };
    }

    @Override
    /*
     * prize calculated as fixed amount
     * */
    public BigDecimal calculatePrizeAmount(PrizeType prizeType) {
        return switch (prizeType) {
            case JACKPOT -> new BigDecimal("1000000");
            case HIGH -> new BigDecimal("10000");
            case MEDIUM -> new BigDecimal("1000");
            case LOW -> new BigDecimal("100");
            default -> BigDecimal.ZERO;
        };
    }
}
