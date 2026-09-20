package com.navratri.app.user.entity;

import com.navratri.app.activity.ActivityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Everything shown publicly (or semi-publicly) about a user. Deliberately excludes
 * phone number and exact address -- those live only on User and Place respectively,
 * and are never serialized in ProfileDto.
 */
@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    private Long id; // shares PK with User (one-to-one)

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String firstName;

    private String profilePictureUrl;

    // Store an age *range* rather than a birthdate, per the "no exact sensitive data" principle.
    private Integer ageRangeMin;
    private Integer ageRangeMax;

    // Coarse area only, e.g. "Vijay Nagar, Indore" -- never GPS coordinates.
    private String approximateArea;

    @Column(length = 500)
    private String bio;

    @Enumerated(EnumType.STRING)
    private GroupSizePreference preferredGroupSize;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_interests", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "interest")
    @Builder.Default
    private Set<String> interests = new HashSet<>();

    @ElementCollection(targetClass = ActivityType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "profile_favorite_activities", joinColumns = @JoinColumn(name = "profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "activity")
    @Builder.Default
    private Set<ActivityType> favoriteActivities = new HashSet<>();

    @Builder.Default
    private boolean verifiedBadge = false;

    @UpdateTimestamp
    private Instant updatedAt;
}
