package com.shinkaryuk.passbook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class addEditImage extends AppCompatActivity {

    public final static int RESULT_EDIT_OK = 1;
    public final static int RESULT_EDIT_DELETE = -2;
    private static final int READ_REQUEST_CODE = 42;
    static final int REQUEST_TAKE_PHOTO = 1;

    String intIdImg;
    String strNameImg;
    String strPathImg;
    String strCommentImg;
    String strShortPathImg;
    private ImageView ivImageEdit;
    String appPathFiles, extStorageDir;
    Boolean isEdit = false;
    String strDateCreate = "";
    String strDateChange = "";
    Bitmap bmpBitmap, jpgBitmap, smallBitmap;//эти файлы нужны для передачи их из интента в вызвавшую данное окно форму с последующим сохранением в БД
    Boolean isChangeCrypt = false;
    Boolean isImgCrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addEditImgToolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_addeditimg));
        setSupportActionBar(toolbar);

        appPathFiles = getApplication().getFilesDir().getAbsolutePath();
        extStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();

        ivImageEdit = (ImageView) findViewById(R.id.ivEditImage);

        ivImageEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //v.getId() will give you the image id
                showImage();
            }
        });

        Button btnDel = (Button) findViewById(R.id.btnDelete);
        Button btnExport = (Button) findViewById(R.id.btnExportImg);
        CheckBox cbIsCrypto = (CheckBox) findViewById(R.id.cb_IsCryptImg);

        intIdImg = getIntent().getExtras().getString("idImg");
        strNameImg = getIntent().getExtras().getString("nameImg");
        strPathImg = getIntent().getExtras().getString("pathImg");
        strCommentImg = getIntent().getExtras().getString("commentImg");
        strShortPathImg = getIntent().getExtras().getString("pathShortImg");

        //Раскомментировать после того как все добавим
        strDateCreate = getIntent().getExtras().getString("dateCreateImg");
        strDateChange = getIntent().getExtras().getString("dateChangeImg");
        isImgCrypt = getIntent().getExtras().getString("isCryptoImg").equals("1");

        cbIsCrypto.setChecked((getIntent().getExtras().getString("isCryptoImg").equals("1")) ||
                intIdImg.equals("0"));//флаг шифровать или нет);//флаг шифровать или нет
        cbIsCrypto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChangeCrypt = true;
            }
        });

        TextView tvNameImg = (TextView)findViewById(R.id.etNameImg);
        tvNameImg.setText(strNameImg);

        TextView tvPathImg = (TextView)findViewById(R.id.etPathImg);
        tvPathImg.setText(strPathImg);

        TextView tvCommentImg = (TextView)findViewById(R.id.etCommentImg);
        tvCommentImg.setText(strCommentImg);

        TextView tvDateCreate = (TextView) findViewById(R.id.tvDateCreateImg);
        TextView tvDateChange = (TextView) findViewById(R.id.tvDateChangeImg);

        if (intIdImg.equals("0")){
            btnDel.setVisibility(View.GONE);
            btnExport.setVisibility(View.GONE);
        }
//Вычисляем текущую дату и форматируем ее
        Date currentDate = new Date();
// Форматирование даты как "день.месяц.год"
        DateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        if ((strDateCreate.isEmpty()) || (strDateCreate =="")) {
            tvDateCreate.setText(dateText);
            tvDateChange.setText(dateText);

            strDateCreate = dateText;
            strDateChange = dateText;
        }
        else {
            tvDateCreate.setText(strDateCreate);
            tvDateChange.setText(dateText);

            strDateChange = dateText;
        }

        //формируем полное имя файла с путем
        String internalFileName = strPathImg;//getFilesDir() + "/" + strShortPathImg;

    // можно так устанавливать изображение
    //Сначала вычисляем опции для установки размера изображения, чтобы не тратить лишнюю память и не грузить полноразмерную картинку
        if (!intIdImg.equals("0")) {
            ivImageEdit.setScaleType(ImageView.ScaleType.FIT_CENTER);
            BitmapFactory.Options options = new BitmapFactory.Options();


            options.inSampleSize = calculateInSampleSize(options, ivImageEdit.getWidth(), ivImageEdit.getHeight());
            if (strShortPathImg != null) {
                if (cbIsCrypto.isChecked()){
                    SecretHelper sh = new SecretHelper();
                    Bitmap encodeBmp = sh.getDecodeBitmap(this, Uri.parse(internalFileName), ((passApp)getApplication()).getPass());
                    //Toast.makeText(this, "YES!", Toast.LENGTH_LONG).show();
                    //encodeBmp.setHeight(options.outHeight);
                    //encodeBmp.setWidth(options.outWidth);
                    ivImageEdit.setImageBitmap(encodeBmp);//aBmp);
                } else {
                    Bitmap aBmp = BitmapFactory.decodeFile(/*strPathImg*/ internalFileName, options);
                    ivImageEdit.setImageBitmap(aBmp);
                }
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        if (intIdImg.equals("0")) menu.removeItem(R.id.menuDelete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                okEdit(null);
                return true;
            case R.id.menuCancel:
                cancelEdit(null);
                return true;
            case R.id.menuDelete:
                deleteEdit(null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cancelEdit(View v){
        setResult(RESULT_CANCELED);
        //onClickClearFiles();
        finish();
    }

    public void okEdit(View v){
        Intent answerIntent = new Intent();

        TextView tvNameImg = (TextView)findViewById(R.id.etNameImg);

        TextView tvPathImg = (TextView)findViewById(R.id.etPathImg);

        TextView tvCommentImg = (TextView)findViewById(R.id.etCommentImg);

        CheckBox cbIsCrypto = (CheckBox) findViewById(R.id.cb_IsCryptImg);

        answerIntent.putExtra("idImgNew", intIdImg);
        answerIntent.putExtra("nameImgNew", tvNameImg.getText().toString());
        answerIntent.putExtra("pathImgNew", appPathFiles + "/" + Uri.parse(tvPathImg.getText().toString()).getLastPathSegment());//tvPathImg.getText().toString());
        answerIntent.putExtra("commentImgNew", tvCommentImg.getText().toString());

        answerIntent.putExtra("dateCreateImgNew", strDateCreate);//tvDateCreate.getText().toString());
        answerIntent.putExtra("dateChangeImgNew", strDateChange);//tvDateChange.getText().toString());

        String aPath = appPathFiles + "/" + Uri.parse(tvPathImg.getText().toString()).getLastPathSegment();//tvPathImg.getText().toString();
        if(!aPath.equals("")) {
            //Toast.makeText(this, "создаем файл", Toast.LENGTH_LONG).show();

            Uri aUri = Uri.parse(tvPathImg.getText().toString());
            boolean aCreateFile = false;
            if (!strPathImg.equals(aPath)){
                aCreateFile = (!createSmallImageFromBitmap(aUri) || !createJPGFromBitmap(aUri));// || !createImageFromBitmap(aUri));
            }

            if (cbIsCrypto.isChecked() != isImgCrypt){
                if (cbIsCrypto.isChecked()) {
                    SecretHelper sh = new SecretHelper();
                    sh.EncodeFile(this, aUri, ((passApp) getApplication()).getPass());
                } else {
                    SecretHelper sh = new SecretHelper();
                    sh.DecodeFile(this, aUri, ((passApp) getApplication()).getPass());
                }
                if (aCreateFile) {
                    SnackbarHelper.show(this, v,"Файлы кэша не могут быть созданы");
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        } else {
            if (aPath.equals("") & !strPathImg.equals("")) {
                //если файл был, но мы удалили путь, то удаляем файл
                deleteImgInternalFile(strShortPathImg);
                deleteSmallImgInternalFile("s_" + strShortPathImg);
            }
        }

        if (cbIsCrypto.isChecked()){
            answerIntent.putExtra("isCryptoNew", "1");
        } else {
            answerIntent.putExtra("isCryptoNew", "0");
        }

        setResult(RESULT_EDIT_OK, answerIntent);

        finish();
    }

    public void deleteEdit(View v){

        Intent intent = new Intent();

        intent.putExtra("idImgNew", intIdImg);

        deleteImgInternalFile(strShortPathImg);
        deleteJPGInternalFile(strShortPathImg);
        deleteSmallImgInternalFile("s_" + strShortPathImg);

        setResult(RESULT_EDIT_DELETE, intent);

        finish();
    }

    public void onClickPath (View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        isEdit = true;
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView tvPathImg = null;
        Uri aUri = null;
        tvPathImg = (TextView)findViewById(R.id.etPathImg);
        ImageView tmpImageView = (ImageView) findViewById(R.id.ivEditImage);

        if (isExternalStorageReadable()) {
            switch (requestCode) {
                case READ_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        PathUtils aPathUtils = new PathUtils();

                        aUri = data.getData();
                        String FilePath = aPathUtils.getPath(this, aUri); //data.getData().getPath();
                        String fileName = aUri.getLastPathSegment();

                        //Toast.makeText(this, FilePath, Toast.LENGTH_LONG).show();

                        String tmpStr = aUri.getAuthority();
                        SnackbarHelper.show(this, tvPathImg, tmpStr);

// можно так устанавливать изображение
//Сначала вычисляем опции для установки размера изображения, чтобы не тратить лишнюю память и не грузить полноразмерную картинку
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        //options.inSampleSize = calculateInSampleSize(options, tmpImageView.getWidth(), tmpImageView.getHeight());
                        Bitmap bitmap = BitmapFactory.decodeFile(FilePath, options);
                        tmpImageView.setImageBitmap(bitmap);

                        tvPathImg.setText(FilePath);

                    }
                    break;

//получаем данные от камеры и создаем необходимые файлы в хранилище приложения
                case REQUEST_TAKE_PHOTO:
                    if (data == null){
                        SnackbarHelper.show(this, tvPathImg,"Ошибка! Снимок не сохранен!");
                        break;
                    }
                    /*Bitmap photoBitmap = (Bitmap) data.getExtras().get("data");
                    String fileName;
                    fileName = createImageFromBitmap(photoBitmap);
                    //Toast.makeText(this, Integer.toString(photoBitmap.getByteCount()), Toast.LENGTH_LONG).show();
                    tmpImageView.setImageBitmap(photoBitmap);
                    tvPathImg.setText(appPathFiles + "/" + fileName);
*/
                    PathUtils aPathUtils = new PathUtils();
                    String FilePath = data.getExtras().getString("imgFile");//aUri.getPath();//aPathUtils.getPath(this, aUri); //data.getData().getPath();
                    aUri = Uri.parse(FilePath);//outputFileUri;
                    String fileName = aUri.getLastPathSegment();

                    //Toast.makeText(this, FilePath, Toast.LENGTH_LONG).show();

                    String tmpStr = aUri.getAuthority();
                    SnackbarHelper.show(this, tvPathImg, tmpStr);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //options.inSampleSize = calculateInSampleSize(options, tmpImageView.getWidth(), tmpImageView.getHeight());
                    Bitmap bitmap = BitmapFactory.decodeFile(FilePath, options);
                    tmpImageView.setImageBitmap(bitmap);

                    tvPathImg.setText(FilePath);

                    break;
            }
        }
        isEdit = false;
    }

    /* Проверяет, доступно ли external storage как минимум для чтения */
    public boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return true;
        }
        return false;
    }

    public void showImage(){
        TextView tvPathImg = (TextView)findViewById(R.id.etPathImg);
        TextView tvNameImg = (TextView)findViewById(R.id.etNameImg);
        if (strPathImg.equals("")) {
            strPathImg = tvPathImg.getText().toString();
        }
        Uri aUri = Uri.parse(strPathImg);
        /*File showFile = new File (strPathImg);
        if (showFile.exists()) {
            aUri = Uri.parse(strPathImg);
        } else {
            String internalFileName = getFilesDir() + "/" + strShortPathImg;
            showFile = new File(internalFileName);//strShortPathImg);
            if (showFile.exists()){
                aUri = Uri.parse(internalFileName);//strShortPathImg);
            } else {
                Toast.makeText(this, "Файл не существует", Toast.LENGTH_LONG).show();
                return;
            }
        }*/
/*
        Intent intent = new Intent(this, preview_img.class);
        intent.putExtra("imageUri", aUri.toString());
        intent.putExtra("imgName", tvNameImg.getText());
        if (isImgCrypt) {
            intent.putExtra("isCrypt", "1");
        } else {
            intent.putExtra("isCrypt", "0");
        }

        isEdit = true;

        startActivity(intent);*/

        Intent intent = new Intent(this, image_view.class);
        intent.putExtra("imageUri", aUri.toString());
        intent.putExtra("imgName", tvNameImg.getText().toString());
        if (isImgCrypt) {
            intent.putExtra("isCrypt", "1");
        } else {
            intent.putExtra("isCrypt", "0");
        }

        isEdit = true;

        startActivity(intent);
        //startActivityForResult(intent, -10);
    }

    public Boolean createImageFromBitmap(Uri aUri) {
        Bitmap bitmap = BitmapFactory.decodeFile(aUri.getPath());
        String fileName = aUri.getLastPathSegment();//no .png or .jpg needed
        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".bmp";

        if (fileName.isEmpty()){
            return false;
        }
        if (fileName.equals("")){
            return false;
        }

        CheckBox cbIsCrypto = (CheckBox) findViewById(R.id.cb_IsCryptImg);

        //если файл существует, то не надо его пересоздавать, т.к. с каждым пересозданием мы ухудшаем качество файла сжатием
        File f = new File(fileName);
        if (f.exists()){
            return true;
        }

        deleteImgInternalFile(fileName);

        //создаем большой файл
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            if (intIdImg.equals("0")) { //если файл новый, то жмем до 70 процентов, иначе без сжатия
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            } else bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray()); // remember close file output
            fo.close();

            bmpBitmap = bitmap;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public Boolean createJPGFromBitmap(Uri aUri) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(aUri.getPath());
        String fileName = aUri.getLastPathSegment();//no .png or .jpg needed

        if (fileName.isEmpty()){
            return false;
        }
        if (fileName.equals("")){
            return false;
        }

        //если файл существует, то не надо его пересоздавать, т.к. с каждым пересозданием мы ухудшаем качество файла сжатием
        File f = new File(fileName);
        if (f.exists()){
            return true;
        }

        deleteImgInternalFile(fileName);

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            if (intIdImg.equals("0")) { //если файл новый, то жмем до 70 процентов, иначе без сжатия
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bytes);
            } else bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray()); // remember close file output
            fo.close();
            jpgBitmap = bitmap;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public String createImageFromBitmap(Bitmap bitmap) {
        String fileName = "pass_photo_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".bmp";

        if (fileName.isEmpty()){
            return "";
        }
        if (fileName.equals("")){
            return "";
        }

        CheckBox cbIsCrypto = (CheckBox) findViewById(R.id.cb_IsCryptImg);

        //если файл существует, то не надо его пересоздавать, т.к. с каждым пересозданием мы ухудшаем качество файла сжатием
        File f = new File(fileName);
        if (f.exists()){
            return "";
        }

        deleteImgInternalFile(fileName);

        //создаем большой файл
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            if (intIdImg.equals("0")) { //если файл новый, то жмем до 70 процентов, иначе без сжатия
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
            } else bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray()); // remember close file output
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }

        return fileName;
    }

    public Boolean createSmallImageFromBitmap(Uri aUri) {
        float scaleWidth = 1;
        float scaleHeight = 1;
        Bitmap bitmap = BitmapFactory.decodeFile(aUri.getPath());
        String fileName = aUri.getLastPathSegment();//no .png or .jpg needed
        fileName = "s_" + fileName.substring(0, fileName.lastIndexOf(".")) + ".jpg";//".bmp";

        if (fileName.isEmpty()){
            return false;
        }
        if (fileName.equals("")){
            return false;
        }

        deleteSmallImgInternalFile(fileName);

        double scale = 1;
        if (bitmap.getWidth() != 0) {
            scale = (double) (((double) (100)) / bitmap.getWidth());
        }

        scaleWidth = (float) (((double) (scaleWidth)) * scale);
        scaleHeight = (float) (((double) (scaleHeight)) * scale);

        int bmpWidth = bitmap.getWidth();//(int) (((double) (bitmap.getWidth())) * scale);
        int bmpHeight = bitmap.getHeight();//(int) (((double) (bitmap.getHeight())) * scale);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);

        //создаем маленький файл
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            if (intIdImg.equals("0")) { //если файл новый, то жмем до 80 процентов, иначе без сжатия
                resizeBmp.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
            } else bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray()); // remember close file output
            fo.close();

            smallBitmap = resizeBmp;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }


    public void deleteImgInternalFile(String shortFileName){
        File f = new File(shortFileName);
        //if (f.exists()) { // файл есть
        try {
            this.deleteFile(shortFileName);
        } catch (Exception e) {
            Toast.makeText(this, "Большой файл из кэша почему-то не удалился. Может его нет?", Toast.LENGTH_LONG).show();
        }

        /*try {
            this.deleteFile(shortFileName.substring(0, shortFileName.lastIndexOf(".")) + ".jpg");
        } catch (Exception e) {
            Toast.makeText(this, "JPG-файл из кэша почему-то не удалился. Может его нет?", Toast.LENGTH_LONG).show();
        }*/
            ivImageEdit.setImageURI(null);
        //}

    }

    public void deleteJPGInternalFile(String shortFileName){
        File f = new File(shortFileName);
        //if (f.exists()) { // файл есть
        try {
            this.deleteFile(shortFileName.substring(0, shortFileName.lastIndexOf(".")) + ".jpg");
        } catch (Exception e) {
            Toast.makeText(this, "JPG-файл из кэша почему-то не удалился. Может его нет?", Toast.LENGTH_LONG).show();
        }
        ivImageEdit.setImageURI(null);
        //}

    }

    public void deleteSmallImgInternalFile(String smallFileName){
        File f = new File(smallFileName);
        //if (f.exists()) { // файл есть
        try {
            this.deleteFile(smallFileName);
        } catch (Exception e) {
            Toast.makeText(this, "Маленький файл из кэша почему-то не удалился. Может его нет?", Toast.LENGTH_LONG).show();
        }
        //ivImageEdit.setImageURI(null);
        //}
    }

    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public void onButtonCameraClick(View v){
        startCamera();
    }

    public void startCamera(){
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String fName = extStorageDir + "/" + Environment.DIRECTORY_DCIM + "/" + "pass_photo_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        File file = new File(fName);
        if (file.mkdirs()) {
            outputFileUri = Uri.parse(fName);//Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        }

        startActivityForResult(intent, CAMERA_RESULT);*/
        Intent cameraIntent = new Intent(this, CameraActivity.class);

        isEdit = true;

        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isEdit) this.finish();
    }

    public void exportImgEdit(View v){
        SnackbarHelper.showL(this, v,"Файл " + strNameImg + ": " + writeImageFileToSD().getAbsolutePath() + " записан на диск!");
    }

    public File writeImageFileToSD() {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d("", "SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File export_Img_File = getBackupStorageDir(this, "Backup_PassBook/export_" + timeStamp);


        //копируем все файлы с изображениями
        return exportFilesImg(export_Img_File, new File(strPathImg));
    }

    //создание каталога бэкапа для записи файла
    public File getBackupStorageDir(Context context, String dirName) {
        // Get the directory for the app's private pictures directory.
        File file = getExternalStoragePublicDirectory(dirName);
        try {
            if (!file.mkdirs()) {
                Log.e("", "Directory not created");
            }
        } catch (RuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return file;
    }

    //копируем файлы изображений из приложения во внешнюю папку
    public File exportFilesImg(File dirBackup, File sourceFileName){
        DatabaseHelper imgDB;
        File backupFile = new File(dirBackup.getAbsolutePath(), sourceFileName.getName());
        File sourceFile = new File(sourceFileName.toString());//appPathFiles + "/" + sourceFileName);
        try {
            return this.copyFile(sourceFile, backupFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public File copyFile(File sourceFile, File destFile) throws IOException {
        SecretHelper sh = new SecretHelper();
        if(!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            if (!isImgCrypt) {
                destination = new FileOutputStream(destFile).getChannel();
                source = new FileInputStream(sourceFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            }
            else {
                sh.DecodeFileToPath(this, Uri.parse(sourceFile.getAbsolutePath()), ((passApp)getApplicationContext()).getPass(), destFile);
            }
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
            return destFile;
        }
    }

}

