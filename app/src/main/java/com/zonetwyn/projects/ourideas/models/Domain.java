package com.zonetwyn.projects.ourideas.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Domain implements Parcelable {

    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;

    // for ui
    private boolean selected;
    private boolean remove;

    public Domain() {

    }

    public Domain(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.selected = false;
    }

    protected Domain(Parcel in) {
        createdAt = in.readString();
        id = in.readString();
        name = in.readString();
        description = in.readString();
        selected = in.readByte() != 0;
        remove = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(createdAt);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (remove ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Domain> CREATOR = new Creator<Domain>() {
        @Override
        public Domain createFromParcel(Parcel in) {
            return new Domain(in);
        }

        @Override
        public Domain[] newArray(int size) {
            return new Domain[size];
        }
    };

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }
}
