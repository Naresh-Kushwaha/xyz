package com.navratri.app.chat.controller;

import com.navratri.app.chat.dto.ConversationDto;
import com.navratri.app.chat.dto.MessageDto;
import com.navratri.app.chat.dto.SendMessageRequest;
import com.navratri.app.chat.service.ChatService;
import com.navratri.app.common.ApiResponse;
import com.navratri.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints cover fetching history and sending a message over plain HTTP
 * (handy for a first working version, or for clients without WebSocket support).
 * For live delivery to the other participant, see ChatWebSocketController.
 */
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ApiResponse<List<ConversationDto>> myConversations(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(chatService.myConversations(principal.getId()));
    }

    @GetMapping("/{id}/messages")
    public ApiResponse<List<MessageDto>> messages(@AuthenticationPrincipal UserPrincipal principal,
                                                    @PathVariable Long id,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "50") int size) {
        return ApiResponse.ok(chatService.getMessages(principal.getId(), id, page, size));
    }

    @PostMapping("/{id}/messages")
    public ApiResponse<MessageDto> send(@AuthenticationPrincipal UserPrincipal principal,
                                         @PathVariable Long id,
                                         @Valid @RequestBody SendMessageRequest request) {
        return ApiResponse.ok(chatService.sendMessage(principal.getId(), id, request.content()));
    }

    @PostMapping("/{id}/leave")
    public ApiResponse<Void> leave(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        chatService.leaveConversation(principal.getId(), id);
        return ApiResponse.message("Conversation closed");
    }
}
