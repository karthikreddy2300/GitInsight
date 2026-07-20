package com.gitinsight.backend.service;

import com.gitinsight.backend.dto.HealthResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HealthService {

    public HealthResponse getStatus() {

        return new HealthResponse(
                "UP",
                "GitInsight Backend",
                "1.0.0",
                LocalDateTime.now().toString()
        );

    }

}