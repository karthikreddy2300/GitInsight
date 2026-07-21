package com.gitinsight.backend.service;

import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

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
        documentationScore += calculateTopicsScore(repo);
        documentationScore += calculateWikiScore(repo);

        // Community
        communityScore += calculateStarsScore(repo.getStargazers_count());
        communityScore += calculateForksScore(repo.getForks_count());
        communityScore += calculateWatchersScore(repo.getWatchers_count());

        // Maintenance
        maintenanceScore += calculateIssuesScore(repo.isHas_issues());
        maintenanceScore += calculateActivityScore(repo);
        maintenanceScore += calculateArchiveScore(repo.isArchived());
        maintenanceScore += calculateLicenseScore(repo);

        boolean hasActions = gitHubApiClient.hasGitHubActions(owner, repoName);
        maintenanceScore += hasActions ? 10 : 0; // changed from 20 → 10

        int total =
                documentationScore
                        + communityScore
                        + maintenanceScore;

        double percentage = (total / 300.0) * 100;

        String grade = calculateGrade(percentage);

        List<String> recommendations = generateRecommendations(
                repo,
                documentationScore,
                communityScore,
                hasActions
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
            return 8;
        }

        if (length <= 60) {
            return 15;
        }

        return 20;
    }

    private int calculateTopicsScore(RepositoryResponse repo) {

        if (repo.getTopics() == null) {
            return 0;
        }

        int count = repo.getTopics().size();

        if (count == 0) {
            return 0;
        }

        if (count <= 2) {
            return 8;
        }

        if (count <= 5) {
            return 15;
        }

        return 20;
    }

    private int calculateWikiScore(RepositoryResponse repo) {

        if (repo.isHas_wiki()) {
            return 10;
        }

        return 0;
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

    private int calculateActivityScore(RepositoryResponse repo) {

        if (repo.getPushed_at() == null) {
            return 0;
        }

        OffsetDateTime lastPush = OffsetDateTime.parse(repo.getPushed_at());
        OffsetDateTime now = OffsetDateTime.now();

        long days = ChronoUnit.DAYS.between(lastPush, now);

        if (days <= 30) {
            return 30; // changed from 40 → 30
        }

        if (days <= 90) {
            return 20; // changed from 30 → 20
        }

        if (days <= 180) {
            return 10; // changed from 20 → 10
        }

        return 0; // >180 days → 0
    }

    private int calculateArchiveScore(boolean archived) {

        if (archived)
            return 0;

        return 20; // changed from 30 → 20
    }

    private int calculateLicenseScore(RepositoryResponse repo) {

        if (repo.getLicense() == null) {
            return 0;
        }

        return 10;
    }

    // ==========================================================
    // Recommendations
    // ==========================================================

    private List<String> generateRecommendations(
            RepositoryResponse repo,
            int documentationScore,
            int communityScore,
            boolean hasActions) {

        List<String> recommendations = new ArrayList<>();

        if (documentationScore < 100) {
            recommendations.add("Improve your README with installation and usage instructions.");
        }

        if (repo.getDescription() == null || repo.getDescription().isBlank()) {
            recommendations.add("Add a meaningful repository description.");
        }

        if (repo.getTopics() == null || repo.getTopics().isEmpty()) {
            recommendations.add("Add GitHub topics to improve repository discoverability.");
        }

        if (!repo.isHas_wiki()) {
            recommendations.add("Enable GitHub Wiki for better documentation.");
        }

        if (repo.getLicense() == null) {
            recommendations.add("Add an open-source license to clarify usage rights.");
        }

        if (!hasActions) {
            recommendations.add("Add a GitHub Actions workflow for Continuous Integration (CI/CD).");
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

        return "F";
    }
}
