package com.example.dry.Item;

public class ChatRoomItem {

    private String idx;
    private String user;
    private String receiver_profile;
    private String sender;
    private String content;
    private String time;
    private String receiver;
    private String sender_profile;
    private String read_cnt_receiver;
    private String read_cnt_sender;

    public ChatRoomItem(String idx, String user, String receiver_profile, String sender, String content,
                        String time, String receiver, String sender_profile, String read_cnt_receiver, String read_cnt_sender) {
        this.idx = idx;
        this.user = user;
        this.receiver_profile = receiver_profile;
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.receiver = receiver;
        this.sender_profile = sender_profile;
        this.read_cnt_receiver = read_cnt_receiver;
        this.read_cnt_sender = read_cnt_sender;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
