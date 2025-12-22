package com.example.demo.job.dto.job;

import com.example.demo.job.domain.Job;
import com.example.demo.job.dto.employer.EmployerResponse;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class JobResponse {

    private Long id;
    private EmployerResponse employer;
    private String title;
    private String description;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String workType;
    private String seniority;
    private List<String> skills;
    private Instant createdAt;
    private Instant updatedAt;

    public JobResponse(Job job) {
        this.id = job.getId();
        this.employer = new EmployerResponse(job.getEmployer());
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.location = job.getLocation();
        this.salaryMin = job.getSalaryMin();
        this.salaryMax = job.getSalaryMax();
        this.workType = job.getWorkType() != null ? job.getWorkType().name() : null;
        this.seniority = job.getSeniority() != null ? job.getSeniority().name() : null;
        this.skills = job.getJobSkills().stream()
                .map(skill -> skill.getSkill())
                .collect(Collectors.toList());
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public EmployerResponse getEmployer() {
        return employer;
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


    public Integer getSalaryMin() {
        return salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public String getWorkType() {
        return workType;
    }

    public String getSeniority() {
        return seniority;
    }

    public List<String> getSkills() {
        return skills;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}