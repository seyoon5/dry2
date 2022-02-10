package com.example.dry.Item;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoomNoModel {

    @SerializedName("status")
    private String status;

    @Expose
    @SerializedName("idx")
    private String idx;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }
}
