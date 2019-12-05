package com.example.shinkaryuk.passbook;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Application FinancialAccounting
 * Created by EvILeg on 26.07.2015.
 */
public class CustomToast extends Toast {

    private static TextView toastText;
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public CustomToast(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /*
         * Производится инициализация разметки всплывающего сообщения,
         * устанавливается изображение значка информации в всплывающее сообщение
         */
        View rootView = inflater.inflate(R.layout.toast_info, null);
        ImageView toastImage = (ImageView) rootView.findViewById(R.id.ivToastInfo);
        toastImage.setImageResource(R.mipmap.ic_action_info);
        toastText = (TextView) rootView.findViewById(R.id.tvToastInfo);

        /*
         * Устанавливается инициализированный внешний вид,
         * местоположение всплывающего сообщения на экране устройства,
         * а также длительность существованая сообщения
         */
        this.setView(rootView);
        //this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        this.setDuration(Toast.LENGTH_LONG);
    }

    /*
     * Метод вызова сообщения без установки длительности существования
     * с передачей сообщению текстовой информации в качестве последовательности
     * текстовых символов или строки
     */
    public static CustomToast makeText(Context context, CharSequence text) {
        CustomToast result = new CustomToast(context);
        toastText.setText(text);

        return result;
    }

    /*
     * Метод вызова сообщения с установкой длительности существования и
     * с передачей сообщению текстовой информации в качестве последовательности
     * текстовых символов или строки
     */
    public static CustomToast makeText(Context context, CharSequence text, int duration) {
        CustomToast result = new CustomToast(context);
        result.setDuration(duration);
        toastText.setText(text);

        return result;
    }

    /*
     * Метод вызова сообщения без установки длительности существования
     * с передачей сообщению ID текстового ресурса
     */
    public static Toast makeText(Context context, int resId)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId));
    }

    /*
     * Метод вызова сообщения с установкой длительности существования и
     * с передачей сообщению ID текстового ресурса
     */
    public static Toast makeText(Context context, int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }
}