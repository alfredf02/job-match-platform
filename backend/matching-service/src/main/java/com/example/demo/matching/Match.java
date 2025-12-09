package com.example.demo.matching;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA entity representing a match between a user and a job.
 */
@Entity
@Table(
        name = "matches",
        indexes = {
                @Index(name = "idx_matches_user_id", columnList = "user_id"),
                @Index(name = "idx_matches_user_score_created", columnList = "user_id, score DESC, created_at DESC")
        }
)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Primary key for the match row

    @Column(name = "user_id", nullable = false)
    private Long userId; // Link to the user-service user

    @Column(name = "job_id", nullable = false)
    private Long jobId; // Link to the job-service job

    @Column(name = "score", nullable = false)
    private Double score = 0.0; // Numeric relevance score, defaults to 0

    @Column(name = "status", nullable = false, length = 50)
    private String status = "SUGGESTED"; // Current status of the match

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // When the match was created

    public Match() {
        // Default constructor required by JPA
    }

    public Match(Long id, Long userId, Long jobId, Double score, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.score = score;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        // Ensure defaults are set before inserting into the database
        if (this.score == null) {
            this.score = 0.0;
        }
        if (this.status == null || this.status.isBlank()) {
            this.status = "SUGGESTED";
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and setters for JPA and business logic access
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