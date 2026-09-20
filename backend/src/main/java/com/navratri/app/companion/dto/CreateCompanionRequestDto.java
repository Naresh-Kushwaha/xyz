package com.navratri.app.companion.dto;

import com.navratri.app.activity.ActivityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateCompanionRequestDto(
        Long eventId, // optional
        @NotNull ActivityType activity,
        @NotNull Instant plannedTime,
        @NotBlank String approximateArea,
        @Min(1) int companionsNeeded,
        String message
) {
}
