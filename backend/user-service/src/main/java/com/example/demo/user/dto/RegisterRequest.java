package com.example.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Incoming payload for user registration.
public class RegisterRequest {

    @Email // Ensure the incoming string is a valid email format.
    @NotBlank // Email is required to create an account.
    private String email;

    @NotBlank // Password must be present before hashing in the service layer.
    @Size(min = 8, max = 100) // Basic password length guard; hashing happens elsewhere.
    private String password;

    @Size(max = 255) // Allow reasonable full name length without overflowing storage.
    private String fullName;

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