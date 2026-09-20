package com.navratri.app.chat.repository;

import com.navratri.app.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("select c from Conversation c where c.userA.id = :userId or c.userB.id = :userId order by c.createdAt desc")
    List<Conversation> findAllForUser(@Param("userId") Long userId);
}
