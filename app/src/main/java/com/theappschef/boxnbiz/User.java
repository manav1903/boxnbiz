package com.theappschef.boxnbiz;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("title")
    private String title;
    @SerializedName("desc")
    private String desc;

    @SerializedName("image")
    private String imageUrl;

    public User(){}

    public User(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}