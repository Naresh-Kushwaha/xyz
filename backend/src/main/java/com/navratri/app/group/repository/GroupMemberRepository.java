package com.navratri.app.group.repository;

import com.navratri.app.group.entity.GroupMember;
import com.navratri.app.group.entity.GroupMemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroup_Id(Long groupId);
    List<GroupMember> findByGroup_IdAndStatus(Long groupId, GroupMemberStatus status);
    List<GroupMember> findByUser_IdAndStatus(Long userId, GroupMemberStatus status);
    Optional<GroupMember> findByGroup_IdAndUser_Id(Long groupId, Long userId);
    long countByGroup_IdAndStatus(Long groupId, GroupMemberStatus status);
    void deleteByGroup_Id(Long groupId);
}
