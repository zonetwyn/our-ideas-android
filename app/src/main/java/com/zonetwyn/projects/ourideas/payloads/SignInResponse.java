package com.zonetwyn.projects.ourideas.payloads;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("token")
    @Expose
    private String token;

    public SignInResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
