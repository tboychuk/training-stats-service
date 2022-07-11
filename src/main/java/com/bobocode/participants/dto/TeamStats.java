package com.bobocode.participants.dto;

import java.math.BigDecimal;
import java.util.List;

public record TeamStats(
        String team,
        List<PersonStatsResponse> teamMembers,
        int teamMembersCount,
        BigDecimal teamAverageDaysPerWeek,
        BigDecimal teamAverageMinutesPerTrainingDay
) {

}
