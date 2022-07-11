package com.bobocode.participants.dto;

import javax.validation.constraints.*;

public record PersonStatsRequest(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotEmpty
        @Pattern(
                regexp = "Petros|Hoverla|Blyznytsia|Breskul|Svydovets",
                message = "Must be either Petros, Hoverla, Blyznytsia, Breskul or Svydovets"
        )
        String team,

        @Min(0)
        @Max(7)
        int trainingDaysPerWeek,

        @Min(0)
        @Max(1440)
        int minutesPerTrainingDay
) {
}
