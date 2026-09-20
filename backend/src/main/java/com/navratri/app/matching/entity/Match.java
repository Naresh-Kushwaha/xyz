package com.navratri.app.matching.entity;

import com.navratri.app.chat.entity.Conversation;
import com.navratri.app.companion.entity.CompanionRequest;
import com.navratri.app.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * A connection between two people's companion requests. Created PENDING when
 * requesterUser expresses interest in recipientUser's request; becomes ACCEPTED
 * (and spins up a Conversation) or DECLINED when recipientUser responds.
 */
@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_user_id")
    private User requesterUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_user_id")
    private User recipientUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_request_id")
    private CompanionRequest recipientRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatchStatus status = MatchStatus.PENDING;

    // Human-readable reasons, stored as a simple pipe-delimited string for the MVP,
    // e.g. "Same event|Both interested in Garba|Both available between 8 and 10 PM".
    @Column(length = 500)
    private String matchReasons;

    private Integer matchScore;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
