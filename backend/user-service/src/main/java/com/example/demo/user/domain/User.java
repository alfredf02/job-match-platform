package com.example.demo.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity // This class is a JPA entity (maps to a DB table)
@Table(name = "users")  // Map to the "users" table
public class User {

    @Id // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID in the DB
    private Long id;

    @Email  // Must be valid email format
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @JsonIgnore // Do NOT include password in JSON responses
    @Column(nullable = false)   // NOT NULL in DB
    private String password;

    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

     // Timestamp of when the user was created
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Getters (no setter for id & createdAt to keep them read-only from outside)
    public Long getId() {
        return id;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Runs before the entity is first saved to DB
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}