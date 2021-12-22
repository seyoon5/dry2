package com.example.dry.Item;

import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class BoardItem {

    private String created, content, nick, profile, idx, replyCnt; //, viewpager2;
    private ArrayList<ViewPagerItem> viewpager2;

    public BoardItem(String created, String content, String nick, String profile, String idx, String replyCnt, ArrayList<ViewPagerItem> viewpager2){
        this.created = created;
        this.content = content;
        this.nick = nick;
        this.profile = profile;
        this.idx = idx;
        this.replyCnt = replyCnt;
        this.viewpager2 = viewpager2;
    }

    public BoardItem() {

    }

    public String getReplyCnt() {
        return replyCnt;
    }

    public void setReplyCnt(String replyCnt) {
        this.replyCnt = replyCnt;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public ArrayList<ViewPagerItem> getViewpager2() {
        return viewpager2;
    }

    public void setViewpager2(ArrayList<ViewPagerItem> viewpager2) {
        this.viewpager2 = viewpager2;
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