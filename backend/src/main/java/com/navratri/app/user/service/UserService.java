package com.navratri.app.user.service;

import com.navratri.app.common.exceptions.ResourceNotFoundException;
import com.navratri.app.user.dto.ProfileDto;
import com.navratri.app.user.dto.UpdateProfileRequest;
import com.navratri.app.user.entity.AuthProvider;
import com.navratri.app.user.entity.Profile;
import com.navratri.app.user.entity.Role;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.repository.ProfileRepository;
import com.navratri.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Profile getProfile(Long userId) {
        return profileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    @Transactional
    public ProfileDto updateProfile(Long userId, UpdateProfileRequest request) {
        Profile profile = getProfile(userId);

        if (request.firstName() != null) profile.setFirstName(request.firstName());
        if (request.profilePictureUrl() != null) profile.setProfilePictureUrl(request.profilePictureUrl());
        if (request.ageRangeMin() != null) profile.setAgeRangeMin(request.ageRangeMin());
        if (request.ageRangeMax() != null) profile.setAgeRangeMax(request.ageRangeMax());
        if (request.approximateArea() != null) profile.setApproximateArea(request.approximateArea());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.preferredGroupSize() != null) profile.setPreferredGroupSize(request.preferredGroupSize());
        if (request.interests() != null) profile.setInterests(request.interests());
        if (request.favoriteActivities() != null) profile.setFavoriteActivities(request.favoriteActivities());

        return ProfileDto.from(profileRepository.save(profile));
    }

    @Transactional
    public User findOrCreateFromGoogle(String email, String name) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = User.builder()
                    .email(email)
                    .provider(AuthProvider.GOOGLE)
                    .role(Role.USER)
                    .emailVerified(true)
                    .build();
            user = userRepository.save(user);

            Profile profile = Profile.builder()
                    .user(user)
                    .firstName(name != null ? name : "Navratri Explorer")
                    .build();
            profileRepository.save(profile);

            return user;
        });
    }
}
