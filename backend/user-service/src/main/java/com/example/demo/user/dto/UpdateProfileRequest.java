package com.example.demo.user.dto;

import jakarta.validation.constraints.Size;

// Request payload for updating a user's profile details.
public class UpdateProfileRequest {

    @Size(max = 255) // Keep location string within reasonable length.
    private String location;

    private String skills; // Comma-separated skills string.

    private Integer minSalary; // Nullable to allow partial updates.

    private Integer maxSalary;

    private String desiredRoles; // Comma-separated desired roles string.

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