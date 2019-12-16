package com.shinkaryuk.passbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by shinkaryuk on 14.01.2018.
 * Данный класс предназначен для шифрования данных
 */

public class SecretHelper {
    Cipher mCipher;

    public SecretHelper() {
        try {
            mCipher = Cipher.getInstance("AES");

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String EncodeStr(String encodeString, String pswd) {
        byte[] encodedBytes = null;
        String encodeStr = encodeString;//getPswd(encodeString);
        SecretKeySpec sks = null;
        try {
            sks = generateKey(pswd);//new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Crypto", "AES secret key spec error");
            Log.e("Crypto", "Ошибка генерации ключа для шифрования на основе пароля " + pswd);
        }

        try {
            mCipher = Cipher.getInstance("AES");

            mCipher.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = mCipher.doFinal(encodeStr.getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e("Crypto", "AES encryption string error");
        } finally {
            if (encodedBytes == null) return "";
        }
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }

    public String DecodeStr(String decodeString, String pswd) {
        byte[] decodedBytes = null;
        Key sks = null;
        //Cipher mCipher;

        //sks = new SecretKeySpec(pswd.getBytes(), "AES");

        // Set up secret key spec for 128-bit AES encryption and decryption
        try {
            //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            //sr.setSeed(pswd.getBytes());
            //KeyGenerator kg = KeyGenerator.getInstance("AES");
            //kg.init(128, sr);
            sks = generateKey(pswd); //new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Crypto", "AES secret key spec error");
            Log.e("Crypto", "Ошибка генерации ключа для дешифрования на основе пароля " + pswd);
        }
        try {
            mCipher = Cipher.getInstance("AES");
            mCipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decryptedValue64 = Base64.decode(decodeString, Base64.DEFAULT);
            decodedBytes = mCipher.doFinal(decryptedValue64);
        } catch (Exception e) {
            Log.e("Crypto", "AES decryption string error");
        } finally {
            if (decodedBytes == null) return "";
        }

        String dStr = new String(decodedBytes);
        return dStr;

    }

    public String DecodeByte(byte[] value, String pswd) {
        byte[] decodedBytes = value;
        Key sks = null;
        //Cipher mCipher;

        //sks = new SecretKeySpec(pswd.getBytes(), "AES");

        // Set up secret key spec for 128-bit AES encryption and decryption
        try {
            //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            //sr.setSeed(pswd.getBytes());
            //KeyGenerator kg = KeyGenerator.getInstance("AES");
            //kg.init(128, sr);
            sks = generateKey(pswd); //new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Crypto", "AES secret key spec error");
        }
        try {
            mCipher = Cipher.getInstance("AES");
            mCipher.init(Cipher.DECRYPT_MODE, sks);
            //byte[] decryptedValue64 = Base64.decode(decodeString, Base64.DEFAULT);
            decodedBytes = mCipher.doFinal(decodedBytes);
        } catch (Exception e) {
            Log.e("Crypto", "AES decryption byte error");
        } finally {
            if (decodedBytes == null) return "";
        }

        String dStr = new String(decodedBytes);
        return dStr;

    }

    private SecretKeySpec generateKey(String pswd) {
        //SecretKeySpec key = new SecretKeySpec(pswd.getBtes(), "AES");
        String aPswd = pswd;
        aPswd = getPswd(pswd);
        return new SecretKeySpec(aPswd.getBytes(), "AES");
    }

    public String getPswd(String pswd) {
        //SecretKeySpec key = new SecretKeySpec(pswd.getBtes(), "AES");
        String aPswd = pswd;
        for (int i = pswd.length(); i < 16; i++) {
            aPswd = aPswd + "X";//Integer.toString(i);
        }
        return aPswd;
    }

    public String hashPass(String pswd) {
        //шифрование/дешифрование для паролей сделано специально отдельной функцией, т.к. была проблема с длиной шифруемой строки - она должна была быть не короче 16 символов.
        //Надо будет проверить, может это уже исправили

        byte[] encodedBytes = null;
        String encodeStr = getPswd(pswd); //добиваем кодируемую строку до 16 символов
        SecretKeySpec sks = null;
        boolean isErrorKey = false;
        try {
            sks = generateKey(pswd);//new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Crypto", "AES secret key spec error");
            Log.e("Crypto", "Ошибка генерации ключа для хэша на основе пароля " + pswd);
            isErrorKey = true;
        }

        if (!isErrorKey) {
            try {
                mCipher = Cipher.getInstance("AES");

                mCipher.init(Cipher.ENCRYPT_MODE, sks);
                encodedBytes = mCipher.doFinal(encodeStr.getBytes("UTF-8"));
            } catch (Exception e) {
                Log.e("Crypto", "AES encryption hash error");
            } finally {
                if (encodedBytes == null) return "";
            }
            return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
        } else
            return "";

    }

    public String unHashPass(String decodeString, String pswd) {
        //шифрование/дешифрование для паролей сделано специально отдельной функцией, т.к. была проблема с длиной шифруемой строки - она должна была быть не короче 16 символов.
        //Надо будет проверить, может это уже исправили

        byte[] decodedBytes = null;
        Key sks = null;
        boolean isErrorKey = false;
        //Cipher mCipher;

        //sks = new SecretKeySpec(pswd.getBytes(), "AES");

        // Set up secret key spec for 128-bit AES encryption and decryption
        try {
            //SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            //sr.setSeed(pswd.getBytes());
            //KeyGenerator kg = KeyGenerator.getInstance("AES");
            //kg.init(128, sr);
            sks = generateKey(pswd); //new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Log.e("Crypto", "AES secret key spec error");
            Log.e("Crypto", "Ошибка генерации ключа для извлечения пароля из хэша на основе пароля " + pswd);
            isErrorKey = true;
        }
        if (!isErrorKey) {
            try {
                mCipher = Cipher.getInstance("AES");
                mCipher.init(Cipher.DECRYPT_MODE, sks);
                byte[] decryptedValue64 = Base64.decode(decodeString, Base64.DEFAULT);
                decodedBytes = mCipher.doFinal(decryptedValue64);
            } catch (Exception e) {
                Log.e("Crypto", "AES decryption hash error");
            } finally {
                if (decodedBytes == null) return "";
            }

            String dStr = new String(decodedBytes);
            return dStr;
        } else
            return "";
    }

    public boolean DecodeFile(Context context, Uri aUri, String pswd){
        Bitmap bitmap;
        DbBitmapUtility dbu = new DbBitmapUtility();
        String fileName = aUri.getLastPathSegment();//no .png or .jpg needed
        PathUtils aPathUtils = new PathUtils();
        String fullPath = aPathUtils.getPath(context, aUri);

        // формируем объект File, который содержит путь к файлу
        try {
            String fileData = "";
            FileInputStream fIn = context.getApplicationContext().openFileInput(fileName);
            //InputStreamReader isr = new InputStreamReader(fIn);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fIn.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            fileData = result.toString("UTF-8");

            bitmap = dbu.convertBase64ToBitmap(DecodeStr(fileData, pswd));


            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            FileOutputStream fo = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray()); // remember close file output
            fo.close();
            //jpgBitmap = bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Toast.makeText(this, "что-то сломалось" + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            return true;
        }
    }

    public boolean EncodeFile(Context context, Uri aUri, String pswd){
        DbBitmapUtility dbu = new DbBitmapUtility();
        String fileName = aUri.getLastPathSegment();//no .png or .jpg needed
        PathUtils aPathUtils = new PathUtils();
        String fullPath = aPathUtils.getPath(context, aUri);
        Bitmap bitmap = BitmapFactory.decodeFile(aUri.getPath());
        if (fullPath == null){
            fullPath = aUri.getPath();
        }

            // формируем объект File, который содержит путь к файлу
            try {
                String fileData = EncodeStr(dbu.convertBitmapToBase64(bitmap), pswd);

                BufferedWriter fo = new BufferedWriter(new FileWriter(fullPath));  //openFileOutput(fileName, Context.MODE_PRIVATE);
                fo.write(fileData); // remember close file output
                fo.close();
                //jpgBitmap = bitmap;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
//            Toast.makeText(this, "что-то сломалось" + e.getMessage(), Toast.LENGTH_LONG).show();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                return true;
            }

    }

    public Bitmap getDecodeBitmap(Context context, Uri aUri, String pswd){
        Bitmap bitmap = null;
        DbBitmapUtility dbu = new DbBitmapUtility();
        String fileName = aUri.getLastPathSegment();//no .png or .jpg needed
        // формируем объект File, который содержит путь к файлу
        try {
            String fileData = "";
            FileInputStream fIn = context.getApplicationContext().openFileInput(fileName);
            //InputStreamReader isr = new InputStreamReader(fIn);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fIn.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            fileData = result.toString("UTF-8");

            bitmap = dbu.convertBase64ToBitmap(DecodeStr(fileData, pswd));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Toast.makeText(this, "что-то сломалось" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        if (bitmap != null){
            return bitmap;
        } else return null;
    }

    public String getDecodeBitmapToStr(Context context, Uri aUri, String pswd){
        String bitmap = null;
        Bitmap bitmapTmp;
        DbBitmapUtility dbu = new DbBitmapUtility();
        String fileName = aUri.getLastPathSegment();//no .png or .jpg needed

        // формируем объект File, который содержит путь к файлу
        try {
            String fileData = "";
            FileInputStream fIn = context.getApplicationContext().openFileInput(fileName);
            //InputStreamReader isr = new InputStreamReader(fIn);
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fIn.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            fileData = result.toString("UTF-8");

            bitmapTmp = dbu.convertBase64ToBitmap(DecodeStr(fileData, pswd));
            //bitmap = DecodeStr(fileData, pswd);
            bitmap = dbu.convertBitmapToBase64(bitmapTmp);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Toast.makeText(this, "что-то сломалось" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        if (bitmap != null){
            return bitmap;
        } else return null;
    }

    public void deleteJPGInternalFile(Context context, String shortFileName){
        File f = new File(shortFileName);
        if (f.exists()) { // файл есть
            try {
                context.deleteFile(shortFileName.substring(0, shortFileName.lastIndexOf(".")) + ".jpg");
            } catch (Exception e) {
                Toast.makeText(context, context.getResources().getString(R.string.message_delete_error), Toast.LENGTH_LONG).show();
            }
        }

    }

    public String randomPassword(Context context){
        //генерация случайного пароля
        int countS;
//Достаем длину пароля из настроек
        SharedPreferences mSettings;
        String APP_PREFERENCES = "mysettings";
        String APP_PREFERENCES_LENPSW = "lenpswd";
        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES_LENPSW)) {
            countS = Integer.parseInt(mSettings.getString(APP_PREFERENCES_LENPSW, "8"));
        }
        else {
            countS = 8;
        }
//////////////////////////////////////////////////////

        String pass = "", strSimbols = "----------aAbBc0CdDeE1fFgGh2HiIjJ3kKlLm4MnNoO5pPqQr6RsStT7uUvVw8WxXyY9zZ";
//      коды символов: цифры - с 48 по 57, заглавные латинские с 65 по 90, обычные латинские с 97 по 122
        Integer mCount = countS, rndCode;
        Boolean isNum = false, isSmallS = false, isLargeS = false;
        Double rnd;
            while (pass.length() <= mCount) {
                rnd = Math.random();
                if (rnd >= 0.1 && rnd <= 0.72) {
                    rndCode = Integer.parseInt(Double.toString(rnd * 100).substring(0, 2));
                    String mSimbol = strSimbols.substring(rndCode, rndCode + 1);
                    if (mSimbol.getBytes()[0] >= 48 && mSimbol.getBytes()[0] <= 57) {
                        isNum = true;
                    } else if (mSimbol.getBytes()[0] >= 65 && mSimbol.getBytes()[0] <= 90) {
                        isLargeS = true;
                    } else if (mSimbol.getBytes()[0] >= 97 && mSimbol.getBytes()[0] <= 122) {
                        isSmallS = true;
                    }
                    pass = pass.concat(strSimbols.substring(rndCode, rndCode + 1));
                    if (pass.length() == mCount) {
                        if (!isLargeS || !isNum || !isSmallS) {
                            pass = pass.substring(0,7);
                        }
                    }
                }
            }
        return pass;
    }

}

