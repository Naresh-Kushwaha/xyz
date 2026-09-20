package com.navratri.app.safety.controller;

import com.navratri.app.common.ApiResponse;
import com.navratri.app.common.PageResponse;
import com.navratri.app.safety.dto.ReportDto;
import com.navratri.app.safety.dto.UpdateReportStatusRequest;
import com.navratri.app.safety.entity.ReportStatus;
import com.navratri.app.safety.service.SafetyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

/**
 * Minimal moderation surface -- list reports, change their status, suspend a user.
 * A full admin UI (per section 19 of the spec) is Phase 2; these endpoints are
 * usable today from a REST client or a future admin frontend.
 * Access is restricted to ROLE_ADMIN in SecurityConfig (/api/admin/**).
 */
@RestController
@RequestMapping("/api/admin/safety")
@RequiredArgsConstructor
public class AdminSafetyController {

    private final SafetyService safetyService;

    @GetMapping("/reports")
    public ApiResponse<PageResponse<ReportDto>> listReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(PageResponse.from(safetyService.listReports(status, PageRequest.of(page, size))));
    }

    @PatchMapping("/reports/{id}")
    public ApiResponse<ReportDto> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateReportStatusRequest request) {
        return ApiResponse.ok(safetyService.updateReportStatus(id, request.status()));
    }

    @PostMapping("/users/{userId}/suspend")
    public ApiResponse<Void> suspend(@PathVariable Long userId) {
        safetyService.suspendUser(userId);
        return ApiResponse.message("User suspended");
    }
}
