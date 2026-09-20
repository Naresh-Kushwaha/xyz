package com.navratri.app.group.dto;

import com.navratri.app.activity.ActivityType;
import jakarta.validation.constraints.Min;

import java.util.Set;

/**
 * All fields optional -- only non-null ones are applied (see GroupService.update()),
 * the same partial-update pattern used by UpdateProfileRequest.
 */
public record UpdateGroupRequest(
        String name,
        String description,
        Set<ActivityType> activities,
        @Min(1) Integer openSlots
) {
}
