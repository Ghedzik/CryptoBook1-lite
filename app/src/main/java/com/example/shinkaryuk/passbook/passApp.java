package com.example.shinkaryuk.passbook;

import android.app.Application;

public class passApp extends Application {
    private String pass;
    private String searchStr="";
    private int showFavorites; //0 - показать все, 1 только избранные

    public String getPass(){
        return pass;
    }

    public void setPass(String var){
        pass = var;
    }

    public String getSearchStr(){
        return searchStr;
    }

    public void setSearchStr(String aStr){
        searchStr = aStr;
    }

    public int getShowFavorites(){
        return showFavorites;
    }

    public void setShowFavorites(int isFavorites){
        showFavorites = isFavorites;
    }
}
