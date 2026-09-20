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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_events_start_time", columnList = "startTime")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Instant startTime;

    @Column(nullable = false)
    private Instant endTime;

    // Coarse locality, e.g. "Vijay Nagar, Indore" -- never a precise address/pin.
    @Column(nullable = false)
    private String approximateLocation;

    private Double latitude;
    private Double longitude;

    private BigDecimal entryFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @ElementCollection(targetClass = ActivityType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "event_activities", joinColumns = @JoinColumn(name = "event_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "activity")
    @Builder.Default
    private Set<ActivityType> activities = new HashSet<>();

    @Builder.Default
    private boolean featured = false;

    @Builder.Default
    private boolean cancelled = false;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
