package com.navratri.app.group.controller;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.common.ApiResponse;
import com.navratri.app.common.PageResponse;
import com.navratri.app.group.dto.*;
import com.navratri.app.group.service.GroupChatService;
import com.navratri.app.group.service.GroupService;
import com.navratri.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final GroupChatService groupChatService;

    @PostMapping
    public ApiResponse<GroupDto> create(@AuthenticationPrincipal UserPrincipal principal,
                                         @Valid @RequestBody CreateGroupRequest request) {
        return ApiResponse.ok("Group created", groupService.create(principal.getId(), request));
    }

    @GetMapping
    public ApiResponse<PageResponse<GroupDto>> search(@AuthenticationPrincipal UserPrincipal principal,
                                                        @RequestParam(required = false) ActivityType activity,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size) {
        Long viewerId = principal != null ? principal.getId() : null;
        return ApiResponse.ok(groupService.search(activity, viewerId, PageRequest.of(page, size)));
    }

    @GetMapping("/mine")
    public ApiResponse<List<GroupDto>> mine(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(groupService.myGroups(principal.getId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<GroupDto> getById(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ApiResponse.ok(groupService.getById(id, principal.getId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<GroupDto> update(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable Long id,
                                         @Valid @RequestBody UpdateGroupRequest request) {
        return ApiResponse.ok("Group updated", groupService.update(id, principal.getId(), request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        groupService.delete(id, principal.getId());
        return ApiResponse.message("Group deleted");
    }

    @GetMapping("/{id}/members")
    public ApiResponse<List<GroupMemberDto>> members(@PathVariable Long id) {
        return ApiResponse.ok(groupService.members(id));
    }

    @GetMapping("/{id}/pending-members")
    public ApiResponse<List<GroupMemberDto>> pendingMembers(@AuthenticationPrincipal UserPrincipal principal,
                                                              @PathVariable Long id) {
        return ApiResponse.ok(groupService.pendingMembers(id, principal.getId()));
    }

    @PostMapping("/{id}/join")
    public ApiResponse<GroupMemberDto> join(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ApiResponse.ok("Request sent to the group owner", groupService.requestToJoin(id, principal.getId()));
    }

    @PostMapping("/{id}/members/{memberUserId}/decision")
    public ApiResponse<GroupMemberDto> decide(@AuthenticationPrincipal UserPrincipal principal,
                                               @PathVariable Long id, @PathVariable Long memberUserId,
                                               @RequestBody MembershipDecisionRequest request) {
        return ApiResponse.ok(groupService.decideMembership(id, principal.getId(), memberUserId, request.approve()));
    }

    @PostMapping("/{id}/leave")
    public ApiResponse<Void> leave(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        groupService.leave(id, principal.getId());
        return ApiResponse.message("You've left the group");
    }

    @GetMapping("/{id}/messages")
    public ApiResponse<List<GroupMessageDto>> messages(@AuthenticationPrincipal UserPrincipal principal,
                                                         @PathVariable Long id,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "50") int size) {
        return ApiResponse.ok(groupChatService.getMessages(id, principal.getId(), page, size));
    }

    @PostMapping("/{id}/messages")
    public ApiResponse<GroupMessageDto> sendMessage(@AuthenticationPrincipal UserPrincipal principal,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody SendGroupMessageRequest request) {
        return ApiResponse.ok(groupChatService.sendMessage(id, principal.getId(), request.content()));
    }
}
