package com.example.dry.Item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatListModel {

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
    @SerializedName("time")
    private String time;
    @Expose
    @SerializedName("receiver")
    private String receiver;
    @Expose
    @SerializedName("sender_profile")
    private String sender_profile;
    @Expose
    @SerializedName("read_cnt_receiver")
    private String read_cnt_receiver;
    @Expose
    @SerializedName("read_cnt_sender")
    private String read_cnt_sender;

    public String getRead_cnt_receiver() {
        return read_cnt_receiver;
    }

    public void setRead_cnt_receiver(String read_cnt_receiver) {
        this.read_cnt_receiver = read_cnt_receiver;
    }

    public String getRead_cnt_sender() {
        return read_cnt_sender;
    }

    public void setRead_cnt_sender(String read_cnt_sender) {
        this.read_cnt_sender = read_cnt_sender;
    }

    public String getSender_profile() {
        return sender_profile;
    }

    public void setSender_profile(String sender_profile) {
        this.sender_profile = sender_profile;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }
}


