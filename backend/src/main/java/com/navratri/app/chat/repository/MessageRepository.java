package com.navratri.app.chat.repository;

import com.navratri.app.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByConversation_IdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
}
