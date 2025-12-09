package com.example.demo.user.dto;

import jakarta.validation.constraints.Size;

// Separate request object to capture preference updates explicitly.
public class UpdatePreferencesRequest {

    @Size(max = 255) // Same constraints as profile update for consistency.
    private String location;

    private String skills; // Comma-separated list of skills.

    private Integer minSalary; // Nullable fields support partial updates.

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