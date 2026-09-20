package com.navratri.app.event.entity;

import com.navratri.app.activity.ActivityType;
import com.navratri.app.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "event_participants", uniqueConstraints = {
        @UniqueConstraint(name = "uq_event_user", columnNames = {"event_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Whether this person is actively looking for a companion for this event.
    @Builder.Default
    private boolean lookingForCompanions = false;

    @Enumerated(EnumType.STRING)
    private ActivityType companionActivity;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant joinedAt;
}
