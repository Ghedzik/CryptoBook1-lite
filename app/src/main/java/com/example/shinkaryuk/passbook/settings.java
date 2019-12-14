package com.example.shinkaryuk.passbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.lang.String;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static java.lang.Math.round;

public class settings extends AppCompatActivity {
    private static final int PICKFILE_RESULT_CODE = 1;
    private static final int READ_REQUEST_CODE = 42;
    private static final String LOG_TAG = "Настройки";
    String[] fList;
    private String appPathFiles;
    String textLenPass = "";
    SeekBar sbLenghtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        //toolbar.setTitle(getResources().getString(R.string.title_activity_addeditimg));
        setSupportActionBar(toolbar);

        appPathFiles = getApplication().getFilesDir().getAbsolutePath();

        RegUtils reg = new RegUtils(this);

        TextView tvLenghtPass = (TextView) findViewById(R.id.tvLenghtPass);
        sbLenghtPass = (SeekBar) findViewById(R.id.sbLenghtPass);
        CheckBox cbEditOnlyWin = (CheckBox) findViewById(R.id.cbEditOnlyWindow);
        textLenPass = tvLenghtPass.getText().toString();

        sbLenghtPass.setOnSeekBarChangeListener(seekBarChangeListener);

        tvLenghtPass.setText(Integer.toString(reg.getLenghtPass()));
        sbLenghtPass.setProgress(reg.getLenghtPass());


        cbEditOnlyWin.setChecked(reg.getHowEdit() == RegUtils.EDIT_IN_WINDOW);

        cbEditOnlyWin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                RegUtils reg = new RegUtils(getApplicationContext());
                if (isChecked) {
                    reg.setHowEdit(RegUtils.EDIT_IN_WINDOW);
                } else {
                    reg.setHowEdit(RegUtils.EDIT_IN_LIST);
                }
            }
        });

        ShowFilesIntenalStorage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuBack:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void ShowFilesIntenalStorage(){
        fList = fileList();
        ArrayList<HashMap<String, String>> fileArrayList = new ArrayList<>();

        HashMap<String, String> hashMap;
        String fStr = "";
        String fSize;
        double fStorageSize = 0;
        for (int i = 0; i < fList.length; i++) {
            File tmpFile = new File(appPathFiles + "/" + fList[i]);

            if (tmpFile.length() < (1024 * 1024)){
                fSize = Double.toString(tmpFile.length() / 1024) + " kB";
            } else {
                fSize = Double.toString(round(tmpFile.length() / (1024 * 1024))) + " MB";
            }
//            fStr = fStr + fList[i] + " ---- " + fSize;
            fStorageSize = fStorageSize + tmpFile.length();

            hashMap = new HashMap<>();
            hashMap.put("FILENAME", fList[i]);
            hashMap.put("FILESIZE", fSize);
            fileArrayList.add(hashMap);
        }
        if (fStorageSize < (1024 * 1024)){
            fStr = getResources().getString(R.string.message_total_app_files_size) + " -- " + Double.toString(round(fStorageSize / 1024)) + " kB";
        } else {
            fStr = getResources().getString(R.string.message_total_app_files_size) + " -- " + Double.toString(round(fStorageSize / (1024 * 1024))) + " MB";
        }

        SimpleAdapter adapter = new SimpleAdapter(this, fileArrayList,
                R.layout.file_list_item, new String[]{"FILENAME", "FILESIZE"},
                new int[]{R.id.textview_file_name, R.id.textview_file_size});

        ListView lvFList = findViewById(R.id.lvFileList);
        //lvFList.addFooterView((View) findViewById(R.id.textView4));

        //Добавляем заголовок Header к ListView
        View v = getLayoutInflater().inflate(R.layout.header_file_list, null);
        v.setBackgroundColor(Color.GRAY);
        if (lvFList.getHeaderViewsCount() == 0) lvFList.addHeaderView(v);

        //добавляем подвал Footer к ListView
        if (lvFList.getFooterViewsCount() == 0) {
            View vFooter = getLayoutInflater().inflate(R.layout.footer_file_list, null);
            TextView tmpTv = (TextView) vFooter.findViewById(R.id.tvFooter);
            tmpTv.setBackgroundColor(Color.GRAY);
            tmpTv.setText(fStr);
            lvFList.addFooterView(vFooter);
        } else {
            View vFooter = getLayoutInflater().inflate(R.layout.footer_file_list, null);
            TextView tmpTv = (TextView) vFooter.findViewById(R.id.tvFooter);
            tmpTv.setText(fStr);
        }

        lvFList.setAdapter(adapter);
    }

    public void onClick (View v){
        DatabaseHelper passDB = new DatabaseHelper(this.getApplicationContext(), v);
        writeBackupFileSD(passDB.backup_DB_pass());
    }

    public void onClickRestoreBackup(View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isExternalStorageReadable()) {
            Uri aUri = null;

            switch (requestCode) {
                case PICKFILE_RESULT_CODE:
                    if (resultCode == RESULT_OK) {
                        String FilePath = data.getData().getPath();
                        //FilePath = data.getData().
                        SnackbarHelper.show(this, sbLenghtPass, FilePath);
                    }
                    break;
                case READ_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        PathUtils aPathUtils = new PathUtils();

                        aUri = data.getData();
                        String FilePath = aPathUtils.getPath(this, aUri); //data.getData().getPath();

                        restoreBackup(aUri);
                        SnackbarHelper.show(this, sbLenghtPass, getResources().getString(R.string.message_restore_backup_successfully1) + FilePath
                                + getResources().getString(R.string.message_restore_backup_successfully2));

                    }
                    break;
                case changeMainPasswd.RESULT_PASSWD_OK:
                    SnackbarHelper.show(this, sbLenghtPass, getResources().getString(R.string.message_password_changed_successfully));
                    break;
            }
        }
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

    //создание каталога для записи бэкапа корне внешнего хранилища
    public File getBackupStorageDir(Context context, String dirName) {
        // Get the directory for the app's private pictures directory.
        File file = getExternalStoragePublicDirectory(dirName);
        try {
            if (!file.mkdirs()) {
                Log.e(LOG_TAG, "Directory not created");
            }
        } catch (RuntimeException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return file;
    }

    void writeBackupFileSD(String aStrTable) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File backup_File = getBackupStorageDir(this, "Backup_PassBook/backup_" + timeStamp);

        // формируем объект File, который содержит путь к файлу
        String imageFileName = "BACKUP_PASS_" + timeStamp + ".crb";
        File sdFile = new File(backup_File.getAbsolutePath(), imageFileName);

        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(aStrTable);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //копируем все файлы с изображениями
        backupFilesImg(backup_File);
        SnackbarHelper.show(this, sbLenghtPass,getResources().getString(R.string.message_file_create_successfully1) + sdFile.getAbsolutePath()
                + getResources().getString(R.string.message_file_create_successfully2));
    }

    public void restoreBackup(Uri aUri){
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // формируем объект File, который содержит путь к файлу
        PathUtils aPathUtils = new PathUtils();
        String fullPath = aPathUtils.getPath(this, aUri);
        File sdFile = new File(fullPath);
        int countCols;

        DatabaseHelper passDB = new DatabaseHelper(this.getApplicationContext(), sbLenghtPass);


        try {
            // открываем поток для чтения
            FileReader aFR = new FileReader(sdFile);

            BufferedReader br = new BufferedReader(aFR);

            String aStr = "";
            String restoreStr = "";
            String passNameFld, passLoginFld, passPassFld, passCommentFld, passFavorites, passDateCreate, passDateChange, isCrypt;
            String aFields[];

            String notesName, notesDateCreate, notesDateChange, notesCrypto;

            String imgName, imgFileName, imgShortFileName, imgSmallFileName, imgComment, imgDateCreate, imgDateChange, imgSmallFile, imgLargeFile, imgCrypto;

            String blockTag = "";
            // читаем содержимое
            while ((aStr = br.readLine()) != null) {
                if (aStr.equals("<pass>") || aStr.equals("<notes>") || aStr.equals("<images>")) {
                    blockTag = aStr;
                }

                switch (blockTag) {
                    case "<pass>":
                        passDB.deletePassAll();

                        while (((aStr = br.readLine()) != null) && (!aStr.equals("<endpass/>"))) {
                            if (!aStr.equals("")) {
                                countCols = getCountCols(aStr,"\t");

                                aFields = aStr.split("\t");
                                passNameFld = aFields[1].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                passLoginFld = aFields[2].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                passPassFld = aFields[3].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                passCommentFld = aFields[4].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                passFavorites = aFields[5];
                                if(countCols > 5) {
                                    passDateCreate = aFields[6];
                                } else {passDateCreate = getCurrentDate();}
                                if(countCols > 6) {
                                    passDateChange = aFields[7];
                                } else {passDateChange = getCurrentDate();}
                                if(countCols > 7) {
                                    isCrypt = aFields[8];
                                } else {isCrypt = "0";}

                                passDB.insertPassForRestore(0, passNameFld, passLoginFld, passPassFld, passCommentFld, passFavorites, passDateCreate, passDateChange, isCrypt);
                            }
                        }
                        break;

                    case "<notes>":
                        passDB.deleteNotesAll();
                        while (((aStr = br.readLine()) != null) && (!aStr.equals("<endnotes/>"))) {
                            if (!aStr.equals("")) {
                                countCols = getCountCols(aStr,"\t");

                                aFields = aStr.split("\t");
                                notesName = aFields[1].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                if(countCols > 1) {
                                    notesDateCreate = aFields[2];
                                } else {notesDateCreate = getCurrentDate();}
                                if(countCols > 2) {
                                    notesDateChange = aFields[3];
                                } else {notesDateChange = getCurrentDate();}
                                if(countCols > 3) {
                                    notesCrypto = aFields[4];
                                } else {notesCrypto = "0";}

                                passDB.insertNotesForRestore(0, notesName, notesDateCreate, notesDateChange, notesCrypto);
                            }
                        }
                        break;

                    case "<images>":
                        passDB.deleteImgAll();
                        onClickClearFiles(null);

                        restoreFilesImg(new File(sdFile.getParent()));

                        while (((aStr = br.readLine()) != null) && (!aStr.equals("<endimages/>"))) {
                            if (!aStr.equals("")) {
                                countCols = getCountCols(aStr,"\t");

                                aFields = aStr.split("\t");
                                imgName = aFields[1].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                imgFileName = aFields[2].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                imgShortFileName = aFields[3].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                imgSmallFileName = aFields[4].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                imgComment = aFields[5].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                imgDateCreate = aFields[6];
                                imgDateChange = aFields[7];
                                if (countCols > 7) {
                                    imgSmallFile = aFields[8].replace(passDB.strEndRow, "\n").replace(passDB.strTab, "\t");
                                    imgCrypto = aFields[9];
                                }
                                else {
                                    imgSmallFile = "";
                                    imgCrypto = "0";//aFields[8];
                                }


//Нужно переписать insert, тк из файла бэкапа будут грузиться данные для маленького файла а не пути.
                                passDB.insertPassForRestore(0, imgName, imgFileName, imgComment, imgShortFileName, imgSmallFileName, imgDateCreate, imgDateChange, imgSmallFile, imgCrypto);//замениить потом реальными датами из бэкапа
                            }
                        }
                        //restoreFilesImg(new File(sdFile.getParent()));
                        break;
                }
            }
//            Toast.makeText(this, restoreStr, Toast.LENGTH_LONG).show();
            aFR.close();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Toast.makeText(this, "что-то сломалось" + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        //deleteAllFiles();
        restoreFilesImg(new File(sdFile.getParent()));
    }

    public void onClickChangePasswd(View v){
        Intent intent = new Intent(this, changeMainPasswd.class);
        startActivityForResult(intent, changeMainPasswd.RESULT_PASSWD_OK);

    }

    public void onClickClearFiles(View v){
        DatabaseHelper imgDB;
        imgDB = new DatabaseHelper(this, v);
        Cursor whereCursor;
        String whereStr = "";
        String[] fldList = new String[3];
        fldList[0] = imgDB.colImgSmallFileName;
        fldList[1] = imgDB.colImgFileName;
        fldList[2] = imgDB.colImgShortFileName;
        for (int i  = 0; i <= fList.length - 1; i++){
            whereStr = fList[i];
            whereCursor = imgDB.getAllImgWhere(fldList, whereStr);
            if (whereCursor.getCount() == 0){
                this.deleteFile(whereStr);
            }
        }
        ShowFilesIntenalStorage();
    }

//удаляем все файлы внутреннего хранилища перед восстановлением из бэкапа
    public void deleteAllFiles(){
        String fStr = "";
        for (int i  = 0; i <= fList.length - 1; i++){
            fStr = fList[i];
            if ((new File(appPathFiles + "/" + fStr)).isFile()){
                this.deleteFile(appPathFiles + "/" + fStr);
            }
        }
        ShowFilesIntenalStorage();
    }


    //копируем файлы изображений из приложения во внешнюю папку
    public void backupFilesImg(File dirBackup){
        DatabaseHelper imgDB;
        imgDB = new DatabaseHelper(this, sbLenghtPass);
        Cursor whereCursor;
        String whereStr = "";
        for (int i  = 0; i <= fList.length - 1; i++){
            whereStr = fList[i];
            whereCursor = imgDB.getAllImgWhere(imgDB.colImgSmallFileName, whereStr);
            if (!(whereCursor.getCount() == 0)){
                File backupFile = new File(dirBackup.getAbsolutePath(), whereStr);
                File sourceFile = new File(appPathFiles + "/" + whereStr);
                try {
                    this.copyFile(sourceFile, backupFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ShowFilesIntenalStorage();
    }

    //копируем файлы изображений из внешней папки в приложение
    public void restoreFilesImg(File dirBackup){
        String fileName = "";
        String[] exFilesList = dirBackup.list();//getExternalFilesDirs(dirBackup.getPath() + "/");
        for (int i  = 0; i <= exFilesList.length - 1; i++){
            fileName = exFilesList[i];
            File backupFile = new File(dirBackup.getAbsolutePath() + "/" + exFilesList[i]);//new File(dirBackup.getAbsolutePath(), whereStr);
            File sourceFile = new File(appPathFiles + "/" + fileName);
            int intTxt = backupFile.getName().indexOf(".txt");
            try {
                if (backupFile.isFile() && intTxt == -1) {
                    this.copyFile(backupFile, sourceFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ShowFilesIntenalStorage();
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            destination = new FileOutputStream(destFile).getChannel();
            source = new FileInputStream(sourceFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }

    public void onClickUpgradeDB(View v) {
        DatabaseHelper wholeDB;
        wholeDB = new DatabaseHelper(this, v);
        wholeDB.altertable(wholeDB.getWritableDatabase());
        //wholeDB.updateWholeDB(wholeDB.getWritableDatabase());

        //DatabaseHelper imgDB = new DatabaseHelper(this.getApplicationContext());
        //Cursor tmpCur = imgDB.getWritableDatabase().rawQuery("pragma table_info(images)", new String[]{});
        //if (tmpCur.getCount() < 11){
        //    imgDB.altertable(imgDB.getWritableDatabase());
        //}
        //imgDB.upgradeImages();
        //imgDB.getWritableDatabase().execSQL("UPDATE images SET imgSmallFile = '' WHERE imgSmallFile <> ''");
        //imgDB.getWritableDatabase().execSQL("UPDATE images SET imgLargeFile = '' WHERE imgLargeFile <> ''");
        //imgDB.altertable(imgDB.getWritableDatabase());

    }

    //@Override
    //protected void onPause() {
    //    super.onPause();
    //    this.finish();
    //}

    private int getCountCols(String source, String separ){
        String aStr;
        aStr = source;
        int count = 0;
        while (aStr.indexOf(separ) > 0){
            count++;
            aStr = aStr.substring(aStr.indexOf(separ) + 1);
        }
        return count;//полей на 1 больше чем разделителей
    }

    private String getCurrentDate() {
//Вычисляем текущую дату и форматируем ее
        Date currentDate = new Date();
// Форматирование даты как "день.месяц.год"
        DateFormat dateFormat = new java.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(currentDate);
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            TextView tvLenghtPass = (TextView) findViewById(R.id.tvLenghtPass);
            tvLenghtPass.setText(textLenPass.concat(Integer.toString(progress)));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            RegUtils reg = new RegUtils(getApplicationContext());
            reg.setLenghtPass(seekBar.getProgress());
        }
    };

}


