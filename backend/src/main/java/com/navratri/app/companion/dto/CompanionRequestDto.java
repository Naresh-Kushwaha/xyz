package com.navratri.app.companion.dto;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.companion.entity.CompanionRequest;
import com.navratri.app.companion.entity.CompanionRequestStatus;

import java.time.Instant;

public record CompanionRequestDto(
        Long id,
        Long userId,
        String userFirstName,
        Long eventId,
        String eventName,
        ActivityType activity,
        Instant plannedTime,
        String approximateArea,
        int companionsNeeded,
        String message,
        CompanionRequestStatus status,
        Instant createdAt
) {
    public static CompanionRequestDto from(CompanionRequest r) {
        return new CompanionRequestDto(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getProfile() != null ? r.getUser().getProfile().getFirstName() : null,
                r.getEvent() != null ? r.getEvent().getId() : null,
                r.getEvent() != null ? r.getEvent().getName() : null,
                r.getActivity(),
                r.getPlannedTime(),
                r.getApproximateArea(),
                r.getCompanionsNeeded(),
                r.getMessage(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}
