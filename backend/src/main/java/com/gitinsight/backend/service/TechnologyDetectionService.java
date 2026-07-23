package com.gitinsight.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gitinsight.backend.client.GitHubApiClient;

@Service
public class TechnologyDetectionService {

    private final GitHubApiClient gitHubApiClient;

    public TechnologyDetectionService(GitHubApiClient gitHubApiClient) {
        this.gitHubApiClient = gitHubApiClient;
    }

    public List<String> detectTechnologies(String owner, String repo) {

        List<String> technologies = new ArrayList<>();

        // ================= Java =================
        if (gitHubApiClient.pathExists(owner, repo, "pom.xml")) {

            technologies.add("Maven");

            String pom = gitHubApiClient.getDecodedFileContent(owner, repo, "pom.xml")
                                        .toLowerCase();

            if (pom.contains("spring-boot")) technologies.add("Spring Boot");
            if (pom.contains("hibernate")) technologies.add("Hibernate");
            if (pom.contains("jpa")) technologies.add("Spring Data JPA");
            if (pom.contains("mysql")) technologies.add("MySQL");
            if (pom.contains("postgresql")) technologies.add("PostgreSQL");
            if (pom.contains("mongodb")) technologies.add("MongoDB");
            if (pom.contains("lombok")) technologies.add("Lombok");
        }

        // ================= Gradle =================
        if (gitHubApiClient.pathExists(owner, repo, "build.gradle")
                || gitHubApiClient.pathExists(owner, repo, "build.gradle.kts")) {

            technologies.add("Gradle");
        }

        // ================= Node =================
        if (gitHubApiClient.pathExists(owner, repo, "package.json")) {

            technologies.add("Node.js");

            String packageJson = gitHubApiClient
                    .getDecodedFileContent(owner, repo, "package.json")
                    .toLowerCase();

            if (packageJson.contains("\"react\"")) technologies.add("React");
            if (packageJson.contains("\"next\"")) technologies.add("Next.js");
            if (packageJson.contains("\"vue\"")) technologies.add("Vue.js");
            if (packageJson.contains("\"angular\"")) technologies.add("Angular");
            if (packageJson.contains("\"express\"")) technologies.add("Express.js");
            if (packageJson.contains("\"nestjs\"")) technologies.add("NestJS");
            if (packageJson.contains("\"typescript\"")) technologies.add("TypeScript");
            if (packageJson.contains("\"tailwindcss\"")) technologies.add("Tailwind CSS");
        }

        // ================= Python =================
        String pythonFile = "";

        if (gitHubApiClient.pathExists(owner, repo, "requirements.txt")) {
            pythonFile = "requirements.txt";
        } else if (gitHubApiClient.pathExists(owner, repo, "pyproject.toml")) {
            pythonFile = "pyproject.toml";
        } else if (gitHubApiClient.pathExists(owner, repo, "Pipfile")) {
            pythonFile = "Pipfile";
        }

        if (!pythonFile.isEmpty()) {
            technologies.add("Python");

            String content = gitHubApiClient
                    .getDecodedFileContent(owner, repo, pythonFile)
                    .toLowerCase();

            if (content.contains("django")) technologies.add("Django");
            if (content.contains("flask")) technologies.add("Flask");
            if (content.contains("fastapi")) technologies.add("FastAPI");
        }

        // ================= Docker =================
        if (gitHubApiClient.pathExists(owner, repo, "Dockerfile"))
            technologies.add("Docker");

        // ================= Kubernetes =================
        if (gitHubApiClient.pathExists(owner, repo, "kubernetes")
                || gitHubApiClient.pathExists(owner, repo, "k8s")
                || gitHubApiClient.pathExists(owner, repo, "helm")
                || gitHubApiClient.pathExists(owner, repo, "charts")
                || gitHubApiClient.pathExists(owner, repo, "deployment.yaml")
                || gitHubApiClient.pathExists(owner, repo, "deployment.yml")) {

            technologies.add("Kubernetes");
        }

        return technologies.stream().distinct().toList();
    }
}
