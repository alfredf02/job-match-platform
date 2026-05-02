package com.example.demo.matching.dto;

import java.util.ArrayList;
import java.util.List;

public class MatchResponse {

    private Long jobId;
    private String title;
    private String company;
    private double score;
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> missingSkills = new ArrayList<>();

    public MatchResponse() {
        // Default constructor for serialization
    }

    public MatchResponse(Long jobId, String title, String company, double score,
                         List<String> matchedSkills, List<String> missingSkills) {
        this.jobId = jobId;
        this.title = title;
        this.company = company;
        this.score = score;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
}