package com.example.demo.matching;

import java.time.LocalDateTime;

/**
 * Response DTO returned by the API to keep persistence details hidden.
 */
public class MatchResponseDto {

    private Long id; // Match identifier
    private Long userId; // User this match belongs to
    private Long jobId; // Job related to the match
    private Double score; // Score representing the match quality
    private String status; // Current status of the match
    private LocalDateTime createdAt; // Timestamp when the match was created

    public MatchResponseDto() {
        // Default constructor
    }

    public MatchResponseDto(Long id, Long userId, Long jobId, Double score, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.score = score;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}