package com.zonetwyn.projects.ourideas.payloads;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("error")
    @Expose
    private String error;

    private String data;

    public ApiResponse() {

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
