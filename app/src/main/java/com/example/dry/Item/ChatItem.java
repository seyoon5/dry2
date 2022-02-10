package com.example.dry.Item;

public class ChatItem {
    private String idx;
    private String profile;
    private String name;
    private String content;
    private String sendTime;
    private int viewType;    // 0일 시 왼쪽(상대가 보낸 메세지), 1일 시 중앙(~가 입장하셨습니다), 2일 시 오른쪽(내가 보낸 메세지)
    private String room_num;
    private String image;

    public ChatItem(String idx, String profile, String name, String content, String sendTime, int viewType, String room_numm) {
        this.idx = idx;
        this.profile = profile;
        this.name = name;
        this.content = content;
        this.sendTime = sendTime;
        this.viewType = viewType;
        this.room_num = room_num;

    }

    public ChatItem(String profile, String name, String content, String sendTime, String image, int viewType) {
        this.profile = profile;
        this.name = name;
        this.content = content;
        this.sendTime = sendTime;
        this.image = image;
        this.viewType = viewType;
    }

    public ChatItem(String profile, String name, String content, String sendTime, int viewType) {
        this.profile = profile;
        this.name = name;
        this.content = content;
        this.sendTime = sendTime;
        this.viewType = viewType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProfile() {
        return profile;
    }
    public void setProfile(String profile) {
        this.profile = profile;
    }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getSendTime() { return sendTime; }

    public void setSendTime(String sendTime) { this.sendTime = sendTime; }

    public int getViewType() { return viewType; }

    public void setViewType(int viewType) { this.viewType = viewType; }
}