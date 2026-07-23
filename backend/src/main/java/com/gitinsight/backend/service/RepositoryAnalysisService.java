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
        RepositoryResponse repo = gitHubApiClient.getRepository(owner, repoName);

        int documentationScore = 0;
        int communityScore = 0;
        int maintenanceScore = 0;

        // Documentation (max 100)
        documentationScore += calculateReadmeScore(owner, repoName);                // max 40
        documentationScore += calculateDescriptionScore(repo);                      // max 20
        documentationScore += calculateTopicsScore(repo);                           // max 20
        documentationScore += calculateWikiScore(repo);                             // max 10
        documentationScore += calculateContributionGuidelinesScore(owner, repoName);// max 10

        // Community (max 100)
        communityScore += calculateStarsScore(repo.getStargazers_count());          // max 40
        communityScore += calculateForksScore(repo.getForks_count());               // max 40
        communityScore += calculateWatchersScore(repo.getWatchers_count());         // max 20

        // Maintenance (max 100)
        maintenanceScore += calculateIssuesScore(repo.isHas_issues());              // max 20
        maintenanceScore += calculateCommitContinuityScore(repo);                   // max 20
        maintenanceScore += calculateArchiveScore(repo.isArchived());               // max 10
        maintenanceScore += calculateLicenseScore(repo);                            // max 10
        maintenanceScore += calculateActionsScore(owner, repoName);                 // max 10
        maintenanceScore += calculateDeploymentScore(owner, repoName);              // max 30

        int total = documentationScore + communityScore + maintenanceScore;
        double percentage = (total / 300.0) * 100;
        String grade = calculateGrade(percentage);

        List<String> recommendations = generateRecommendations(
                repo, documentationScore, communityScore, maintenanceScore);

        // Build response
        ScoreResponse response = new ScoreResponse(
                documentationScore, communityScore, maintenanceScore,
                total, percentage, grade, recommendations);

        // Metadata
        response.setRepositoryName(repo.getName());
        response.setOwner(owner);
        response.setLanguage(repo.getLanguage());
        response.setDefaultBranch(repo.getDefault_branch());
        response.setStars(repo.getStargazers_count());
        response.setForks(repo.getForks_count());
        response.setWatchers(repo.getWatchers_count());
        response.setOpenIssues(repo.getOpen_issues_count());
        response.setLastUpdated(repo.getPushed_at());
        response.setArchived(repo.isArchived());
        response.setLicense(repo.getLicense() != null ? repo.getLicense().getName() : "No License");
        response.setRepositorySize(repo.getSize());
        response.setVisibility(repo.getVisibility());
        response.setTopicCount(repo.getTopics() == null ? 0 : repo.getTopics().size());

        return response;
    }

    // ========================= Documentation =========================
    private int calculateReadmeScore(String owner, String repoName) {
        try {
            ReadmeResponse readme = gitHubApiClient.getReadme(owner, repoName);
            if (readme == null || readme.getContent() == null) return 0;
            String decoded = new String(Base64.getMimeDecoder().decode(readme.getContent()));
            String lower = decoded.toLowerCase();
            if (decoded.length() > 500 &&
                lower.contains("installation") &&
                lower.contains("usage")) {
                return 40;
            }
            return 20;
        } catch (Exception e) {
            return 0;
        }
    }

    private int calculateDescriptionScore(RepositoryResponse repo) {
        String description = repo.getDescription();
        if (description == null || description.isBlank()) return 0;
        return description.length() > 30 ? 20 : 10;
    }

    private int calculateTopicsScore(RepositoryResponse repo) {
        if (repo.getTopics() == null) return 0;
        int count = repo.getTopics().size();
        return count >= 5 ? 20 : count >= 2 ? 10 : 0;
    }

    private int calculateWikiScore(RepositoryResponse repo) {
        return repo.isHas_wiki() ? 10 : 0;
    }

    private int calculateContributionGuidelinesScore(String owner, String repoName) {
        try {
            boolean hasContributing =
                gitHubApiClient.pathExists(owner, repoName, "CONTRIBUTING.md");
            boolean hasCodeOfConduct =
                gitHubApiClient.pathExists(owner, repoName, "CODE_OF_CONDUCT.md");

            if (hasContributing && hasCodeOfConduct) return 10;
            if (hasContributing || hasCodeOfConduct) return 5;
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // ========================= Community =========================
    private int calculateStarsScore(int stars) {
        if (stars >= 100) return 40;
        if (stars >= 50) return 30;
        if (stars >= 10) return 20;
        return stars > 0 ? 10 : 0;
    }

    private int calculateForksScore(int forks) {
        if (forks >= 50) return 40;
        if (forks >= 20) return 30;
        if (forks >= 5) return 20;
        return forks > 0 ? 10 : 0;
    }

    private int calculateWatchersScore(int watchers) {
        if (watchers >= 10) return 20;
        if (watchers >= 5) return 10;
        return watchers > 0 ? 5 : 0;
    }

    // ========================= Maintenance =========================
    private int calculateIssuesScore(boolean hasIssues) {
        return hasIssues ? 20 : 0;
    }

    private int calculateCommitContinuityScore(RepositoryResponse repo) {
        if (repo.getPushed_at() == null) return 0;
        long days = ChronoUnit.DAYS.between(OffsetDateTime.parse(repo.getPushed_at()), OffsetDateTime.now());
        if (days <= 30) return 20;
        if (days <= 90) return 10;
        return 0;
    }

    private int calculateArchiveScore(boolean archived) {
        return archived ? 0 : 10;
    }

    private int calculateLicenseScore(RepositoryResponse repo) {
        return repo.getLicense() != null ? 10 : 0;
    }

    private int calculateActionsScore(String owner, String repoName) {
        return gitHubApiClient.hasGitHubActions(owner, repoName) ? 10 : 0;
    }

    private int calculateDeploymentScore(String owner, String repoName) {
        try {
            boolean hasDockerfile = gitHubApiClient.pathExists(owner, repoName, "Dockerfile");
            boolean hasComposeYml = gitHubApiClient.pathExists(owner, repoName, "docker-compose.yml")
                    || gitHubApiClient.pathExists(owner, repoName, "docker-compose.yaml");
            boolean hasVercel = gitHubApiClient.pathExists(owner, repoName, "vercel.json");
            boolean hasNetlify = gitHubApiClient.pathExists(owner, repoName, "netlify.toml");
            boolean hasRender = gitHubApiClient.pathExists(owner, repoName, "render.yaml")
                    || gitHubApiClient.pathExists(owner, repoName, "render.yml");
            boolean hasFly = gitHubApiClient.pathExists(owner, repoName, "fly.toml");
            boolean hasProcfile = gitHubApiClient.pathExists(owner, repoName, "Procfile");
            boolean hasKubernetes = gitHubApiClient.pathExists(owner, repoName, "kubernetes")
                    || gitHubApiClient.pathExists(owner, repoName, "k8s")
                    || gitHubApiClient.pathExists(owner, repoName, "deployment.yaml")
                    || gitHubApiClient.pathExists(owner, repoName, "deployment.yml")
                    || gitHubApiClient.pathExists(owner, repoName, "helm")
                    || gitHubApiClient.pathExists(owner, repoName, "charts");

            int score = 0;
            if (hasDockerfile) score += 10;
            if (hasComposeYml) score += 5;
            if (hasVercel || hasNetlify || hasRender || hasFly) score += 10;
            if (hasKubernetes) score += 5;

            return Math.min(score, 30);
        } catch (Exception e) {
            return 0;
        }
    }

    // ========================= Recommendations =========================
private List<String> generateRecommendations(
        RepositoryResponse repo,
        int docScore,
        int communityScore,
        int maintenanceScore) {

    List<String> recs = new ArrayList<>();

    if (docScore < 60) {
        recs.add("Improve documentation (README, description, topics, wiki, and contributing guidelines).");
    }

    if (communityScore < 50) {
        recs.add("Increase community engagement by attracting more stars, forks, and watchers.");
    }

    if (maintenanceScore < 60) {
        recs.add("Improve project maintenance by enabling GitHub Actions, adding deployment configuration, keeping commits regular, and using a license.");
    }

    if (repo.isArchived()) {
        recs.add("Repository is archived. Consider making it active again if development is continuing.");
    }

    if (repo.getLicense() == null) {
        recs.add("Add an open-source license.");
    }

    if (repo.getTopics() == null || repo.getTopics().isEmpty()) {
        recs.add("Add GitHub repository topics to improve discoverability.");
    }

    return recs;
}

private String calculateGrade(double percentage) {

    if (percentage >= 90)
        return "A+";

    if (percentage >= 80)
        return "A";

    if (percentage >= 70)
        return "B";

    if (percentage >= 60)
        return "C";

    if (percentage >= 50)
        return "D";

    return "F";
}
}