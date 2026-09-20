package com.navratri.app.safety.dto;

import com.navratri.app.safety.entity.ReportReason;
import jakarta.validation.constraints.NotNull;

public record CreateReportRequest(
        @NotNull Long reportedUserId,
        Long reportedMessageId,
        @NotNull ReportReason reason,
        String details
) {
}
