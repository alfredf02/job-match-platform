package com.example.demo.job.dto.employer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateEmployerRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be 255 characters or less")
    private String name;

    @Size(max = 255, message = "Website must be 255 characters or less")
    private String website;

    private String description;

    @Size(max = 255, message = "Location must be 255 characters or less")
    private String location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}