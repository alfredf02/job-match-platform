package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO for incoming "create user" HTTP requests
public class CreateUserRequest {

     // Must be non-empty and a valid email address
    @Email
    @NotBlank
    private String email;

    // Must be non-empty (note: not yet hashed, just raw string here)
    @NotBlank
    private String password;

    // Must be non-empty
    @NotBlank
    private String fullName;

    // Standard getters/setters so Spring can bind JSON into this object
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}