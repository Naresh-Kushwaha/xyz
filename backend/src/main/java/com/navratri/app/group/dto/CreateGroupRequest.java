package com.navratri.app.group.dto;

import com.navratri.app.activity.ActivityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CreateGroupRequest(
        @NotBlank String name,
        String description,
        Long eventId,
        @NotEmpty Set<ActivityType> activities,
        @Min(1) int openSlots
) {
}
