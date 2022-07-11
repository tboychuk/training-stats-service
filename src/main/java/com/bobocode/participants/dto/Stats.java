package com.bobocode.participants.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public record Stats(
        List<TeamStats> teams,
        int personsCount,
        BigDecimal averageTrainingDaysPerWeek,
        BigDecimal averageMinutesPerTrainingDay
) {

    public static Stats emptyStats() {
        return new Stats(Collections.emptyList(), 0, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
