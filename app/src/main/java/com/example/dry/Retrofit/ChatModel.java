package com.example.dry.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatModel {

    @Expose
    @SerializedName("idx")
    private String idx;
    @Expose
    @SerializedName("receiver_profile")
    private String receiver_profile;
    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("receiver")
    private String receiver;
    @Expose
    @SerializedName("sender_profile")
    private String sender_profile;
    @Expose
    @SerializedName("contents")
    private String contents;
    @Expose
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("identity")
    private String identity;
    @Expose
    @SerializedName("profile")
    private String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getReceiver_profile() {
        return receiver_profile;
    }

    public void setReceiver_profile(String receiver_profile) {
        this.receiver_profile = receiver_profile;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender_profile() {
        return sender_profile;
    }

    public void setSender_profile(String sender_profile) {
        this.sender_profile = sender_profile;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}