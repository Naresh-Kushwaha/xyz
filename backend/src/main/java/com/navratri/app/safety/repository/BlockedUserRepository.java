package com.navratri.app.safety.repository;

import com.navratri.app.safety.entity.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    boolean existsByBlocker_IdAndBlocked_Id(Long blockerId, Long blockedId);
    List<BlockedUser> findByBlocker_Id(Long blockerId);
    void deleteByBlocker_IdAndBlocked_Id(Long blockerId, Long blockedId);
}
