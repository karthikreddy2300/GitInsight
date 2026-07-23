package com.gitinsight.backend.dto;

import java.util.List;

public class TechnologyStackResponse {

    private List<String> technologies;
    private List<String> ciCdTools;

    public TechnologyStackResponse() {
    }

    public TechnologyStackResponse(List<String> technologies, List<String> ciCdTools) {
        this.technologies = technologies;
        this.ciCdTools = ciCdTools;
    }

    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies;
    }

    public List<String> getCiCdTools() {
        return ciCdTools;
    }

    public void setCiCdTools(List<String> ciCdTools) {
        this.ciCdTools = ciCdTools;
    }
}