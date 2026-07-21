package com.gitinsight.backend.dto;

import java.util.List;

public class ScoreResponse {

    private int documentationScore;
    private int communityScore;
    private int maintenanceScore;

    private int totalScore;
    private double percentage;
    private String grade;

    private List<String> recommendations;

    public ScoreResponse() {
    }

    public ScoreResponse(int documentationScore,
                         int communityScore,
                         int maintenanceScore,
                         int totalScore,
                         double percentage,
                         String grade,
                         List<String> recommendations) {

        this.documentationScore = documentationScore;
        this.communityScore = communityScore;
        this.maintenanceScore = maintenanceScore;
        this.totalScore = totalScore;
        this.percentage = percentage;
        this.grade = grade;
        this.recommendations = recommendations;
    }

    public int getDocumentationScore() {
        return documentationScore;
    }

    public void setDocumentationScore(int documentationScore) {
        this.documentationScore = documentationScore;
    }

    public int getCommunityScore() {
        return communityScore;
    }

    public void setCommunityScore(int communityScore) {
        this.communityScore = communityScore;
    }

    public int getMaintenanceScore() {
        return maintenanceScore;
    }

    public void setMaintenanceScore(int maintenanceScore) {
        this.maintenanceScore = maintenanceScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
