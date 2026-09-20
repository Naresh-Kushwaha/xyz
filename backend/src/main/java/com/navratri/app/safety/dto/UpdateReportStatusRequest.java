package com.navratri.app.safety.dto;

import com.navratri.app.safety.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateReportStatusRequest(@NotNull ReportStatus status) {
}
