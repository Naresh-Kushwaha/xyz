package com.navratri.app.group.dto;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.group.entity.GroupMemberStatus;
import com.navratri.app.group.entity.NavratriGroup;

import java.time.Instant;
import java.util.Set;

public record GroupDto(
        Long id,
        String name,
        String description,
        Long eventId,
        String eventName,
        Set<ActivityType> activities,
        int openSlots,
        long approvedMemberCount,
        Long ownerId,
        String ownerFirstName,
        GroupMemberStatus myStatus, // null if the viewer isn't a member
        Instant createdAt
) {
    public static GroupDto from(NavratriGroup g, long approvedMemberCount, GroupMemberStatus myStatus) {
        return new GroupDto(
                g.getId(), g.getName(), g.getDescription(),
                g.getEvent() != null ? g.getEvent().getId() : null,
                g.getEvent() != null ? g.getEvent().getName() : null,
                g.getActivities(), g.getOpenSlots(), approvedMemberCount,
                g.getOwner().getId(),
                g.getOwner().getProfile() != null ? g.getOwner().getProfile().getFirstName() : null,
                myStatus, g.getCreatedAt()
        );
    }
}
