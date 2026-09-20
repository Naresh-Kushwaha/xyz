package com.navratri.app.event.dto;

import com.navratri.app.activity.ActivityType;

public record JoinEventRequest(
        boolean lookingForCompanions,
        ActivityType companionActivity
) {
}
