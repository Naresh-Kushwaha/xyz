package com.navratri.app.matching.dto;

import jakarta.validation.constraints.NotNull;

public record SendInterestRequest(@NotNull Long theirCompanionRequestId) {
}
