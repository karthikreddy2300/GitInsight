package com.gitinsight.backend.controller;

import com.gitinsight.backend.dto.ScoreResponse;
import com.gitinsight.backend.service.RepositoryAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
public class RepositoryAnalysisController {

    private final RepositoryAnalysisService repositoryAnalysisService;

    public RepositoryAnalysisController(
            RepositoryAnalysisService repositoryAnalysisService) {

        this.repositoryAnalysisService = repositoryAnalysisService;
    }

    @GetMapping("/analyze/{owner}/{repo}")
    public ScoreResponse analyzeRepository(
            @PathVariable String owner,
            @PathVariable String repo) {

        return repositoryAnalysisService.calculateScore(owner, repo);

    }
}