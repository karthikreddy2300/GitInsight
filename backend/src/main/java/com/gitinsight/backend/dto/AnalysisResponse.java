package com.gitinsight.backend.dto;

import java.util.List;

public class AnalysisResponse {

    private String owner;
    private String repository;
    private ScoreResponse score;
    private List<String> recommendations;

    public AnalysisResponse() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public ScoreResponse getScore() {
        return score;
    }

    public void setScore(ScoreResponse score) {
        this.score = score;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}