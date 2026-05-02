package com.example.demo.matching.dto;

import java.util.ArrayList;
import java.util.List;

public class MatchingUserProfileDto {

    private Long externalUserId;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private List<String> skills = new ArrayList<>();
    private List<String> desiredRoles = new ArrayList<>();

    public MatchingUserProfileDto() {
        // Default constructor for serialization
    }

    public MatchingUserProfileDto(Long externalUserId, String location, Integer salaryMin, Integer salaryMax,
                                  List<String> skills, List<String> desiredRoles) {
        this.externalUserId = externalUserId;
        this.location = location;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.skills = skills;
        this.desiredRoles = desiredRoles;
    }

    public Long getExternalUserId() {
        return externalUserId;
    }

    public void setExternalUserId(Long externalUserId) {
        this.externalUserId = externalUserId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getDesiredRoles() {
        return desiredRoles;
    }

    public void setDesiredRoles(List<String> desiredRoles) {
        this.desiredRoles = desiredRoles;
    }
}