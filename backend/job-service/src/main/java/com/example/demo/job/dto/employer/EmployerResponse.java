package com.example.demo.job.dto.employer;

import com.example.demo.job.domain.Employer;
import java.time.Instant;

public class EmployerResponse {

    private Long id;
    private String name;
    private String website;
    private String description;
    private String location;
    private Instant createdAt;
    private Instant updatedAt;

    public EmployerResponse(Employer employer) {
        this.id = employer.getId();
        this.name = employer.getName();
        this.website = employer.getWebsite();
        this.description = employer.getDescription();
        this.location = employer.getLocation();
        this.createdAt = employer.getCreatedAt();
        this.updatedAt = employer.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}