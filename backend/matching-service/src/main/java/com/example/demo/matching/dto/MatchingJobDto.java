package com.example.demo.matching.dto;

import java.util.ArrayList;
import java.util.List;

public class MatchingJobDto {

    private Long externalJobId;
    private String title;
    private String company;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String workType;
    private String seniority;
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> preferredSkills = new ArrayList<>();

    public MatchingJobDto() {
        // Default constructor for serialization
    }

    public MatchingJobDto(Long externalJobId, String title, String company, String location,
                          Integer salaryMin, Integer salaryMax, String workType, String seniority,
                          List<String> requiredSkills, List<String> preferredSkills) {
        this.externalJobId = externalJobId;
        this.title = title;
        this.company = company;
        this.location = location;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.workType = workType;
        this.seniority = seniority;
        this.requiredSkills = requiredSkills;
        this.preferredSkills = preferredSkills;
    }

    public Long getExternalJobId() {
        return externalJobId;
    }

    public void setExternalJobId(Long externalJobId) {
        this.externalJobId = externalJobId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getSeniority() {
        return seniority;
    }

    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public List<String> getPreferredSkills() {
        return preferredSkills;
    }

    public void setPreferredSkills(List<String> preferredSkills) {
        this.preferredSkills = preferredSkills;
    }
}