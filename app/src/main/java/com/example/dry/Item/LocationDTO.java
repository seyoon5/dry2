package com.example.dry.Item;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LocationDTO {
    @SerializedName("documents")
    private ArrayList<LocationItem> documents;

    public ArrayList<LocationItem> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<LocationItem> documents) {
        this.documents = documents;
    }
}
