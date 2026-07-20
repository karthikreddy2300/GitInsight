package com.gitinsight.backend.controller;

import com.gitinsight.backend.dto.GitHubUserResponse;
import com.gitinsight.backend.dto.RepositoryResponse;
import com.gitinsight.backend.service.GitHubService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService service;

    public GitHubController(GitHubService service) {
        this.service = service;
    }

    @GetMapping("/user/{username}")
    public GitHubUserResponse getUser(
            @PathVariable String username
    ) {
        return service.getUser(username);
    }

    @GetMapping("/repository/{owner}/{repo}")
    public RepositoryResponse getRepository(
            @PathVariable String owner,
            @PathVariable String repo
    ) {
        return service.getRepository(owner, repo);
    }
}
