package com.bobocode.participants.dto;

public record PersonStatsResponse(
        String firstName,
        String lastName,
        int trainingDaysPerWeek,
        int minutesPerTrainingDay
) {
}
