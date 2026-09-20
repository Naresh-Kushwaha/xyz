package com.navratri.app.companion.entity;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.event.entity.Event;
import com.navratri.app.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * "I want a Garba companion" -- the seed of the matching system.
 * Either tied to a specific Event, or freestanding (activity + date/time + area only).
 */
@Entity
@Table(name = "companion_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // nullable

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activity;

    @Column(nullable = false)
    private Instant plannedTime;

    @Column(nullable = false)
    private String approximateArea;

    @Builder.Default
    private int companionsNeeded = 1;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CompanionRequestStatus status = CompanionRequestStatus.OPEN;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
