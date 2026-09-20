package com.navratri.app.safety.service;

import com.navratri.app.common.exceptions.BadRequestException;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.safety.dto.BlockedUserDto;
import com.navratri.app.safety.dto.CreateReportRequest;
import com.navratri.app.safety.dto.ReportDto;
import com.navratri.app.safety.entity.BlockedUser;
import com.navratri.app.safety.entity.Report;
import com.navratri.app.safety.entity.ReportStatus;
import com.navratri.app.safety.repository.BlockedUserRepository;
import com.navratri.app.safety.repository.ReportRepository;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SafetyService {

    private final BlockedUserRepository blockedUserRepository;
    private final ReportRepository reportRepository;
    private final UserService userService;

    @Transactional
    public void blockUser(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new BadRequestException("You can't block yourself");
        }
        if (blockedUserRepository.existsByBlocker_IdAndBlocked_Id(blockerId, blockedId)) {
            return; // already blocked, nothing to do
        }

        User blocker = userService.getById(blockerId);
        User blocked = userService.getById(blockedId);

        blockedUserRepository.save(BlockedUser.builder().blocker(blocker).blocked(blocked).build());
    }

    @Transactional
    public void unblockUser(Long blockerId, Long blockedId) {
        blockedUserRepository.deleteByBlocker_IdAndBlocked_Id(blockerId, blockedId);
    }

    public boolean isBlocked(Long blockerId, Long blockedId) {
        return blockedUserRepository.existsByBlocker_IdAndBlocked_Id(blockerId, blockedId);
    }

    @Transactional(readOnly = true)
    public List<BlockedUserDto> listBlocked(Long blockerId) {
        return blockedUserRepository.findByBlocker_Id(blockerId).stream()
                .map(BlockedUserDto::from)
                .toList();
    }

    @Transactional
    public ReportDto submitReport(Long reporterId, CreateReportRequest request) {
        User reporter = userService.getById(reporterId);
        User reportedUser = userService.getById(request.reportedUserId());

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reportedMessageId(request.reportedMessageId())
                .reason(request.reason())
                .details(request.details())
                .build();

        return ReportDto.from(reportRepository.save(report));
    }

    // --- Admin-facing operations ---

    @Transactional(readOnly = true)
    public Page<ReportDto> listReports(ReportStatus status, Pageable pageable) {
        Page<Report> page = status != null
                ? reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                : reportRepository.findAllByOrderByCreatedAtDesc(pageable);
        return page.map(ReportDto::from);
    }

    @Transactional
    public ReportDto updateReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(status);
        return ReportDto.from(reportRepository.save(report));
    }

    @Transactional
    public void suspendUser(Long userId) {
        User user = userService.getById(userId);
        user.setSuspended(true);
        userService.save(user);
    }
}
