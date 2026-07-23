package com.gitinsight.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.gitinsight.backend.dto.TechnologyStackResponse;
import com.gitinsight.backend.service.CiCdDetectionService;
import com.gitinsight.backend.service.TechnologyDetectionService;

@RestController
@RequestMapping("/api/repositories")
@CrossOrigin(origins = "*")
public class TechnologyStackController {

    private final TechnologyDetectionService technologyDetectionService;
    private final CiCdDetectionService ciCdDetectionService;

    public TechnologyStackController(
            TechnologyDetectionService technologyDetectionService,
            CiCdDetectionService ciCdDetectionService) {

        this.technologyDetectionService = technologyDetectionService;
        this.ciCdDetectionService = ciCdDetectionService;
    }

    @GetMapping("/{owner}/{repo}/technology-stack")
    public TechnologyStackResponse getTechnologyStack(
            @PathVariable String owner,
            @PathVariable String repo) {

        List<String> technologies =
                technologyDetectionService.detectTechnologies(owner, repo);

        List<String> ciCdTools =
                ciCdDetectionService.detectCiCdTools(owner, repo);

        return new TechnologyStackResponse(
                technologies,
                ciCdTools
        );
    }
}