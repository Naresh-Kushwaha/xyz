package com.navratri.app.matching.dto;

import com.navratri.app.companion.dto.CompanionRequestDto;

import java.util.List;

/**
 * A potential companion for one of my open requests, with a human-readable
 * explanation of why they were surfaced -- never the raw scoring internals.
 */
public record MatchCandidateDto(
        CompanionRequestDto theirRequest,
        int score,
        List<String> reasons
) {
}
