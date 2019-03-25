package com.zonetwyn.projects.ourideas.payloads;

import com.zonetwyn.projects.ourideas.models.Subject;

public class IdeaData {

    private IdeaRequest request;
    private Subject subject;

    public IdeaData(IdeaRequest request, Subject subject) {
        this.request = request;
        this.subject = subject;
    }

    public IdeaRequest getRequest() {
        return request;
    }

    public void setRequest(IdeaRequest request) {
        this.request = request;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
