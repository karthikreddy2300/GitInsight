package com.gitinsight.backend.controller;

import com.gitinsight.backend.dto.HealthResponse;
import com.gitinsight.backend.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/api/health")
    public HealthResponse healthCheck() {
        return healthService.getStatus();
    }
}