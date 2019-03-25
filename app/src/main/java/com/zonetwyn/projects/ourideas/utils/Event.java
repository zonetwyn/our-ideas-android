package com.zonetwyn.projects.ourideas.utils;

public class Event {

    public static final int SUBJECT_FILTERS_DOMAINS = 100;
    public static final int SUBJECT_FILTERS_LIMIT_REACHED = 101;

    public static final int SUBJECT_HEADER_FILTER_CLICK = 110;

    public static final int SUBJECT_MAIN_NEW_SUBJECT = 120;
    public static final int SUBJECT_MAIN_SAVE_SUBJECT = 121;
    public static final int SUBJECT_MAIN_ACCOUNT = 122;
    public static final int SUBJECT_MAIN_NEW_IDEA = 123;
    public static final int SUBJECT_MAIN_SAVE_IDEA = 124;

    public static final int SUBJECT_HOME_UPDATE_COUNT = 125;

    private int subject;
    private Object data;

    public Event() {
    }

    public Event(int subject, Object data) {
        this.subject = subject;
        this.data = data;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
