package com.example.dry.Item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatDTO {

    @Expose
    @SerializedName("idx")
    private String idx;
    @Expose
    @SerializedName("user")
    private String user;
    @Expose
    @SerializedName("receiver_profile")
    private String receiver_profile;
    @Expose
    @SerializedName("sender")
    private String sender;
    @Expose
    @SerializedName("content")
    private String content;
    @Expose
    @SerializedName("receiver")
    private String receiver;
    @Expose
    @SerializedName("sender_profile")
    private String sender_profile;
    @Expose
    @SerializedName("people")
    private String people;

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

}

//art+insert로 getter/setter 추가 단축키