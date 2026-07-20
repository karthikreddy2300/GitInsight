package com.gitinsight.backend.service;

import com.gitinsight.backend.dto.RepositoryResponse;
import com.gitinsight.backend.client.GitHubApiClient;
import com.gitinsight.backend.dto.GitHubUserResponse;
import org.springframework.stereotype.Service;

@Service
public class GitHubService {

    private final GitHubApiClient gitHubApiClient;

    public GitHubService(GitHubApiClient gitHubApiClient) {
        this.gitHubApiClient = gitHubApiClient;
    }

    public GitHubUserResponse getUser(String username) {
        return gitHubApiClient.getUser(username);
    }

    public RepositoryResponse getRepository(String owner, String repo) {
        return gitHubApiClient.getRepository(owner, repo);
    }
}
