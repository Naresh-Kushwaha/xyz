package com.navratri.app.user.service;

import com.navratri.app.common.exceptions.BadRequestException;
import com.navratri.app.common.exceptions.UnauthorizedException;
import com.navratri.app.user.dto.AuthResponse;
import com.navratri.app.user.dto.LoginRequest;
import com.navratri.app.user.dto.ProfileDto;
import com.navratri.app.user.dto.RegisterRequest;
import com.navratri.app.user.entity.AuthProvider;
import com.navratri.app.user.entity.Profile;
import com.navratri.app.user.entity.Role;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.repository.ProfileRepository;
import com.navratri.app.user.repository.UserRepository;
import com.navratri.app.security.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("An account with this email already exists");
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .provider(AuthProvider.LOCAL)
                .role(Role.USER)
                .build();
        user = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(user)
                .firstName(request.firstName())
                .build();
        profile = profileRepository.save(profile);

        return buildAuthResponse(user, profile);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        if (user.isSuspended()) {
            throw new UnauthorizedException("This account has been suspended");
        }

        Profile profile = profileRepository.findByUser_Id(user.getId()).orElse(null);
        return buildAuthResponse(user, profile);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken) || !"refresh".equals(jwtService.extractTokenType(refreshToken))) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        Claims claims = jwtService.parseClaims(refreshToken);
        Long userId = Long.parseLong(claims.get("uid", String.class));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Account no longer exists"));

        Profile profile = profileRepository.findByUser_Id(user.getId()).orElse(null);
        return buildAuthResponse(user, profile);
    }

    private AuthResponse buildAuthResponse(User user, Profile profile) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        return new AuthResponse(accessToken, refreshToken, profile != null ? ProfileDto.from(profile) : null);
    }
}
