// src/test/java/com/example/demo/user/service/AuthServiceTest.java
package com.example.demo.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserProfile;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.security.JwtService;
import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.RegisterRequest;
import com.example.demo.user.repository.UserProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    @Test
    void register_success_creates_user_profile_and_returns_token() {
        // arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("plaintext");
        request.setFullName("New User");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashed");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(request.getEmail());
        savedUser.setPasswordHash("hashed");
        savedUser.setFullName(request.getFullName());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserProfile savedProfile = new UserProfile();
        savedProfile.setId(10L);
        savedProfile.setUser(savedUser);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(savedProfile);

        when(jwtService.generateToken(savedUser.getId(), savedUser.getEmail())).thenReturn("jwt-token");

        // act
        AuthResponse response = authService.register(request);

        // assert
        assertThat(response.getUserId()).isEqualTo(savedUser.getId());
        assertThat(response.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getFullName()).isEqualTo(request.getFullName());

        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userProfileRepository).save(any(UserProfile.class));
        verify(jwtService).generateToken(savedUser.getId(), savedUser.getEmail());
    }

    @Test
    void register_duplicate_email_throws() {
        // arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("plaintext");
        request.setFullName("Existing User");

        User existing = new User();
        existing.setId(5L);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existing));

        // act/assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository, times(1)).findByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
        verify(userProfileRepository, never()).save(any(UserProfile.class));
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void login_success_returns_auth_response_with_token() {
        // arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("plaintext");

        User user = new User();
        user.setId(2L);
        user.setEmail(request.getEmail());
        user.setPasswordHash("hashed");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(user.getId(), user.getEmail())).thenReturn("jwt-token");

        // act
        AuthResponse response = authService.login(request);

        // assert
        assertThat(response.getUserId()).isEqualTo(user.getId());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getToken()).isEqualTo("jwt-token");

        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPasswordHash());
        verify(jwtService).generateToken(user.getId(), user.getEmail());
    }

    @Test
    void login_wrong_password_throws() {
        // arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong");

        User user = new User();
        user.setId(2L);
        user.setEmail(request.getEmail());
        user.setPasswordHash("hashed");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

        // act/assert
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        verify(jwtService, never()).generateToken(any(), any());
    }
}