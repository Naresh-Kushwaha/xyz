package com.navratri.app.companion.controller;

import com.navratri.app.common.ApiResponse;
import com.navratri.app.companion.dto.CompanionRequestDto;
import com.navratri.app.companion.dto.CreateCompanionRequestDto;
import com.navratri.app.companion.service.CompanionRequestService;
import com.navratri.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companion-requests")
@RequiredArgsConstructor
public class CompanionRequestController {

    private final CompanionRequestService companionRequestService;

    @PostMapping
    public ApiResponse<CompanionRequestDto> create(@AuthenticationPrincipal UserPrincipal principal,
                                                     @Valid @RequestBody CreateCompanionRequestDto dto) {
        return ApiResponse.ok("Companion request created", companionRequestService.create(principal.getId(), dto));
    }

    @GetMapping("/mine")
    public ApiResponse<List<CompanionRequestDto>> mine(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(companionRequestService.myRequests(principal.getId()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        companionRequestService.cancel(principal.getId(), id);
        return ApiResponse.message("Request cancelled");
    }
}
