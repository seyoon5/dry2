package com.example.dry.Item;

import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ReplyItem {

    private String created, content, nick, profile, board_idx, idx, parent, delete;

    public ReplyItem(String created, String content, String nick, String profile, String idx,
                     String board_idx, String parent, String delete){
        this.created = created;
        this.content = content;
        this.nick = nick;
        this.profile = profile;
        this.idx = idx;
        this.board_idx = board_idx;
        this.parent = parent;
        this.delete = delete;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getBoard_idx() {
        return board_idx;
    }

    public void setBoard_idx(String board_idx) {
        this.board_idx = board_idx;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}