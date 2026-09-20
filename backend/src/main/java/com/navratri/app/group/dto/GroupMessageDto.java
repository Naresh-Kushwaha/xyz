package com.navratri.app.group.dto;

import com.navratri.app.group.entity.GroupMessage;

import java.time.Instant;

public record GroupMessageDto(
        Long id,
        Long groupId,
        Long senderId,
        String senderFirstName,
        String content,
        Instant createdAt
) {
    public static GroupMessageDto from(GroupMessage m) {
        return new GroupMessageDto(
                m.getId(),
                m.getGroup().getId(),
                m.getSender().getId(),
                m.getSender().getProfile() != null ? m.getSender().getProfile().getFirstName() : null,
                m.getContent(),
                m.getCreatedAt()
        );
    }
}
