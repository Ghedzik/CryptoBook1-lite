package com.example.shinkaryuk.passbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

public class image_view extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ZoomableImageView ziv = (ZoomableImageView) findViewById(R.id.zivPreview);

        Uri aUri = Uri.parse(getIntent().getExtras().getString("imageUri"));//работа с файлами
        String nameDoc = getIntent().getExtras().getString("imgName");//работа с файлами
        String isCrypt = getIntent().getExtras().getString("isCrypt");

        Toolbar toolbar = (Toolbar) findViewById(R.id.ImgViewToolbar);
        toolbar.setTitle(nameDoc);
        setSupportActionBar(toolbar);

        ziv.setBackgroundColor(Color.BLACK);

        String strURI = aUri.getPath(); //работа с файлами
        if (isCrypt.equals("0")) {
            String fileURL = "file://" + aUri.getPath().toString();
            Bitmap bitmap = BitmapFactory.decodeFile(strURI);
            ziv.setBitmap(bitmap);//loadUrl(fileURL);

        } else if (isCrypt.equals("1")){
            SecretHelper sh = new SecretHelper();
            DbBitmapUtility dbu = new DbBitmapUtility();
            Bitmap bitmap = sh.getDecodeBitmap(this, Uri.parse(strURI), ((passApp)getApplication()).getPass());
            //String dataImage = "data:image/jpg;base64," + strBitmap;
            //Bitmap bitmap = dbu.convertBase64ToBitmap(strBitmap);
            ziv.setBitmap(bitmap);
        }

        this.setTitle(nameDoc);

    }
}
