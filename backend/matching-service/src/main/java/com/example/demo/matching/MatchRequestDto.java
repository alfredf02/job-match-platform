package com.example.demo.matching;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for creating a new match.
 */
public class MatchRequestDto {

    @NotNull(message = "userId is required")
    @Positive(message = "userId must be positive")
    private Long userId; // User identifier coming from user-service

    @NotNull(message = "jobId is required")
    @Positive(message = "jobId must be positive")
    private Long jobId; // Job identifier coming from job-service

    private Double score; // Optional initial score

    private String status; // Optional initial status

    public MatchRequestDto() {
        // Default constructor for deserialization
    }

    public MatchRequestDto(Long userId, Long jobId, Double score, String status) {
        this.userId = userId;
        this.jobId = jobId;
        this.score = score;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}