package com.example.demo.job.dto;

import com.example.demo.job.domain.Job;
import java.time.Instant;

public class JobResponse {

    private Long id;
    private Long employerId;
    private String title;
    private String description;
    private String location;
    private String workType;
    private String seniority;
    private Integer salaryMin;
    private Integer salaryMax;
    private Instant createdAt;
    private Instant updatedAt;

    public JobResponse(Job job) {
        this.id = job.getId();
        this.employerId = job.getEmployer().getId();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.location = job.getLocation();
        this.workType = job.getWorkType();
        this.seniority = job.getSeniority();
        this.salaryMin = job.getSalaryMin();
        this.salaryMax = job.getSalaryMax();
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getEmployerId() {
        return employerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getWorkType() {
        return workType;
    }

    public String getSeniority() {
        return seniority;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}