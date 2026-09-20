package com.navratri.app.group.entity;

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
import java.util.HashSet;
import java.util.Set;

/**
 * Named "NavratriGroup" rather than "Group" to avoid colliding with the SQL keyword
 * and java.util/javax naming collisions.
 */
@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NavratriGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // optional

    @ElementCollection(targetClass = ActivityType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "group_activities", joinColumns = @JoinColumn(name = "group_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "activity")
    @Builder.Default
    private Set<ActivityType> activities = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private int openSlots = 1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
