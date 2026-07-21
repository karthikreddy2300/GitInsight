package com.gitinsight.backend.dto;

import java.util.List;

public class RecommendationResponse {

    private List<String> recommendations;

    public RecommendationResponse() {
    }

    public RecommendationResponse(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}