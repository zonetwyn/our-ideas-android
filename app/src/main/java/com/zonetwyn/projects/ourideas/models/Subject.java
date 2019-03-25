package com.zonetwyn.projects.ourideas.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Subject implements Parcelable {

    @SerializedName("likes")
    @Expose
    private int likes;
    @SerializedName("unlikes")
    @Expose
    private int unlikes;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("ideasCount")
    @Expose
    private int ideasCount;
    @SerializedName("domains")
    @Expose
    private List<Domain> domains;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public Subject() {

    }

    protected Subject(Parcel in) {
        likes = in.readInt();
        unlikes = in.readInt();
        status = in.readString();
        ideasCount = in.readInt();
        domains = in.createTypedArrayList(Domain.CREATOR);
        id = in.readString();
        title = in.readString();
        description = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        createdAt = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(likes);
        dest.writeInt(unlikes);
        dest.writeString(status);
        dest.writeInt(ideasCount);
        dest.writeTypedList(domains);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeParcelable(user, flags);
        dest.writeString(createdAt);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
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

    public int getIdeasCount() {
        return ideasCount;
    }

    public void setIdeasCount(int ideasCount) {
        this.ideasCount = ideasCount;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
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
