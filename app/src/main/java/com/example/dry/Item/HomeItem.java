package com.example.dry.Item;

public class HomeItem {
    String name, address, tele, info;

    public HomeItem(String name, String address, String tele, String info){
        this.address = address;
        this.info = info;
        this.name = name;
        this.tele = tele;
    }

    public HomeItem(String name, String address, String tele) {
        this.address = address;
        this.name = name;
        this.tele = tele;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTele() {
        return tele;
    }

    public void setTele(String tele) {
        this.tele = tele;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
