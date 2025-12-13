package com.example.demo.user.service;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserProfile;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.security.JwtService;
import com.example.demo.user.dto.AuthResponse;
import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.RegisterRequest;
import com.example.demo.user.repository.UserProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service // Exposes authentication operations for controllers.
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            UserProfileRepository userProfileRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Block duplicate registrations by checking if the email already exists.
        userRepository
                .findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Email is already registered");
                });

        // Hash the raw password before storing it so the database never contains plaintext credentials.
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);
        user.setFullName(request.getFullName());

        User savedUser = userRepository.save(user);

        // Optionally create an empty profile so profile endpoints have a starting row.
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        userProfileRepository.save(profile);

        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());

        return buildAuthResponse(savedUser, token);
    }

    public AuthResponse login(LoginRequest request) {
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                // Fail fast when the email is unknown to avoid leaking which accounts exist.
                                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        // Verify the submitted password against the stored hash; mismatch means authentication failed.
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        AuthResponse response = new AuthResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setToken(token);
        return response;
    }
}

/*
Explanation
- register: checks for duplicate emails, hashes the password, saves the user (and a starter profile), issues a JWT, and returns an AuthResponse.
- login: fetches the user by email, verifies the password hash, issues a JWT, and returns the same AuthResponse shape used by registration.
- Controllers call these methods to encapsulate auth logic while keeping security concerns (hashing/JWT) in one place.
*/