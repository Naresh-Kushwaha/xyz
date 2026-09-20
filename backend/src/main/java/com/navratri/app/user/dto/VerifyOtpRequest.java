package com.navratri.app.user.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyOtpRequest(@NotBlank String phoneNumber, @NotBlank String code) {
}
