package com.navratri.app.group.websocket;

import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.group.dto.GroupMessageDto;
import com.navratri.app.group.service.GroupChatService;
import com.navratri.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * Real-time half of group chat, mirroring ChatWebSocketController for 1:1 chat.
 * Client flow (see frontend src/api/groups.js -> connectToGroupChat):
 *  1. Connect to /ws?token=<accessToken> via SockJS + STOMP (same connection used for 1:1 chat).
 *  2. SUBSCRIBE /topic/groups/{groupId}
 *  3. SEND /app/group.send/{groupId}  body: { "content": "..." }
 * GroupChatService enforces that only APPROVED members of the group can read or send.
 */
@Controller
@RequiredArgsConstructor
public class GroupChatWebSocketController {

    private final GroupChatService groupChatService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/group.send/{groupId}")
    public void sendMessage(@DestinationVariable Long groupId,
                             @Payload GroupStompMessage payload,
                             SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String email = sessionAttributes != null ? (String) sessionAttributes.get("email") : null;
        if (email == null) {
            return; // handshake wasn't authenticated -- silently drop.
        }

        Long senderId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();

        GroupMessageDto saved = groupChatService.sendMessage(groupId, senderId, payload.content());

        messagingTemplate.convertAndSend("/topic/groups/" + groupId, saved);
    }
}
