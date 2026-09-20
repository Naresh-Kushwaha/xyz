package com.navratri.app.user.dto;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.user.entity.GroupSizePreference;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateProfileRequest(
        String firstName,
        String profilePictureUrl,
        Integer ageRangeMin,
        Integer ageRangeMax,
        String approximateArea,
        @Size(max = 500) String bio,
        GroupSizePreference preferredGroupSize,
        Set<String> interests,
        Set<ActivityType> favoriteActivities
) {
}
