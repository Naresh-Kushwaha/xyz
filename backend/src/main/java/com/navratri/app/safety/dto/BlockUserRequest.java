package com.navratri.app.safety.dto;

import jakarta.validation.constraints.NotNull;

public record BlockUserRequest(@NotNull Long userId) {
}
