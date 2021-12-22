package com.example.dry.Retrofit;

import android.app.Application;

import com.example.dry.Item.BoardItem;
import com.example.dry.Item.ReplyItem;

import java.util.ArrayList;

public class FileModel extends Application {
    public String message;
    public String content;
    public String contentImage;
    public Boolean status;
    public String email;
    public String image;
    public String currentPassword;
    public String newPassword;
    public String newPasswordCheck;
    public String idx;
    public String originImg;
    public ArrayList<BoardItem> data;
    public ArrayList<ReplyItem> replyData;

    public ArrayList<ReplyItem> getReplyData() {
        return replyData;
    }

    public void setReplyData(ArrayList<ReplyItem> replyData) {
        this.replyData = replyData;
    }

    public String getOriginImg() {
        return originImg;
    }

    public void setOriginImg(String originImg) {
        this.originImg = originImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentImage() {
        return contentImage;
    }

    public void setContentImage(String contentImage) {
        this.contentImage = contentImage;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public ArrayList<BoardItem> getData() {
        return data;
    }

    public void setData(ArrayList<BoardItem> data) {
        this.data = data;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordCheck() {
        return newPasswordCheck;
    }

    public void setNewPasswordCheck(String newPasswordCheck) {
        this.newPasswordCheck = newPasswordCheck;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
