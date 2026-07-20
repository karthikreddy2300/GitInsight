package com.gitinsight.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    private String status;
    private String application;
    private String version;
    private String timestamp;

}