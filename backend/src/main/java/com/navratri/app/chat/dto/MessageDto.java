package com.navratri.app.chat.dto;

import com.navratri.app.chat.entity.Message;

import java.time.Instant;

public record MessageDto(
        Long id,
        Long conversationId,
        Long senderId,
        String senderFirstName,
        String content,
        boolean read,
        Instant createdAt
) {
    public static MessageDto from(Message m) {
        return new MessageDto(
                m.getId(),
                m.getConversation().getId(),
                m.getSender().getId(),
                m.getSender().getProfile() != null ? m.getSender().getProfile().getFirstName() : null,
                m.getContent(),
                m.isRead(),
                m.getCreatedAt()
        );
    }
}
