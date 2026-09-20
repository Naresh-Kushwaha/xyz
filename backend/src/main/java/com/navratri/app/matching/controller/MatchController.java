package com.navratri.app.matching.controller;

import com.navratri.app.common.ApiResponse;
import com.navratri.app.matching.dto.MatchCandidateDto;
import com.navratri.app.matching.dto.MatchDto;
import com.navratri.app.matching.dto.RespondToMatchRequest;
import com.navratri.app.matching.dto.SendInterestRequest;
import com.navratri.app.matching.service.MatchingService;
import com.navratri.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MatchController {

    private final MatchingService matchingService;

    @GetMapping("/companion-requests/{id}/candidates")
    public ApiResponse<List<MatchCandidateDto>> candidates(@AuthenticationPrincipal UserPrincipal principal,
                                                             @PathVariable("id") Long companionRequestId) {
        return ApiResponse.ok(matchingService.findCandidates(principal.getId(), companionRequestId));
    }

    @PostMapping("/matches/interest")
    public ApiResponse<MatchDto> sendInterest(@AuthenticationPrincipal UserPrincipal principal,
                                               @Valid @RequestBody SendInterestRequest request) {
        return ApiResponse.ok("Interest sent", matchingService.sendInterest(principal.getId(), request.theirCompanionRequestId()));
    }

    @GetMapping("/matches/pending")
    public ApiResponse<List<MatchDto>> pending(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(matchingService.myPendingRequests(principal.getId()));
    }

    @GetMapping("/matches")
    public ApiResponse<List<MatchDto>> myMatches(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(matchingService.myMatches(principal.getId()));
    }

    @PostMapping("/matches/{id}/respond")
    public ApiResponse<MatchDto> respond(@AuthenticationPrincipal UserPrincipal principal,
                                          @PathVariable Long id,
                                          @Valid @RequestBody RespondToMatchRequest request) {
        return ApiResponse.ok(matchingService.respond(principal.getId(), id, request.decision()));
    }
}
