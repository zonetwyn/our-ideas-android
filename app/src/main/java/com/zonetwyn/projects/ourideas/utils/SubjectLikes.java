package com.zonetwyn.projects.ourideas.utils;

import java.util.ArrayList;
import java.util.List;

public class SubjectLikes {

    private List<String> ids;

    public SubjectLikes() {
        ids = new ArrayList<>();
    }

    public void addSubjectLike(String id) {
        ids.add(id);
    }

    public List<String> getIds() {
        return ids;
    }

    public boolean contains(String id) {
        return ids.contains(id);
    }
}
