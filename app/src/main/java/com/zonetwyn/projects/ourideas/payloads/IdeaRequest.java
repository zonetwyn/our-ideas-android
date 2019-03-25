package com.zonetwyn.projects.ourideas.payloads;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.zonetwyn.projects.ourideas.models.Domain;
import com.zonetwyn.projects.ourideas.models.Subject;

import java.util.ArrayList;
import java.util.List;

public class IdeaRequest {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;

    public IdeaRequest() {
    }

    public IdeaRequest(String title, String description) {
        this.title = title;
        this.description = description;
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
}
