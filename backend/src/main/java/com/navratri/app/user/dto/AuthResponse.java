package com.navratri.app.user.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        ProfileDto profile
) {
}
