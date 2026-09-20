package com.navratri.app.user.dto;

import jakarta.validation.constraints.NotBlank;

public record SendOtpRequest(@NotBlank String phoneNumber) {
}
