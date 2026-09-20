package com.navratri.app.event.dto;

import com.navratri.app.activity.ActivityType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CreateEventRequest(
        @NotBlank String name,
        String description,
        @NotNull @Future Instant startTime,
        @NotNull Instant endTime,
        @NotBlank String approximateLocation,
        Double latitude,
        Double longitude,
        BigDecimal entryFee,
        @NotEmpty Set<ActivityType> activities
) {
}
