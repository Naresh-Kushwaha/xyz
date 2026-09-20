package com.navratri.app.user.controller;

import com.navratri.app.common.ApiResponse;
import com.navratri.app.security.UserPrincipal;
import com.navratri.app.user.dto.ProfileDto;
import com.navratri.app.user.dto.UpdateProfileRequest;
import com.navratri.app.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<ProfileDto> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(ProfileDto.from(userService.getProfile(principal.getId())));
    }

    @PutMapping("/me")
    public ApiResponse<ProfileDto> updateMe(@AuthenticationPrincipal UserPrincipal principal,
                                             @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Profile updated", userService.updateProfile(principal.getId(), request));
    }

    @GetMapping("/{userId}")
    public ApiResponse<ProfileDto> getProfile(@PathVariable Long userId) {
        return ApiResponse.ok(ProfileDto.from(userService.getProfile(userId)));
    }
}
