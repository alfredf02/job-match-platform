package com.example.demo.user.service;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserProfile;
import com.example.demo.user.repository.UserRepository;
import com.jobmatch.user.dto.UpdatePreferencesRequest;
import com.jobmatch.user.dto.UpdateProfileRequest;
import com.jobmatch.user.dto.UserProfileResponse;
import com.jobmatch.user.repository.UserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service // Encapsulates profile retrieval and update operations.
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        // Ensure the user exists; otherwise we cannot build a profile response.
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // If a profile is missing, return an empty shell instead of failing so the UI can render defaults.
        UserProfile profile =
                userProfileRepository.findByUserId(userId).orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        return mapToResponse(user, profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        // Fetch the owning user to ensure updates are scoped to a valid account.
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Find or create the profile so updates always have a persisted row to work with.
        UserProfile profile =
                userProfileRepository
                        .findByUserId(userId)
                        .orElseGet(() -> {
                            UserProfile created = new UserProfile();
                            created.setUser(user);
                            return created;
                        });

        // Apply incoming changes; nullable fields are allowed and will overwrite existing values.
        profile.setLocation(request.getLocation());
        profile.setSkills(request.getSkills());
        profile.setMinSalary(request.getMinSalary());
        profile.setMaxSalary(request.getMaxSalary());
        profile.setDesiredRoles(request.getDesiredRoles());

        UserProfile saved = userProfileRepository.save(profile);
        return mapToResponse(user, saved);
    }

    @Transactional
    public UserProfileResponse updatePreferences(Long userId, UpdatePreferencesRequest request) {
        // Preferences currently mirror profile fields; delegate to reuse update logic.
        UpdateProfileRequest converted = new UpdateProfileRequest();
        converted.setLocation(request.getLocation());
        converted.setSkills(request.getSkills());
        converted.setMinSalary(request.getMinSalary());
        converted.setMaxSalary(request.getMaxSalary());
        converted.setDesiredRoles(request.getDesiredRoles());
        return updateProfile(userId, converted);
    }

    private UserProfileResponse mapToResponse(User user, UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setLocation(profile.getLocation());
        response.setSkills(profile.getSkills());
        response.setMinSalary(profile.getMinSalary());
        response.setMaxSalary(profile.getMaxSalary());
        response.setDesiredRoles(profile.getDesiredRoles());
        return response;
    }
}

/*
Explanation
- Retrieves and updates profile data while ensuring it belongs to the authenticated user id passed from controllers.
- Maps persisted User/UserProfile entities into UserProfileResponse DTOs so controllers return REST-friendly payloads.
- The current user id flows from JWT -> JwtAuthenticationFilter -> SecurityContext -> controller -> service methods.
*/