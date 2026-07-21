package com.gitinsight.backend.dto;

import java.util.List;

public class ScoreResponse {

    private String repositoryName;
    private String owner;
    private String language;
    private String defaultBranch;
    private String license;
    private int stars;
    private int forks;
    private int watchers;
    private int openIssues;
    private String lastUpdated;
    private boolean archived;

    // ✅ New fields
    private int repositorySize;
    private int repositoryAgeDays;
    private int topicCount;
    private int daysSinceLastCommit;
    private String visibility;

    // ✅ Contributor fields
    private int contributorCount;
    private String topContributor;
    private int topContributorCommits;

    // ✅ Commit activity fields
    private int commitsLastWeek;
    private int commitsLastMonth;
    private String repositoryStatus;

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

    // ✅ New getters and setters
    public int getRepositorySize() {
        return repositorySize;
    }

    public void setRepositorySize(int repositorySize) {
        this.repositorySize = repositorySize;
    }

    public int getRepositoryAgeDays() {
        return repositoryAgeDays;
    }

    public void setRepositoryAgeDays(int repositoryAgeDays) {
        this.repositoryAgeDays = repositoryAgeDays;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    public int getDaysSinceLastCommit() {
        return daysSinceLastCommit;
    }

    public void setDaysSinceLastCommit(int daysSinceLastCommit) {
        this.daysSinceLastCommit = daysSinceLastCommit;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public int getContributorCount() {
        return contributorCount;
    }

    public void setContributorCount(int contributorCount) {
        this.contributorCount = contributorCount;
    }

    public String getTopContributor() {
        return topContributor;
    }

    public void setTopContributor(String topContributor) {
        this.topContributor = topContributor;
    }

    public int getTopContributorCommits() {
        return topContributorCommits;
    }

    public void setTopContributorCommits(int topContributorCommits) {
        this.topContributorCommits = topContributorCommits;
    }

    public int getCommitsLastWeek() {
        return commitsLastWeek;
    }

    public void setCommitsLastWeek(int commitsLastWeek) {
        this.commitsLastWeek = commitsLastWeek;
    }

    public int getCommitsLastMonth() {
        return commitsLastMonth;
    }

    public void setCommitsLastMonth(int commitsLastMonth) {
        this.commitsLastMonth = commitsLastMonth;
    }

    public String getRepositoryStatus() {
        return repositoryStatus;
    }

    public void setRepositoryStatus(String repositoryStatus) {
        this.repositoryStatus = repositoryStatus;
    }

    // Existing getters and setters
    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
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
