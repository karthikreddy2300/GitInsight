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
import com.gitinsight.backend.dto.ContributorResponse;
import com.gitinsight.backend.dto.CommitResponse;

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
        maintenanceScore += hasActions ? 10 : 0;

        int total = documentationScore + communityScore + maintenanceScore;
        double percentage = (total / 300.0) * 100;
        String grade = calculateGrade(percentage);

        List<String> recommendations = generateRecommendations(
                repo, documentationScore, communityScore, hasActions);

        // Build response
        ScoreResponse response = new ScoreResponse(
                documentationScore, communityScore, maintenanceScore,
                total, percentage, grade, recommendations);

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

        if (repo.getLicense() != null) {
            response.setLicense(repo.getLicense().getName());
        } else {
            response.setLicense("No License");
        }

        response.setRepositorySize(repo.getSize());
        response.setVisibility(repo.getVisibility());
        response.setTopicCount(repo.getTopics() == null ? 0 : repo.getTopics().size());

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime created = OffsetDateTime.parse(repo.getCreated_at());
        response.setRepositoryAgeDays((int) ChronoUnit.DAYS.between(created, now));

        OffsetDateTime pushed = OffsetDateTime.parse(repo.getPushed_at());
        response.setDaysSinceLastCommit((int) ChronoUnit.DAYS.between(pushed, now));

        // Contributor logic
        ContributorResponse[] contributors = gitHubApiClient.getContributors(owner, repoName);
        if (contributors != null && contributors.length > 0) {
            response.setContributorCount(contributors.length);
            response.setTopContributor(contributors[0].getLogin());
            response.setTopContributorCommits(contributors[0].getContributions());
        } else {
            response.setContributorCount(0);
            response.setTopContributor("N/A");
            response.setTopContributorCommits(0);
        }

        // Commit activity logic (null‑safe)
        CommitResponse[] commits = gitHubApiClient.getCommits(owner, repoName);
        int lastWeek = 0;
        int lastMonth = 0;

        if (commits != null) {
            for (CommitResponse commit : commits) {
                if (commit.getCommit() == null ||
                    commit.getCommit().getAuthor() == null ||
                    commit.getCommit().getAuthor().getDate() == null) {
                    continue;
                }
                OffsetDateTime date = OffsetDateTime.parse(
                        commit.getCommit().getAuthor().getDate());
                long days = ChronoUnit.DAYS.between(date, now);
                if (days <= 30) lastMonth++;
                if (days <= 7) lastWeek++;
            }
        }

        response.setCommitsLastWeek(lastWeek);
        response.setCommitsLastMonth(lastMonth);

        if (lastMonth >= 20)
            response.setRepositoryStatus("ACTIVE");
        else if (lastMonth >= 5)
            response.setRepositoryStatus("MODERATELY ACTIVE");
        else
            response.setRepositoryStatus("INACTIVE");

        return response;
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
        if (readme == null || readme.getContent() == null) return 0;

        String base64Content = readme.getContent().replaceAll("\\s", "");
        String decodedReadme;
        try {
            decodedReadme = new String(Base64.getMimeDecoder().decode(base64Content));
        } catch (IllegalArgumentException ex) {
            return 25;
        }

        int score = 25;
        if (decodedReadme.length() > 300) score = 40;
        String lower = decodedReadme.toLowerCase();
        if (lower.contains("installation") && lower.contains("usage")) score = 50;
        return score;
    }

    private int calculateDescriptionScore(RepositoryResponse repo) {
        String description = repo.getDescription();
        if (description == null || description.isBlank()) return 0;
        int length = description.trim().length();
        if (length <= 20) return 8;
        if (length <= 60) return 15;
        return 20;
    }

    private int calculateTopicsScore(RepositoryResponse repo) {
        if (repo.getTopics() == null) return 0;
        int count = repo.getTopics().size();
        if (count == 0) return 0;
        if (count <= 2) return 8;
        if (count <= 5) return 15;
        return 20;
    }

    private int calculateWikiScore(RepositoryResponse repo) {
        return repo.isHas_wiki() ? 10 : 0;
    }

    // ==========================================================
    // Community
    // ==========================================================
    private int calculateStarsScore(int stars) {
        if (stars == 0) return 0;
        if (stars <= 5) return 10;
        if (stars <= 15) return 20;
        if (stars <= 50) return 30;
        return 40;
    }

    private int calculateForksScore(int forks) {
        if (forks == 0) return 0;
        if (forks <= 2) return 10;
        if (forks <= 5) return 20;
        if (forks <= 15) return 30;
        return 40;
    }

    private int calculateWatchersScore(int watchers) {
        if (watchers == 0) return 0;
        if (watchers <= 2) return 5;
        if (watchers <= 5) return 10;
        if (watchers <= 10) return 15;
        return 20;
    }

    // ==========================================================
    // Maintenance
    // ==========================================================
    private int calculateIssuesScore(boolean hasIssues) {
        return hasIssues ? 30 : 0;
    }

    private int calculateActivityScore(RepositoryResponse repo) {
        if (repo.getPushed_at() == null) return 0;
        OffsetDateTime lastPush = OffsetDateTime.parse(repo.getPushed_at());
        OffsetDateTime now = OffsetDateTime.now();
        long days = ChronoUnit.DAYS.between(lastPush, now);
        if (days <= 30) return 30;
        if (days <= 90) return 20;
        if (days <= 180) return 10;
        return 0;
    }

    private int calculateArchiveScore(boolean archived) {
        return archived ? 0 : 20;
    }

    private int calculateLicenseScore(RepositoryResponse repo) {
        return repo.getLicense() == null ? 0 : 10;
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

        if (communityScore < 50) {
            recommendations.add("Improve community engagement by attracting contributors and stars.");
        }

        if (!hasActions) {
            recommendations.add("Add GitHub Actions CI/CD workflow.");
        }

        if (repo.getLicense() == null) {
            recommendations.add("Add an open-source license.");
        }

        if (!repo.isHas_wiki()) {
            recommendations.add("Enable GitHub Wiki for better documentation.");
        }

        return recommendations;
    }


    // ==========================================================
    // Grade Calculation
    // ==========================================================

    private String calculateGrade(double percentage) {

        if (percentage >= 90)
            return "A";

        if (percentage >= 75)
            return "B";

        if (percentage >= 60)
            return "C";

        if (percentage >= 40)
            return "D";

        return "F";
    }
}