package com.gitinsight.backend.client;

import com.gitinsight.backend.dto.ReadmeResponse;
import com.gitinsight.backend.dto.GitHubUserResponse;
import com.gitinsight.backend.dto.RepositoryResponse;
import com.gitinsight.backend.dto.ContributorResponse; // ✅ new import
import com.gitinsight.backend.dto.CommitResponse;      // ✅ added import
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

    public ReadmeResponse getReadme(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/readme", owner, repo)
                .retrieve()
                .body(ReadmeResponse.class);
    }

    public boolean hasGitHubActions(String owner, String repo) {
        try {
            restClient.get()
                    .uri("/repos/{owner}/{repo}/contents/.github/workflows",
                            owner,
                            repo)
                    .retrieve()
                    .body(String.class);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ New method to fetch contributors
    public ContributorResponse[] getContributors(String owner, String repo) {
        String url = "https://api.github.com/repos/"
                + owner
                + "/"
                + repo
                + "/contributors";

        return restClient.get()
                .uri(url)
                .retrieve()
                .body(ContributorResponse[].class);
    }

    // ✅ New method to fetch commits
    public CommitResponse[] getCommits(String owner, String repo) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/commits?per_page=100",
                        owner,
                        repo)
                .retrieve()
                .body(CommitResponse[].class);
    }
}
