package com.navratri.app.safety.dto;

import com.navratri.app.safety.entity.Report;
import com.navratri.app.safety.entity.ReportReason;
import com.navratri.app.safety.entity.ReportStatus;

import java.time.Instant;

public record ReportDto(
        Long id,
        Long reporterId,
        Long reportedUserId,
        String reportedUserFirstName,
        Long reportedMessageId,
        ReportReason reason,
        String details,
        ReportStatus status,
        Instant createdAt
) {
    public static ReportDto from(Report r) {
        return new ReportDto(
                r.getId(), r.getReporter().getId(), r.getReportedUser().getId(),
                r.getReportedUser().getProfile() != null ? r.getReportedUser().getProfile().getFirstName() : null,
                r.getReportedMessageId(), r.getReason(), r.getDetails(), r.getStatus(), r.getCreatedAt()
        );
    }
}
