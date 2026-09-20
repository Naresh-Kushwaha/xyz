package com.navratri.app.chat.dto;

import com.navratri.app.chat.entity.Conversation;

import java.time.Instant;

public record ConversationDto(
        Long id,
        Long otherUserId,
        String otherUserFirstName,
        boolean closed,
        Instant createdAt
) {
    public static ConversationDto from(Conversation c, Long viewingUserId) {
        var other = c.otherParticipant(viewingUserId);
        return new ConversationDto(
                c.getId(),
                other.getId(),
                other.getProfile() != null ? other.getProfile().getFirstName() : null,
                c.isClosed(),
                c.getCreatedAt()
        );
    }
}
