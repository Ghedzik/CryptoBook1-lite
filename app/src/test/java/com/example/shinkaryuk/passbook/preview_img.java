package com.example.shinkaryuk.passbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ZoomControls;


public class preview_img extends AppCompatActivity {

    private float scaleWidth = 1;
    private float scaleHeight = 1;
    private Bitmap bitmap;
    protected WebView webView;
    FrameLayout mContentView;
    String nameDoc;
    String isCrypt = "";

/*************************************************************************************************/
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private boolean mVisible;

/*************************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_img);

/**************************************************************************************************/
        mVisible = true;
        mContentView = (FrameLayout)findViewById(R.id.flImage);
        webView = (WebView) findViewById(R.id.wvImage);

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
                if (bitmap.getWidth() > bitmap.getHeight()){

                    webView.getLayoutParams().height = mContentView.getWidth();
                    webView.getLayoutParams().width = mContentView.getHeight();
                    webView.setTop(mContentView.getTop());
                    webView.setLeft(mContentView.getLeft());
                    //webView.setRotation(90);
                }

                webView.setRotation(90);
                changeContent();
            }
        });
/**************************************************************************************************/

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.hide();
        }


        Uri aUri = Uri.parse(getIntent().getExtras().getString("imageUri"));//работа с файлами
        nameDoc = getIntent().getExtras().getString("imgName");//работа с файлами
        isCrypt = getIntent().getExtras().getString("isCrypt");

        this.setTitle(nameDoc);

//        webView = (WebView) findViewById(R.id.wvImage);
        webView.setBackgroundColor(Color.BLACK);
        //включаем поддержку масштабирования
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        //больше места для картинки
        webView.setPadding(0, 0, 0, 0);
        //полосы прокрутки – внутри изображения, увеличение места для просмотра
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        String strURI = aUri.getPath(); //работа с файлами
        if (isCrypt.equals("0")) {
            String fileURL = "file://" + aUri.getPath().toString();
            webView.loadUrl(fileURL);
            bitmap = BitmapFactory.decodeFile(strURI);
        } else if (isCrypt.equals("1")){
            SecretHelper sh = new SecretHelper();
            DbBitmapUtility dbu = new DbBitmapUtility();
            String strBitmap = sh.getDecodeBitmapToStr(this, Uri.parse(strURI), ((passApp)getApplication()).getPass());
            //String dataImage = "data:image/jpg;base64," + strBitmap;
            bitmap = dbu.convertBase64ToBitmap(strBitmap);
            webView.loadUrl("data:image/jpg;base64," + strBitmap);
        }
        //webView.lo
        changeContent();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        changeContent();
    }

    private void changeContent(){
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = webView.getWidth();//display.getWidth();
        if (width == 0){
            width = display.getWidth();
        }
        int height = webView.getHeight();//display.getHeight();
        if (height == 0){
            height = display.getHeight();
        }

        //Bitmap img = Bitmap bitmap = BitmapFactory.decodeFile(FilePath, options);
/*        if (bitmap.getWidth() > bitmap.getHeight()){

            webView.getLayoutParams().height = mContentView.getWidth();
            webView.getLayoutParams().width = mContentView.getHeight();
            webView.setTop(mContentView.getTop());
            webView.setLeft(mContentView.getLeft());
            webView.setRotation(90);
        }*/

        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Double val = 1d;

        //if (picHeight > height)
        val = new Double(height) / new Double(picHeight);

        if (val > (new Double(width) / new Double(picWidth))){
            val = new Double(width) / new Double(picWidth);
        }

        val = val * 100d;

        webView.setInitialScale( val.intValue() );
    }

/**************************************************************************************************/
    public void onClickwvImage(View view) {
        toggle();
        if (bitmap.getWidth() > bitmap.getHeight()){

            webView.getLayoutParams().height = mContentView.getWidth();
            webView.getLayoutParams().width = mContentView.getHeight();
            webView.setTop(mContentView.getTop());
            webView.setLeft(mContentView.getLeft());
            webView.setRotation(90);
        }
        changeContent();
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
/*
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);*/
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        /*mContentView*/webView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;
    }



/**************************************************************************************************/

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

}
