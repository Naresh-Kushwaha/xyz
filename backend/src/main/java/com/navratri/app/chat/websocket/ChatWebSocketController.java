package com.navratri.app.chat.websocket;

import com.navratri.app.chat.dto.MessageDto;
import com.navratri.app.chat.service.ChatService;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
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
 * Real-time half of chat. The REST endpoints in ChatController read/write the same
 * data; this just fans a freshly-sent message out to whoever is subscribed to
 * /topic/conversations/{conversationId} at the moment it's sent.
 *
 * Client flow (see frontend src/api/chat.js):
 *  1. Connect to /ws?token=<accessToken> via SockJS + STOMP.
 *  2. SUBSCRIBE /topic/conversations/{conversationId}
 *  3. SEND /app/chat.send/{conversationId}  body: { "content": "..." }
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send/{conversationId}")
    public void sendMessage(@DestinationVariable Long conversationId,
                             @Payload StompChatMessage payload,
                             SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String email = sessionAttributes != null ? (String) sessionAttributes.get("email") : null;
        if (email == null) {
            return; // handshake wasn't authenticated -- silently drop.
        }

        Long senderId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getId();

        MessageDto saved = chatService.sendMessage(senderId, conversationId, payload.content());

        messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, saved);
    }
}
