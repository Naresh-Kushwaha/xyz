package com.navratri.app.group.service;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.common.PageResponse;
import com.navratri.app.common.exceptions.BadRequestException;
import com.navratri.app.common.exceptions.ForbiddenException;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.event.entity.Event;
import com.navratri.app.event.repository.EventRepository;
import com.navratri.app.group.dto.CreateGroupRequest;
import com.navratri.app.group.dto.GroupDto;
import com.navratri.app.group.dto.GroupMemberDto;
import com.navratri.app.group.dto.UpdateGroupRequest;
import com.navratri.app.group.entity.GroupMember;
import com.navratri.app.group.entity.GroupMemberRole;
import com.navratri.app.group.entity.GroupMemberStatus;
import com.navratri.app.group.entity.NavratriGroup;
import com.navratri.app.group.repository.GroupMemberRepository;
import com.navratri.app.group.repository.GroupMessageRepository;
import com.navratri.app.group.repository.GroupRepository;
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
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    @Transactional
    public GroupDto create(Long ownerId, CreateGroupRequest request) {
        User owner = userService.getById(ownerId);

        Event event = null;
        if (request.eventId() != null) {
            event = eventRepository.findById(request.eventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        }

        NavratriGroup group = NavratriGroup.builder()
                .name(request.name())
                .description(request.description())
                .event(event)
                .activities(request.activities())
                .openSlots(request.openSlots())
                .owner(owner)
                .build();
        group = groupRepository.save(group);

        GroupMember ownerMembership = GroupMember.builder()
                .group(group).user(owner)
                .role(GroupMemberRole.OWNER)
                .status(GroupMemberStatus.APPROVED)
                .build();
        groupMemberRepository.save(ownerMembership);

        return toDto(group, ownerId);
    }

    @Transactional(readOnly = true)
    public PageResponse<GroupDto> search(ActivityType activity, Long viewerId, Pageable pageable) {
        Page<NavratriGroup> page = groupRepository.search(activity, pageable);
        return PageResponse.from(page.map(g -> toDto(g, viewerId)));
    }

    @Transactional(readOnly = true)
    public GroupDto getById(Long groupId, Long viewerId) {
        return toDto(findGroup(groupId), viewerId);
    }

    @Transactional(readOnly = true)
    public List<GroupDto> myGroups(Long userId) {
        return groupMemberRepository.findByUser_IdAndStatus(userId, GroupMemberStatus.APPROVED).stream()
                .map(GroupMember::getGroup)
                .map(g -> toDto(g, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GroupMemberDto> members(Long groupId) {
        return groupMemberRepository.findByGroup_IdAndStatus(groupId, GroupMemberStatus.APPROVED).stream()
                .map(GroupMemberDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<GroupMemberDto> pendingMembers(Long groupId, Long ownerId) {
        NavratriGroup group = findGroup(groupId);
        if (!group.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only the group owner can view pending requests");
        }

        return groupMemberRepository.findByGroup_IdAndStatus(groupId, GroupMemberStatus.PENDING).stream()
                .map(GroupMemberDto::from).toList();
    }

    @Transactional
    public GroupMemberDto requestToJoin(Long groupId, Long userId) {
        NavratriGroup group = findGroup(groupId);
        User user = userService.getById(userId);

        groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId).ifPresent(existing -> {
            throw new BadRequestException("You've already requested to join this group");
        });

        GroupMember member = GroupMember.builder()
                .group(group).user(user)
                .role(GroupMemberRole.MEMBER)
                .status(GroupMemberStatus.PENDING)
                .build();

        return GroupMemberDto.from(groupMemberRepository.save(member));
    }

    @Transactional
    public GroupMemberDto decideMembership(Long groupId, Long ownerId, Long memberUserId, boolean approve) {
        NavratriGroup group = findGroup(groupId);
        if (!group.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only the group owner can approve or reject members");
        }

        GroupMember member = groupMemberRepository.findByGroup_IdAndUser_Id(groupId, memberUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership request not found"));

        long approvedCount = groupMemberRepository.countByGroup_IdAndStatus(groupId, GroupMemberStatus.APPROVED);
        if (approve && approvedCount >= group.getOpenSlots() + 1) { // +1 for the owner
            throw new BadRequestException("This group is already full");
        }

        member.setStatus(approve ? GroupMemberStatus.APPROVED : GroupMemberStatus.REJECTED);
        return GroupMemberDto.from(groupMemberRepository.save(member));
    }

    @Transactional
    public GroupDto update(Long groupId, Long ownerId, UpdateGroupRequest request) {
        NavratriGroup group = findGroup(groupId);
        if (!group.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only the group owner can edit this group");
        }

        if (request.name() != null) group.setName(request.name());
        if (request.description() != null) group.setDescription(request.description());
        if (request.activities() != null && !request.activities().isEmpty()) group.setActivities(request.activities());
        if (request.openSlots() != null) group.setOpenSlots(request.openSlots());

        return toDto(groupRepository.save(group), ownerId);
    }

    @Transactional
    public void delete(Long groupId, Long ownerId) {
        NavratriGroup group = findGroup(groupId);
        if (!group.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only the group owner can delete this group");
        }

        // No cascading FKs are set up from NavratriGroup -> GroupMember/GroupMessage
        // (they're modeled as independent tables referencing the group by id), so we
        // clear those out explicitly before removing the group itself.
        groupMessageRepository.deleteByGroup_Id(groupId);
        groupMemberRepository.deleteByGroup_Id(groupId);
        groupRepository.delete(group);
    }

    @Transactional
    public void leave(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("You're not a member of this group"));

        if (member.getRole() == GroupMemberRole.OWNER) {
            throw new BadRequestException("The owner can't leave their own group; close it instead");
        }

        member.setStatus(GroupMemberStatus.LEFT);
        groupMemberRepository.save(member);
    }

    private NavratriGroup findGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    private GroupDto toDto(NavratriGroup group, Long viewerId) {
        long approvedCount = groupMemberRepository.countByGroup_IdAndStatus(group.getId(), GroupMemberStatus.APPROVED);
        GroupMemberStatus myStatus = viewerId == null ? null : groupMemberRepository
                .findByGroup_IdAndUser_Id(group.getId(), viewerId)
                .map(GroupMember::getStatus)
                .orElse(null);
        return GroupDto.from(group, approvedCount, myStatus);
    }
}
