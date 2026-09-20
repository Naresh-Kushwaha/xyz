package com.navratri.app.matching.dto;

import com.navratri.app.matching.entity.MatchStatus;
import jakarta.validation.constraints.NotNull;

public record RespondToMatchRequest(@NotNull MatchStatus decision) {
}
