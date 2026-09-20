package com.navratri.app.safety.controller;

import com.navratri.app.common.ApiResponse;
import com.navratri.app.safety.dto.BlockUserRequest;
import com.navratri.app.safety.dto.BlockedUserDto;
import com.navratri.app.safety.dto.CreateReportRequest;
import com.navratri.app.safety.dto.ReportDto;
import com.navratri.app.safety.service.SafetyService;
import com.navratri.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/safety")
@RequiredArgsConstructor
public class SafetyController {

    private final SafetyService safetyService;

    @PostMapping("/block")
    public ApiResponse<Void> block(@AuthenticationPrincipal UserPrincipal principal,
                                    @Valid @RequestBody BlockUserRequest request) {
        safetyService.blockUser(principal.getId(), request.userId());
        return ApiResponse.message("User blocked");
    }

    @PostMapping("/unblock")
    public ApiResponse<Void> unblock(@AuthenticationPrincipal UserPrincipal principal,
                                      @Valid @RequestBody BlockUserRequest request) {
        safetyService.unblockUser(principal.getId(), request.userId());
        return ApiResponse.message("User unblocked");
    }

    @GetMapping("/blocked")
    public ApiResponse<List<BlockedUserDto>> listBlocked(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(safetyService.listBlocked(principal.getId()));
    }

    @PostMapping("/reports")
    public ApiResponse<ReportDto> report(@AuthenticationPrincipal UserPrincipal principal,
                                          @Valid @RequestBody CreateReportRequest request) {
        return ApiResponse.ok("Report submitted, our team will review it", safetyService.submitReport(principal.getId(), request));
    }
}
