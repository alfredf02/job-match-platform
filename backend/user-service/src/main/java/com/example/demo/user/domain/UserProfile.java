package com.example.demo.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity // Marks this as a JPA entity to be persisted in the database.
@Table(name = "user_profiles") // Maps to the "user_profiles" table created in migration V2.
public class UserProfile {

    @Id // Primary key for the profile.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull // Every profile must be linked to a user record.
    @OneToOne(fetch = FetchType.LAZY) // Unidirectional one-to-one from profile to user.
    @JoinColumn(name = "user_id", nullable = false, unique = true) // Enforces FK and one-profile-per-user rule.
    private User user;

    @Size(max = 255) // Keep location field within reasonable length.
    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "skills") // Comma-separated list of skills.
    private String skills;

    @PositiveOrZero // Salary expectations cannot be negative.
    @Column(name = "min_salary")
    private Integer minSalary;

    @PositiveOrZero
    @Column(name = "max_salary")
    private Integer maxSalary;

    @Column(name = "desired_roles") // Comma-separated desired roles.
    private String desiredRoles;

    // Getters and setters used by JPA and frameworks for property access.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Integer getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public Integer getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getDesiredRoles() {
        return desiredRoles;
    }

    public void setDesiredRoles(String desiredRoles) {
        this.desiredRoles = desiredRoles;
    }
}