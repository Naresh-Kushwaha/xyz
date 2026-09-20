package com.navratri.app.user.controller;

import com.navratri.app.common.ApiResponse;
import com.navratri.app.user.dto.*;
import com.navratri.app.user.service.AuthService;
import com.navratri.app.user.service.PhoneVerificationService;
import com.navratri.app.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Account created", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // Stateless JWT: logout is handled client-side by discarding the tokens.
        // For hard revocation, maintain a denylist of refresh-token IDs in Redis and check it in JwtAuthFilter.
        return ApiResponse.message("Logged out");
    }

    @PostMapping("/phone/send-otp")
    public ApiResponse<Void> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        phoneVerificationService.sendOtp(request.phoneNumber());
        return ApiResponse.message("Verification code sent");
    }

    @PostMapping("/phone/verify-otp")
    public ApiResponse<Void> verifyOtp(@AuthenticationPrincipal UserPrincipal principal,
                                        @Valid @RequestBody VerifyOtpRequest request) {
        phoneVerificationService.verifyOtp(principal.getId(), request.phoneNumber(), request.code());
        return ApiResponse.message("Phone number verified");
    }
}
