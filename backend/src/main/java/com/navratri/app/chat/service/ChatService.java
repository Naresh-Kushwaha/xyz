package com.navratri.app.chat.service;

import com.navratri.app.chat.dto.ConversationDto;
import com.navratri.app.chat.dto.MessageDto;
import com.navratri.app.chat.entity.Conversation;
import com.navratri.app.chat.entity.Message;
import com.navratri.app.chat.repository.ConversationRepository;
import com.navratri.app.chat.repository.MessageRepository;
import com.navratri.app.common.exceptions.ForbiddenException;
import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.safety.service.SafetyService;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final SafetyService safetyService;

    @Transactional(readOnly = true)
    public List<ConversationDto> myConversations(Long userId) {
        return conversationRepository.findAllForUser(userId).stream()
                .map(c -> ConversationDto.from(c, userId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessages(Long userId, Long conversationId, int page, int size) {
        Conversation conversation = getConversationForParticipant(userId, conversationId);
        List<Message> messages = messageRepository
                .findByConversation_IdOrderByCreatedAtDesc(conversation.getId(), PageRequest.of(page, size))
                .getContent();
        return messages.stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(MessageDto::from)
                .toList();
    }

    @Transactional
    public MessageDto sendMessage(Long senderId, Long conversationId, String content) {
        Conversation conversation = getConversationForParticipant(senderId, conversationId);

        if (conversation.isClosed()) {
            throw new ForbiddenException("This conversation has been closed");
        }

        Long otherUserId = conversation.otherParticipant(senderId).getId();
        if (safetyService.isBlocked(otherUserId, senderId) || safetyService.isBlocked(senderId, otherUserId)) {
            throw new ForbiddenException("You can't message this user");
        }

        User sender = userService.getById(senderId);
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .build();

        return MessageDto.from(messageRepository.save(message));
    }

    public Conversation getConversationForParticipant(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        if (!conversation.hasParticipant(userId)) {
            throw new ForbiddenException("You're not part of this conversation");
        }
        return conversation;
    }

    @Transactional
    public void leaveConversation(Long userId, Long conversationId) {
        Conversation conversation = getConversationForParticipant(userId, conversationId);
        conversation.setClosed(true);
        conversationRepository.save(conversation);
    }
}
