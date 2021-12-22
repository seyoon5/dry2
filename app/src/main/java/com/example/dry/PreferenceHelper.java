package com.example.dry;

import android.content.Context;
import android.content.SharedPreferences;

import javax.xml.namespace.NamespaceContext;

public class PreferenceHelper {
    private final String EMAIL = "email";
    private final String NICK = "nick";
    private final String IMAGE = "image";
    private final String USER = "user";
    private final String PW = "password";
    private final String BOARD_IDX = "board_idx";
    private SharedPreferences app_prefs;
    private Context context;


    public PreferenceHelper(Context context){
        app_prefs = context.getSharedPreferences("shared",0);
        this.context = context;
    }
    public void putIsLogin(boolean loginOrOut){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putBoolean(USER, loginOrOut);
        editor.apply();
    }

    public void putNick(String loginOrOut){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(NICK, loginOrOut);
        editor.apply();
    }
    public void putEmail(String loginOrOut){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(EMAIL, loginOrOut);
        editor.apply();
    }
    public void putImage(String image){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(IMAGE, image);
        editor.apply();}

    public void putBoard_idx(String board_idx){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(BOARD_IDX, board_idx);
        editor.apply();
    }

    public void putPw(String image){
        SharedPreferences.Editor editor = app_prefs.edit();
        editor.putString(PW, image);
        editor.apply();}

    public String getImage(){
        return app_prefs.getString(IMAGE, "");
    }
    public String getNICK(){
        return app_prefs.getString(NICK, "");
    }
    public String getBOARD_IDX(){
        return app_prefs.getString(BOARD_IDX, "");
    }
    public String getEMAIL(){
        return app_prefs.getString(EMAIL, "");
    }
    public String getPw(){
        return app_prefs.getString(PW, "");
    }
}
