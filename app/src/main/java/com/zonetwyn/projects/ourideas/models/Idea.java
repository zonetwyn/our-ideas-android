package com.zonetwyn.projects.ourideas.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Idea implements Parcelable {

    @SerializedName("likes")
    @Expose
    private int likes;
    @SerializedName("unlikes")
    @Expose
    private int unlikes;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public Idea() {

    }

    protected Idea(Parcel in) {
        likes = in.readInt();
        unlikes = in.readInt();
        status = in.readString();
        id = in.readString();
        title = in.readString();
        description = in.readString();
        subject = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        createdAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(likes);
        dest.writeInt(unlikes);
        dest.writeString(status);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(subject);
        dest.writeParcelable(user, flags);
        dest.writeString(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Idea> CREATOR = new Creator<Idea>() {
        @Override
        public Idea createFromParcel(Parcel in) {
            return new Idea(in);
        }

        @Override
        public Idea[] newArray(int size) {
            return new Idea[size];
        }
    };

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getUnlikes() {
        return unlikes;
    }

    public void setUnlikes(int unlikes) {
        this.unlikes = unlikes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
