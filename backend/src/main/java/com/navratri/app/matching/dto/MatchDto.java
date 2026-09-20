package com.navratri.app.matching.dto;

import com.navratri.app.matching.entity.Match;
import com.navratri.app.matching.entity.MatchStatus;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public record MatchDto(
        Long id,
        Long otherUserId,
        String otherUserFirstName,
        MatchStatus status,
        List<String> reasons,
        Long conversationId,
        Instant createdAt
) {
    public static MatchDto from(Match m, Long viewingUserId) {
        boolean viewerIsRequester = m.getRequesterUser().getId().equals(viewingUserId);
        var otherUser = viewerIsRequester ? m.getRecipientUser() : m.getRequesterUser();

        return new MatchDto(
                m.getId(),
                otherUser.getId(),
                otherUser.getProfile() != null ? otherUser.getProfile().getFirstName() : null,
                m.getStatus(),
                m.getMatchReasons() != null ? Arrays.asList(m.getMatchReasons().split("\\|")) : List.of(),
                m.getConversation() != null ? m.getConversation().getId() : null,
                m.getCreatedAt()
        );
    }
}
