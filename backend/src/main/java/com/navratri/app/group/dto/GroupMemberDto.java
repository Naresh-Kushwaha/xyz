package com.navratri.app.group.dto;

import com.navratri.app.group.entity.GroupMember;
import com.navratri.app.group.entity.GroupMemberRole;
import com.navratri.app.group.entity.GroupMemberStatus;

public record GroupMemberDto(
        Long id,
        Long userId,
        String firstName,
        GroupMemberRole role,
        GroupMemberStatus status
) {
    public static GroupMemberDto from(GroupMember m) {
        return new GroupMemberDto(
                m.getId(), m.getUser().getId(),
                m.getUser().getProfile() != null ? m.getUser().getProfile().getFirstName() : null,
                m.getRole(), m.getStatus()
        );
    }
}
