package com.jobmatch.user.controller;

import com.example.demo.user.service.AuthService;
import com.jobmatch.user.dto.AuthResponse;
import com.jobmatch.user.dto.LoginRequest;
import com.jobmatch.user.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Exposes HTTP endpoints for authentication flows.
@RequestMapping("/api/auth") // Base path for auth-related operations.
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // Delegates to the service which handles hashing, uniqueness checks, and token creation.
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Service validates credentials; on failure it throws a 401 to signal bad credentials.
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

/*
Explanation
- register: receives validated registration data, forwards to AuthService for duplicate check, password hashing, user/profile creation, JWT issuance, and returns 201 with the AuthResponse.
- login: accepts credentials, lets AuthService verify the password and emit a JWT, and returns the same AuthResponse shape with 200 OK.
- Controllers stay thin, leaving business rules and security details to the service layer for easier testing and maintenance.
*/