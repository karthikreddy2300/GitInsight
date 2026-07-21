package com.gitinsight.backend.dto;

public class ContributorResponse {

    private String login;
    private int contributions;

    public ContributorResponse() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }
}