package com.example.demo.user.dto;

// Response payload returned after successful authentication (login/registration).
public class AuthResponse {

    private Long userId; // Identifier of the authenticated user.
    private String email; // Email used for identification.
    private String token; // JWT or access token issued by the auth layer.
    private String fullName; // Display name for the user.

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}