package com.navratri.app.matching.repository;

import com.navratri.app.matching.entity.Match;
import com.navratri.app.matching.entity.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("""
            select m from Match m
            where m.status = :status
              and (m.requesterUser.id = :userId or m.recipientUser.id = :userId)
            order by m.createdAt desc
            """)
    List<Match> findAllForUserByStatus(@Param("userId") Long userId, @Param("status") MatchStatus status);

    @Query("""
            select m from Match m
            where m.recipientUser.id = :userId and m.status = 'PENDING'
            order by m.createdAt desc
            """)
    List<Match> findPendingForRecipient(@Param("userId") Long userId);

    Optional<Match> findByRequesterUser_IdAndRecipientRequest_Id(Long requesterUserId, Long recipientRequestId);
}
