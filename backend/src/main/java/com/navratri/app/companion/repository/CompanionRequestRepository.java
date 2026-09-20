package com.navratri.app.companion.repository;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.companion.entity.CompanionRequest;
import com.navratri.app.companion.entity.CompanionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanionRequestRepository extends JpaRepository<CompanionRequest, Long> {

    List<CompanionRequest> findByUser_IdOrderByCreatedAtDesc(Long userId);

    List<CompanionRequest> findByStatusAndActivityAndUser_IdNot(
            CompanionRequestStatus status, ActivityType activity, Long excludingUserId);

    List<CompanionRequest> findByStatusAndEvent_IdAndUser_IdNot(
            CompanionRequestStatus status, Long eventId, Long excludingUserId);
}
