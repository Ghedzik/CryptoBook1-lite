package com.shinkaryuk.passbook;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {

    public DateTimeHelper(){}

    public String getCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public String substractDate(@NonNull String fullDateTime){
        return fullDateTime.substring(1, fullDateTime.indexOf(" ")).trim();
    }

    public String normalFormatDate(@NonNull String fullDateTime){
        return fullDateTime.substring(7, 8) + "." + fullDateTime.substring(5, 6) + "." + fullDateTime.substring(1, 4);
    }

    public String normalFormatDateTime(@NonNull String fullDateTime){
        return fullDateTime.substring(7, 8) + "."
                + fullDateTime.substring(5, 6) + "."
                + fullDateTime.substring(1, 4) + " "
                + fullDateTime.substring(10, 14);
    }

    public String normalFormatDateFromDate(@NonNull String onlyDate){
        return onlyDate.substring(7, 8) + "."
                + onlyDate.substring(5, 6) + "."
                + onlyDate.substring(1, 4);
    }

    public String getNormalCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public String getCurrentDateTimeLikeSQLite(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public String getNormalCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public String convertNormalDateToSQLite(@NonNull String normalOnlyDate){
        String result = normalOnlyDate;
        if (result.indexOf(".") == 2) {
            result = result.substring(6, 10) + "-"
                    + result.substring(3, 5) + "-"
                    + result.substring(0, 2);
        } else if (result.indexOf(".") == 4) {
            result = result.replace(".", "-");
        }
        return result;
    }

    public String convertSQLiteDateToNormal(@NonNull String sqliteOnlyDate){
        return sqliteOnlyDate.substring(9, 10) + "."
                + sqliteOnlyDate.substring(6, 7) + "."
                + sqliteOnlyDate.substring(1, 4);
    }

    public String getCurrentDateLikeSQLite(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
