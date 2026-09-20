package com.navratri.app.event.repository;

import com.navratri.app.event.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    long countByEvent_Id(Long eventId);
    long countByEvent_IdAndLookingForCompanionsTrue(Long eventId);
    Optional<EventParticipant> findByEvent_IdAndUser_Id(Long eventId, Long userId);
    List<EventParticipant> findByEvent_IdAndLookingForCompanionsTrue(Long eventId);
}
