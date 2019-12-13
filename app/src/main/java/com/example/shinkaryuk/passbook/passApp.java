package com.example.shinkaryuk.passbook;

import android.app.Application;
import android.content.SharedPreferences;

public class passApp extends Application {
    private String pass;
    private String searchStr="";
    private int showFavorites; //0 - показать все, 1 только избранные

    public String getPass(){
        return pass;
    }

    public void setPass(String var){
        RegUtils ru = new RegUtils(this);
        ru.writeNewPass(var);
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
