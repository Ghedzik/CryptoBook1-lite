package com.example.shinkaryuk.passbook;

import android.content.Context;
import android.content.SharedPreferences;

public class RegUtils {
    static int EDIT_IN_WINDOW = 1;
    static int EDIT_IN_LIST = 0;

    final static String APP_PREFERENCES = "mysettings";
    final static String APP_PREFERENCES_SHOW_WIN_EDIT = "showwinedit";
    final static String APP_PREFERENCES_LENPSW = "lenpswd";
    public static final String APP_PREFERENCES_PSW = "pswd";

    private int howEdit = this.EDIT_IN_LIST; //как редактировать записи паролей и записок
    private int lenghtPass; //длина пароля, который генерирует программа

    private Context mContext;
    private SharedPreferences mSettings;

    public RegUtils(Context context){
        mContext = context;
        mSettings = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        init(context);
    }

    public int getHowEdit(){
        init(mContext);
        return howEdit;
    }

    public void setHowEdit(int mHowEdit){
        howEdit = mHowEdit;
        //добавить код для записи значения в реестр
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_PREFERENCES_SHOW_WIN_EDIT, mHowEdit);
        editor.apply();
        editor.commit();
        init(mContext);
    }

    public int getLenghtPass(){
        init(mContext);
        return lenghtPass;
    }

    public void setLenghtPass(int len){
        lenghtPass = len;
        //добавить код для записи значения в реестр
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_LENPSW, Integer.toString(len));
        editor.apply();
        editor.commit();
        init(mContext);
    }

    public void init(Context context){
//Достаем параметр, указывающий открывать окно для редактирования при создании новой записи или создавать прямо в списке, из настроек
        //SharedPreferences mSettings;

        if (mSettings.contains(APP_PREFERENCES_SHOW_WIN_EDIT)) {
            howEdit = mSettings.getInt(APP_PREFERENCES_SHOW_WIN_EDIT, EDIT_IN_WINDOW);
        }
        else {
            howEdit = EDIT_IN_WINDOW;
        }
//////////////////////////////////////////////////////

        if (mSettings.contains(APP_PREFERENCES_LENPSW)) {
            lenghtPass = Integer.parseInt(mSettings.getString(APP_PREFERENCES_LENPSW, "8"));
        }
        else {
            lenghtPass = 8;
        }
    }


    public void writeNewPass(String var){
        String hashPswd;
        SecretHelper sh;
        sh = new SecretHelper();
        hashPswd = sh.hashPass(var);
        String unHashStr = sh.unHashPass(hashPswd, var);

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_PSW, hashPswd);
        editor.apply();
    }
}
