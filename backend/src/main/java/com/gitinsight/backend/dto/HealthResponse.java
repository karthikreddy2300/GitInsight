package com.gitinsight.backend.dto;

public class HealthResponse {

    private String status;
    private String application;
    private String version;
    private String timestamp;

    public HealthResponse() {
    }

    public HealthResponse(String status, String application, String version, String timestamp) {
        this.status = status;
        this.application = application;
        this.version = version;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}