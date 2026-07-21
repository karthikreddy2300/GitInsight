package com.gitinsight.backend.service;

import java.util.Base64;
import java.util.ArrayList;
import java.util.List;

import com.gitinsight.backend.client.GitHubApiClient;
import com.gitinsight.backend.dto.ReadmeResponse;
import com.gitinsight.backend.dto.RepositoryResponse;
import com.gitinsight.backend.dto.ScoreResponse;

import org.springframework.stereotype.Service;

@Service
public class RepositoryAnalysisService {

    private final GitHubApiClient gitHubApiClient;

    public RepositoryAnalysisService(GitHubApiClient gitHubApiClient) {
        this.gitHubApiClient = gitHubApiClient;
    }

    public ScoreResponse calculateScore(String owner, String repoName) {

        RepositoryResponse repo =
                gitHubApiClient.getRepository(owner, repoName);

        int documentationScore = 0;
        int communityScore = 0;
        int maintenanceScore = 0;

        // Documentation
        documentationScore += calculateReadmeScore(owner, repoName);
        documentationScore += calculateDescriptionScore(repo);

        // Community
        communityScore += calculateStarsScore(repo.getStargazers_count());
        communityScore += calculateForksScore(repo.getForks_count());
        communityScore += calculateWatchersScore(repo.getWatchers_count());

        // Maintenance
        maintenanceScore += calculateIssuesScore(repo.isHas_issues());
        maintenanceScore += calculateActivityScore();
        maintenanceScore += calculateArchiveScore(repo.isArchived());

        int total =
                documentationScore
                        + communityScore
                        + maintenanceScore;

        double percentage = (total / 300.0) * 100;

        String grade = calculateGrade(percentage);

        List<String> recommendations = generateRecommendations(
                repo,
                documentationScore,
                communityScore
        );

        return new ScoreResponse(
                documentationScore,
                communityScore,
                maintenanceScore,
                total,
                percentage,
                grade,
                recommendations
        );
    }

    // ==========================================================
    // Documentation
    // ==========================================================

    private int calculateReadmeScore(String owner, String repoName) {

        ReadmeResponse readme;

        try {
            readme = gitHubApiClient.getReadme(owner, repoName);
        } catch (Exception e) {
            return 0;
        }

        if (readme == null || readme.getContent() == null) {
            return 0;
        }

        String base64Content = readme.getContent();

        if (base64Content == null) {
            return 0;
        }

        base64Content = base64Content.replaceAll("\\s", "");

        String decodedReadme;

        try {
            decodedReadme = new String(
                    Base64.getMimeDecoder().decode(base64Content)
            );
        } catch (IllegalArgumentException ex) {
            return 25;
        }

        int score = 25;

        if (decodedReadme.length() > 300) {
            score = 40;
        }

        String lower = decodedReadme.toLowerCase();

        if (lower.contains("installation")
                && lower.contains("usage")) {
            score = 50;
        }

        return score;
    }

    private int calculateDescriptionScore(RepositoryResponse repo) {

        String description = repo.getDescription();

        if (description == null || description.isBlank()) {
            return 0;
        }

        int length = description.trim().length();

        if (length <= 20) {
            return 20;
        }

        if (length <= 60) {
            return 35;
        }

        return 50;
    }

    // ==========================================================
    // Community
    // ==========================================================

    private int calculateStarsScore(int stars) {

        if (stars == 0)
            return 0;

        if (stars <= 5)
            return 10;

        if (stars <= 15)
            return 20;

        if (stars <= 50)
            return 30;

        return 40;
    }

    private int calculateForksScore(int forks) {

        if (forks == 0)
            return 0;

        if (forks <= 2)
            return 10;

        if (forks <= 5)
            return 20;

        if (forks <= 15)
            return 30;

        return 40;
    }

    private int calculateWatchersScore(int watchers) {

        if (watchers == 0)
            return 0;

        if (watchers <= 2)
            return 5;

        if (watchers <= 5)
            return 10;

        if (watchers <= 10)
            return 15;

        return 20;
    }

    // ==========================================================
    // Maintenance
    // ==========================================================

    private int calculateIssuesScore(boolean hasIssues) {

        if (hasIssues)
            return 30;

        return 0;
    }

    private int calculateActivityScore() {

        /*
         * TODO
         *
         * Later use:
         * repo.getPushed_at()
         *
         * >12 months -> 0
         * 6-12 months -> 10
         * 3-6 months -> 20
         * 1-3 months -> 30
         * <30 days -> 40
         */

        return 40;
    }

    private int calculateArchiveScore(boolean archived) {

        if (archived)
            return 0;

        return 30;
    }

    // ==========================================================
    // Recommendations
    // ==========================================================

    private List<String> generateRecommendations(
            RepositoryResponse repo,
            int documentationScore,
            int communityScore) {

        List<String> recommendations = new ArrayList<>();

        if (documentationScore < 100) {
            recommendations.add("Improve your README with installation and usage instructions.");
        }

        if (repo.getDescription() == null || repo.getDescription().isBlank()) {
            recommendations.add("Add a meaningful repository description.");
        }

        if (communityScore < 60) {
            recommendations.add("Increase project visibility to gain more stars and forks.");
        }

        if (!repo.isHas_issues()) {
            recommendations.add("Enable GitHub Issues for better project management.");
        }

        if (repo.isArchived()) {
            recommendations.add("Keep the repository active instead of archiving it.");
        }

        return recommendations;
    }

    // ==========================================================
    // Grade Calculation
    // ==========================================================

    private String calculateGrade(double percentage) {

        if (percentage >= 90) {
            return "A+";
        }

        if (percentage >= 80) {
            return "A";
        }

        if (percentage >= 70) {
            return "B";
        }

        if (percentage >= 60) {
            return "C";
        }

        if (percentage >= 50) {
            return "D";
        }

        retugit statusrn "F";
    }
}
