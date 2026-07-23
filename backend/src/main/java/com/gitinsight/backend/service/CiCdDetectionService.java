package com.gitinsight.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gitinsight.backend.client.GitHubApiClient;

@Service
public class CiCdDetectionService {

    private final GitHubApiClient gitHubApiClient;

    public CiCdDetectionService(GitHubApiClient gitHubApiClient) {
        this.gitHubApiClient = gitHubApiClient;
    }

    public List<String> detectCiCdTools(String owner, String repo) {

        List<String> tools = new ArrayList<>();

        // GitHub Actions
        if (gitHubApiClient.hasGitHubActions(owner, repo)) {
            tools.add("GitHub Actions");
        }

        // Jenkins
        if (gitHubApiClient.pathExists(owner, repo, "Jenkinsfile")) {
            tools.add("Jenkins");
        }

        // GitLab CI
        if (gitHubApiClient.pathExists(owner, repo, ".gitlab-ci.yml")) {
            tools.add("GitLab CI");
        }

        // CircleCI
        if (gitHubApiClient.pathExists(owner, repo, ".circleci/config.yml")) {
            tools.add("CircleCI");
        }

        // Travis CI
        if (gitHubApiClient.pathExists(owner, repo, ".travis.yml")) {
            tools.add("Travis CI");
        }

        // Azure Pipelines
        if (gitHubApiClient.pathExists(owner, repo, "azure-pipelines.yml")) {
            tools.add("Azure Pipelines");
        }

        return tools;
    }
}