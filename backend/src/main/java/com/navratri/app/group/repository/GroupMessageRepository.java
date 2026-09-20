package com.navratri.app.group.repository;

import com.navratri.app.group.entity.GroupMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    Page<GroupMessage> findByGroup_IdOrderByCreatedAtDesc(Long groupId, Pageable pageable);
    void deleteByGroup_Id(Long groupId);
}
