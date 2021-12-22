package com.example.dry.Item;

import android.net.Uri;

import java.util.ArrayList;

public class BoardUpdateItem {
    private String Image;

    public BoardUpdateItem(String image) {
        Image = image;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
