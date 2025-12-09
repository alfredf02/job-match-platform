package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Incoming payload for user login/authentication.
public class LoginRequest {

    @Email // Validate incoming email format.
    @NotBlank // Email is mandatory for login.
    private String email;

    @NotBlank // Password must be present before authentication.
    @Size(min = 8, max = 100) // Align with registration rules for consistency.
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}