package com.navratri.app.event.dto;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.event.entity.Event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record EventDto(
        Long id,
        String name,
        String description,
        Instant startTime,
        Instant endTime,
        String approximateLocation,
        Double latitude,
        Double longitude,
        BigDecimal entryFee,
        String organizerName,
        Set<ActivityType> activities,
        boolean featured,
        long attendingCount,
        long lookingForCompanionsCount
) {
    public static EventDto from(Event e, long attendingCount, long lookingForCompanionsCount) {
        return new EventDto(
                e.getId(), e.getName(), e.getDescription(), e.getStartTime(), e.getEndTime(),
                e.getApproximateLocation(), e.getLatitude(), e.getLongitude(), e.getEntryFee(),
                e.getOrganizer() != null ? e.getOrganizer().getEmail() : null,
                e.getActivities(), e.isFeatured(), attendingCount, lookingForCompanionsCount
        );
    }
}
