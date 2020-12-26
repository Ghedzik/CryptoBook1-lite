package com.shinkaryuk.passbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class ExportImportFileHelper {
    @SuppressLint("StaticFieldLeak")
    static Context context;
    static String fileNameImg;
    static Boolean isImgCrypt;

    public ExportImportFileHelper(String fileNameImage, Context c, Boolean isCryptImage){
        fileNameImg = fileNameImage;
        context = c;
        isImgCrypt = isCryptImage;
    }

    public static File writeImageFileToSD(String fileNameImage, Context c, Boolean isCryptImage) {
        // проверяем доступность SD
        ExportImportFileHelper exportImportFileHelper = new ExportImportFileHelper(fileNameImage, c, isCryptImage);
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d("", "SD-карта не доступна: " + Environment.getExternalStorageState());
            return null;
        }
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File export_Img_File = exportImportFileHelper.getBackupStorageDir("Backup_PassBook/export_" + timeStamp);


        //копируем все файлы с изображениями
        return exportFilesImg(export_Img_File, new File(fileNameImg));
    }

    //создание каталога бэкапа для записи файла
    public static File getBackupStorageDir(String dirName) {
        // Get the directory for the app's private pictures directory.
        File file = getExternalStoragePublicDirectory(dirName);
        try {
            if (!file.mkdirs()) {
                Log.e("", "Directory not created");
            }
        } catch (RuntimeException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return file;
    }

    //копируем файлы изображений из приложения во внешнюю папку
    public static File exportFilesImg(File dirBackup, File sourceFileName){
        DatabaseHelper imgDB;
        File backupFile = new File(dirBackup.getAbsolutePath(), sourceFileName.getName());
        File sourceFile = new File(sourceFileName.toString());//appPathFiles + "/" + sourceFileName);
        try {
            return copyFile(sourceFile, backupFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File copyFile(File sourceFile, File destFile) throws IOException {
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
                sh.DecodeFileToPath(context, Uri.parse(sourceFile.getAbsolutePath()), ((passApp)context.getApplicationContext()).getPass(), destFile);
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
