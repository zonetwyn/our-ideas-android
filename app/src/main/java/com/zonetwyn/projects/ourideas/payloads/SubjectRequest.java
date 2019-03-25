package com.zonetwyn.projects.ourideas.payloads;

import com.zonetwyn.projects.ourideas.models.Domain;

import java.util.ArrayList;
import java.util.List;

public class SubjectRequest {

    private String title;
    private String description;
    private List<String> domains;

    public SubjectRequest() {
    }

    public SubjectRequest(String title, String description, List<Domain> domains) {
        this.title = title;
        this.description = description;
        this.domains = new ArrayList<>();
        for (Domain domain : domains) {
            this.domains.add(domain.getId());
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }
}
