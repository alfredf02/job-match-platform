// src/test/java/com/jobmatch/user/service/UserProfileServiceTest.java
package com.example.demo.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserProfile;
import com.example.demo.user.controller.UserProfileController;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserProfileService;
import com.example.demo.user.dto.UpdateProfileRequest;
import com.example.demo.user.dto.UserProfileResponse;
import com.example.demo.user.repository.UserProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserProfileRepository userProfileRepository;

    @InjectMocks private UserProfileService userProfileService;

    @AfterEach
    void tearDown() {
        // Reset any thread-local state (none here) if needed between tests.
    }

    @Test
    void getCurrentUserProfile_creates_default_when_missing() {
        // arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("user@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // act
        UserProfileResponse response = userProfileService.getCurrentUserProfile(userId);

        // assert
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo(user.getEmail());

        // A missing profile should still produce a response using a default profile instance.
        verify(userProfileRepository).findByUserId(userId);
    }

    @Test
    void updateProfile_updates_fields() {
        // arrange
        Long userId = 5L;
        User user = new User();
        user.setId(userId);
        user.setEmail("edit@example.com");

        UserProfile profile = new UserProfile();
        profile.setId(50L);
        profile.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(profile)).thenReturn(profile);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setLocation("Remote");
        request.setSkills("Java,Spring");
        request.setMinSalary(100000);
        request.setMaxSalary(150000);
        request.setDesiredRoles("Backend Engineer");

        // act
        UserProfileResponse response = userProfileService.updateProfile(userId, request);

        // assert
        assertThat(response.getLocation()).isEqualTo("Remote");
        assertThat(response.getSkills()).isEqualTo("Java,Spring");
        assertThat(response.getMinSalary()).isEqualTo(100000);
        assertThat(response.getMaxSalary()).isEqualTo(150000);
        assertThat(response.getDesiredRoles()).isEqualTo("Backend Engineer");

        verify(userProfileRepository).save(profile);
    }

    @Test
    void getCurrentUserProfile_throws_if_user_missing() {
        // arrange
        Long userId = 404L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // act/assert
        assertThatThrownBy(() -> userProfileService.getCurrentUserProfile(userId))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userProfileRepository, never()).findByUserId(userId);
    }
}