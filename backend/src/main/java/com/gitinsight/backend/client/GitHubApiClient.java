package com.gitinsight.backend.client;

import com.gitinsight.backend.dto.CommitResponse;
import com.gitinsight.backend.dto.ContributorResponse;
import com.gitinsight.backend.dto.GitHubUserResponse;
import com.gitinsight.backend.dto.ReadmeResponse;
import com.gitinsight.backend.dto.RepositoryResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Component
public class GitHubApiClient {

    private final RestClient restClient;

    public GitHubApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    // ========================= User =========================
    public GitHubUserResponse getUser(String username) {
        return restClient.get()
                .uri("/users/{username}", username)
                .retrieve()
                .body(GitHubUserResponse.class);
    }

    // ========================= Repository =========================
    public RepositoryResponse getRepository(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .body(RepositoryResponse.class);
    }

    // ========================= README =========================
    public ReadmeResponse getReadme(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/readme", owner, repo)
                .retrieve()
                .body(ReadmeResponse.class);
    }

    // ========================= File Content =========================
    public String getDecodedFileContent(String owner, String repo, String path) {
        try {
            ReadmeResponse file = restClient.get()
                    .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                    .retrieve()
                    .body(ReadmeResponse.class);

            if (file == null || file.getContent() == null) {
                return "";
            }

            return new String(
                Base64.getMimeDecoder().decode(file.getContent()),
                StandardCharsets.UTF_8
            );
        } catch (RestClientResponseException e) {
            return "";
        }
    }

    // ========================= GitHub Actions =========================
    public boolean hasGitHubActions(String owner, String repo) {
        return pathExists(owner, repo, ".github/workflows");
    }

    // ========================= Contributors =========================
    public ContributorResponse[] getContributors(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/contributors", owner, repo)
                .retrieve()
                .body(ContributorResponse[].class);
    }

    // ========================= Commits =========================
    public CommitResponse[] getCommits(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/commits?per_page=100", owner, repo)
                .retrieve()
                .body(CommitResponse[].class);
    }

    // ========================= Path Existence Check =========================
    public boolean pathExists(String owner, String repo, String path) {
        try {
            restClient.get()
                    .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                    .retrieve()
                    .body(String.class);
            return true;
        } catch (RestClientResponseException e) {
            return false;
        }
    }
}
