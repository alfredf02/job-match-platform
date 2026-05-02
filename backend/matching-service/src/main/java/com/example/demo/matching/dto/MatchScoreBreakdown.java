package com.example.demo.matching.dto;

public class MatchScoreBreakdown {

    private double skillOverlapScore;
    private double locationScore;
    private double salaryScore;
    private double roleScore;
    private double totalScore;

    public MatchScoreBreakdown() {
        // Default constructor for serialization
    }

    public MatchScoreBreakdown(double skillOverlapScore, double locationScore, double salaryScore,
                               double roleScore, double totalScore) {
        this.skillOverlapScore = skillOverlapScore;
        this.locationScore = locationScore;
        this.salaryScore = salaryScore;
        this.roleScore = roleScore;
        this.totalScore = totalScore;
    }

    public double getSkillOverlapScore() {
        return skillOverlapScore;
    }

    public void setSkillOverlapScore(double skillOverlapScore) {
        this.skillOverlapScore = skillOverlapScore;
    }

    public double getLocationScore() {
        return locationScore;
    }

    public void setLocationScore(double locationScore) {
        this.locationScore = locationScore;
    }

    public double getSalaryScore() {
        return salaryScore;
    }

    public void setSalaryScore(double salaryScore) {
        this.salaryScore = salaryScore;
    }

    public double getRoleScore() {
        return roleScore;
    }

    public void setRoleScore(double roleScore) {
        this.roleScore = roleScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }
}