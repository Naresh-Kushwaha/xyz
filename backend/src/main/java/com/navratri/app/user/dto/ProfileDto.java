package com.navratri.app.user.dto;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.user.entity.GroupSizePreference;
import com.navratri.app.user.entity.Profile;

import java.util.Set;

/**
 * What other users (and the profile owner) can see about a person.
 * Deliberately omits email, phone number, and exact address.
 */
public record ProfileDto(
        Long userId,
        String firstName,
        String profilePictureUrl,
        Integer ageRangeMin,
        Integer ageRangeMax,
        String approximateArea,
        String bio,
        GroupSizePreference preferredGroupSize,
        Set<String> interests,
        Set<ActivityType> favoriteActivities,
        boolean verifiedBadge
) {
    public static ProfileDto from(Profile p) {
        return new ProfileDto(
                p.getId(), // Profile shares its primary key with User (see @MapsId), so this IS the user id
                p.getFirstName(),
                p.getProfilePictureUrl(),
                p.getAgeRangeMin(),
                p.getAgeRangeMax(),
                p.getApproximateArea(),
                p.getBio(),
                p.getPreferredGroupSize(),
                p.getInterests(),
                p.getFavoriteActivities(),
                p.isVerifiedBadge()
        );
    }
}
