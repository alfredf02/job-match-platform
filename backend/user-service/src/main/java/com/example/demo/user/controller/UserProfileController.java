package com.jobmatch.user.controller;

import com.example.demo.user.service.UserProfileService;
import com.jobmatch.user.dto.UpdatePreferencesRequest;
import com.jobmatch.user.dto.UpdateProfileRequest;
import com.jobmatch.user.dto.UserProfileResponse;
import com.jobmatch.user.security.JwtAuthenticationFilter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Exposes profile-related HTTP endpoints for authenticated users.
@RequestMapping("/api/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        // Resolve the current user id from the SecurityContext populated by JwtAuthenticationFilter.
        Long userId = getCurrentUserId();
        UserProfileResponse response = userProfileService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        // Apply profile updates for the authenticated user.
        Long userId = getCurrentUserId();
        UserProfileResponse response = userProfileService.updateProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserProfileResponse> updatePreferences(
            @Valid @RequestBody UpdatePreferencesRequest request) {
        // Preferences use the same underlying profile record; this endpoint keeps the API boundary explicit.
        Long userId = getCurrentUserId();
        UserProfileResponse response = userProfileService.updatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtAuthenticationFilter.JwtUserPrincipal)) {
            throw new IllegalStateException("Authenticated user id not found in security context");
        }
        JwtAuthenticationFilter.JwtUserPrincipal principal =
                (JwtAuthenticationFilter.JwtUserPrincipal) authentication.getPrincipal();
        return principal.getUserId();
    }
}

/*
Explanation
- Controllers pull the authenticated user id from the SecurityContext (set by JwtAuthenticationFilter) and delegate to
UserProfileService for reads/updates.
- getCurrentUserProfile: JWT -> SecurityContext -> controller -> service -> database -> response DTO.
- updateProfile/updatePreferences: authenticated user id flows the same way, ensuring updates are scoped to the caller.
*/