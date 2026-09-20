package com.navratri.app.group.service;

import com.navratri.app.common.exceptions.ForbiddenException;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.group.dto.GroupMessageDto;
import com.navratri.app.group.entity.GroupMember;
import com.navratri.app.group.entity.GroupMemberStatus;
import com.navratri.app.group.entity.GroupMessage;
import com.navratri.app.group.entity.NavratriGroup;
import com.navratri.app.group.repository.GroupMemberRepository;
import com.navratri.app.group.repository.GroupMessageRepository;
import com.navratri.app.group.repository.GroupRepository;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Group chat is one shared room per group, open to every APPROVED member
 * (the owner included -- they're auto-approved on group creation). Unlike
 * 1:1 chat, there's no accept/decline step: joining the group IS the gate.
 */
@Service
@RequiredArgsConstructor
public class GroupChatService {

    private final GroupMessageRepository groupMessageRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<GroupMessageDto> getMessages(Long groupId, Long userId, int page, int size) {
        requireApprovedMember(groupId, userId);

        List<GroupMessage> messages = groupMessageRepository
                .findByGroup_IdOrderByCreatedAtDesc(groupId, PageRequest.of(page, size))
                .getContent();

        return messages.stream()
                .sorted(Comparator.comparing(GroupMessage::getCreatedAt))
                .map(GroupMessageDto::from)
                .toList();
    }

    @Transactional
    public GroupMessageDto sendMessage(Long groupId, Long senderId, String content) {
        requireApprovedMember(groupId, senderId);

        NavratriGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        User sender = userService.getById(senderId);

        GroupMessage message = GroupMessage.builder()
                .group(group)
                .sender(sender)
                .content(content)
                .build();

        return GroupMessageDto.from(groupMessageRepository.save(message));
    }

    private void requireApprovedMember(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new ForbiddenException("You must be a member of this group to use its chat"));

        if (member.getStatus() != GroupMemberStatus.APPROVED) {
            throw new ForbiddenException("Your membership request hasn't been approved yet");
        }
    }
}
