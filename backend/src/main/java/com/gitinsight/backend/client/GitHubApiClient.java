package com.gitinsight.backend.client;

import com.gitinsight.backend.dto.GitHubUserResponse;
import com.gitinsight.backend.dto.RepositoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubApiClient {

    private final RestClient restClient;

    public GitHubApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GitHubUserResponse getUser(String username) {

        return restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubUserResponse.class);

    }

    public RepositoryResponse getRepository(String owner, String repo) {

        return restClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .body(RepositoryResponse.class);

    }
}