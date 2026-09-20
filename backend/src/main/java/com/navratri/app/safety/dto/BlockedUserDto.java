package com.navratri.app.safety.dto;

import com.navratri.app.safety.entity.BlockedUser;

import java.time.Instant;

public record BlockedUserDto(Long userId, String firstName, Instant blockedAt) {
    public static BlockedUserDto from(BlockedUser b) {
        var user = b.getBlocked();
        return new BlockedUserDto(
                user.getId(),
                user.getProfile() != null ? user.getProfile().getFirstName() : null,
                b.getCreatedAt()
        );
    }
}
