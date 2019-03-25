package com.zonetwyn.projects.ourideas.utils;

import com.zonetwyn.projects.ourideas.models.Domain;

import java.util.List;

public interface OnFilterSelectedListener {
    void onFiltersSelected(List<Domain> domains);
    void onLimitReached();
}
