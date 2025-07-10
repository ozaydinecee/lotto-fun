package com.project.lottofun.service.strategy;

import com.project.lottofun.model.enums.PrizeType;

import java.math.BigDecimal;

public interface PrizeCalculationStrategy {
    PrizeType calculatePrize(int matchCount);
    BigDecimal calculatePrizeAmount(PrizeType prizeType);
}
