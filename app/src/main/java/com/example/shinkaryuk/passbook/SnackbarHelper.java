package com.example.shinkaryuk.passbook;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

public class SnackbarHelper {
    static Snackbar snackbar;
    static Context mContext;


    public SnackbarHelper(Context context, View v, String msg){
        mContext = context;
        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
        //addMargins();
        setRoundBorders();
        ViewCompat.setElevation(snackbar.getView(), 6f);
    }

    public SnackbarHelper(Context context, View v, String msg, boolean isWarning){
        mContext = context;
        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
        //addMargins();
        setRoundBordersWarning();
        ViewCompat.setElevation(snackbar.getView(), 6f);
    }

    private static void addMargins() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(12, 12, 12, 12);
        snackbar.getView().setLayoutParams(params);
    }

    private static void setRoundBorders() {
        snackbar.getView().setBackground(mContext.getDrawable(R.drawable.toast_border));
    }

    private static void setRoundBordersWarning() {
        snackbar.getView().setBackground(mContext.getDrawable(R.drawable.toast_border_warning));
    }

    public static SnackbarHelper show(Context context, View v, String msg){
        SnackbarHelper snackHelper = new SnackbarHelper(context, v, msg);

        snackHelper.snackbar.show();
        return snackHelper;
    }

    public static SnackbarHelper showW(Context context, View v, String msg){
        SnackbarHelper snackHelper = new SnackbarHelper(context, v, msg, true);

        snackHelper.snackbar.show();
        return snackHelper;
    }
}
