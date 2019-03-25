package com.zonetwyn.projects.ourideas.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.zonetwyn.projects.ourideas.utils.SubjectLikes;

public class SessionManager {

    private static SessionManager sessionManager;
    private static String preferenceFile = "OurIdeasPreferenceFile";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private int mode = 0;

    private static String keySubjectLikes = "SubjectLikes";
    private static String keyToken = "Token";
    private static String keyUsername = "Username";
    private static String keyLoggedIn = "LoggedIn";

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(preferenceFile, mode);
        editor = preferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(context);
        }
        return sessionManager;
    }

    public void addSubjectLikes(String id) {
        String json = preferences.getString(keySubjectLikes, null);
        SubjectLikes likes = null;
        if (json == null) {
            likes = new SubjectLikes();
        } else {
            likes = new Gson().fromJson(json, SubjectLikes.class);
        }

        likes.addSubjectLike(id);
        editor.putString(keySubjectLikes, new Gson().toJson(likes));
        editor.commit();
    }

    public SubjectLikes getSubjectLikes() {
        String json = preferences.getString(keySubjectLikes, null);
        SubjectLikes likes = null;
        if (json != null) {
            likes = new Gson().fromJson(json, SubjectLikes.class);
        }
        return likes;
    }

    public void signIn(String token, String username) {
        editor.putString(keyToken, token);
        editor.putString(keyUsername, username);
        editor.putBoolean(keyLoggedIn, true);
        editor.commit();
    }

    public String getUsername() {
        return preferences.getString(keyUsername, null);
    }

    public String getToken() {
        return preferences.getString(keyToken, null);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(keyLoggedIn, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}
