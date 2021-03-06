package com.shinkaryuk.passbook;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

public class SnackbarHelper {
    static Snackbar snackbar;
    static Context mContext;


    public SnackbarHelper(Context context, View v, String msg){
        mContext = context;
        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_SHORT);
        //addMargins();
        setRoundBorders();
        ViewCompat.setElevation(snackbar.getView(), 6f);
    }

    public SnackbarHelper(Context context, View v, String msg, boolean isWarning){
        mContext = context;
        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_SHORT);
        //addMargins();
        setRoundBordersWarning();
        ViewCompat.setElevation(snackbar.getView(), 6f);
    }

    public SnackbarHelper(Context context, View v, String msg, Integer isLong){
        mContext = context;
        snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG);
        //addMargins();
        setRoundBorders();
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

    public static SnackbarHelper showL(Context context, View v, String msg){
        SnackbarHelper snackHelper = new SnackbarHelper(context, v, msg, 1);

        snackHelper.snackbar.show();
        return snackHelper;
    }
    public static void showAbout(Context context) {
        String versionInfo = context.getResources().getString(R.string.version_info) + " " + BuildConfig.VERSION_CODE + "." + BuildConfig.VERSION_NAME + "\n\n";
        new AlertDialog.Builder(context) .setMessage(versionInfo + context.getResources().getString(R.string.about_message)) .setPositiveButton(android.R.string.ok, null) .show();
        //return null;
    }
}
