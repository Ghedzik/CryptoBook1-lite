package com.shinkaryuk.passbook;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
//import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;
//import android.app.Application;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shinkaryuk on 05.12.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String dbName = "passDB";
    public final static int DB_VERSION = 2;

    //Таблица паролей
    public static final String passTable = "pass";
    public static final String colID = "_id";
    public static final String colName = "passName";
    public static final String colLogin = "passLogin";
    public static final String colPass = "passPass";
    public static final String colComment = "passComment";
    public static final String colFavorite = "passFavorite";
    public static final String colPassDateCreate = "passDateCreate";
    public static final String colPassDateChange = "passDateChange";
    public static final String colPassIsCrypt = "isCrypt"; //указывает зашифрована ли строка
    //public static final String colPassSettings = "settings"; //поле настроек записи, битовая маска 1-й бит - запись - архивная, 2-й бит - избранная, третий бит - шифрованная...
    //Пример маски
    // 0001 (дес = 1) запись архивная,
    // 0010 (дес = 2) запись избранная,
    // 0011 (дес = 3) запись архивная и избранная
    // 0100 (дес = 4) запись зашифрованная,
    // 0101 (дес = 5) запись зашифрованная и архивная,
    // 0110 (дес = 6) запись зашифрованная и избранная
    // 0111 (дес = 7) запись зашифрованная, избранная, архивная
    //Это нужно переделать для того, чтобы пользоваться одним полем и не плодить новых полей. Надо сделать в будущем.
    //Первым будет реализован функционал архивирования

    /*
    Изображения могут содержать в себе несколько страниц - например несколько страниц паспорта или другого документа.
    Для этого надо сделать две таблицы: master-detail.
    В главной хранятся наименования документа, в дочерней - сама информация с названиями.
    Необходимо добавить новую таблицу images_main, в которой будут следующие поля:
    */
    public static final String imgMainTable = "images_main";
    public static final String colImgMainID = "_id";
    public static final String colImgMainName = "imgMainName";
    public static final String colImgMainComment = "imgMainComment";
    public static final String colImgMainDateCreate = "imgMainDateCreate";
    public static final String colImgMainDateChange = "imgMainDateChange";
    public static final String colImgMainIsCrypt = "isCrypt";
    public static final String colImages_main_id_key = "images_main_id";
    /*
    Для этого придется переделать форму отображения, форму для редактирования, возможно добавить форму редактирования
    основной записи из таблицы images_main, но лучше не добавлять отдельную форму для этого. а редактировать ее прямо
    на форме отображения, т.к. редактирование будет простым.
     */

    //Таблица изображений
    public static final String imgTable = "images";
    public static final String colImgID = "_id";
    public static final String colImgName = "imgName";
    public static final String colImgFileName = "imgFileName";
    public static final String colImgShortFileName = "imgShortFileName";
    public static final String colImgSmallFileName = "imgSmallFileName";
    public static final String colImgComment = "imgComment";
    public static final String colImgDateCreate = "imgDateCreate";
    public static final String colImgDateChange = "imgDateChange";
    //public static final String colImgSettings = "settings"; //поле настроек записи, битовая маска 1-й бит - запись - архивная, 2-й бит - избранная, третий бит - шифрованная...
    public static final String colImgSmall = "imgSmallFile";
    public static final String colImgIsCrypt = "isCrypt";
    public static final String colImages_main_id_fk = "images_main_id_fk";


    //Таблица заметок
    public static final String notesTable = "notes";
    public static final String colNotesID = "_id";
    public static final String colNotes = "notesNoteName";
    public static final String colNoteDateCreate = "notesDateCreate";
    public static final String colNoteDateChange = "notesDateChange";
    public static final String colNoteIsCrypt = "isCrypt"; //указывает зашифрована ли строк
    //public static final String colNoteSettings = "settings"; //поле настроек записи, битовая маска 1-й бит - запись - архивная, 2-й бит - избранная, третий бит - шифрованная...

    //Типы сортировок
    public static final int sortAsc = 1;
    public static final int sortDesc = -1;

    //Фильтрация избранных
    public static final int whereFav = 1;
    public static final int whereAll = 0;

    //символы замены для конца строки и для табуляции
    public final static String strEndRow = "<endRow>";
    public final static String strTab = "<smbTab>";

    //Тип шифрования или дешифрования строки
    public final static int CRYPTO_ENCODE = 0;
    public final static int CRYPTO_DECODE = 1;

    private SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_PSW = "pswd";
    public static String prefStrPswd = "";

    public static final String CHECK_RECORD_FOR_BACKUP = "Гарантия личной тайны - залог свободы";

    private Context mContext;
    View viewForSnackbar;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        SQLiteDatabase db;
        if (sqLiteDatabase.isReadOnly()){
            db = this.getWritableDatabase();
        }
        else {
            db = sqLiteDatabase;
        }
//        db.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteDateCreate + " TEXT");
//        db.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteDateChange +
        //Context context = getApplicationContext();
        //mSettings = PreferenceManager.getDefaultSharedPreferences(this);//SharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String strSQL = "CREATE TABLE IF NOT EXISTS "+passTable+" ("+
                colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colName+" TEXT, "+
                colLogin+" TEXT, "+
                colPass+" TEXT, "+
                colComment+" TEXT, "+
                colFavorite+" INTEGER, "+
                colPassDateCreate+" TEXT, "+
                colPassDateChange+" TEXT, " +
                colPassIsCrypt + " INTEGER)";
        db.execSQL(strSQL);

        strSQL = "CREATE TABLE IF NOT EXISTS " + imgTable + " ("+
                colImgID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colImgName+" TEXT, "+
                colImgFileName+" TEXT, "+
                colImgShortFileName+" TEXT, "+
                colImgSmallFileName+" TEXT, "+
                colImgComment+" TEXT, "+
                colImgDateCreate+" TEXT, "+
                colImgDateChange+" TEXT, " +
                colImgSmall + " TEXT, " +
                colImgIsCrypt + " INTEGER, " +
                colImages_main_id_fk + " INTEGER)";
        db.execSQL(strSQL);

        strSQL = "CREATE TABLE IF NOT EXISTS "+imgMainTable+" ("+
                colImgMainID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colImgMainName + " TEXT, "+
                colImgMainComment + " TEXT, "+
                colImgMainDateCreate + " TEXT, "+
                colImgMainDateChange + " TEXT, " +
                colImgMainIsCrypt + " INTEGER, " +
                colImages_main_id_key + " INTEGER)";
        db.execSQL(strSQL);

        strSQL = "CREATE TABLE IF NOT EXISTS "+notesTable+" ("+
                colNotesID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colNotes+" TEXT, "+
                colNoteDateCreate+" TEXT, "+
                colNoteDateChange+" TEXT, " +
                colNoteIsCrypt + " INTEGER)";
        db.execSQL(strSQL);

    }

    public void altertable(SQLiteDatabase sqLiteDatabase){
//Сюда будем писать изменения в структуры таблиц БД
        //SQLiteDatabase db;
        //sqLiteDatabase = this.getWritableDatabase();
        //sqLiteDatabase.execSQL("ALTER TABLE  " + passTable + " ADD " + colPassDateCreate + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + passTable + " ADD " + colPassDateChange + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + passTable + " ADD " + colPassIsCrypt + " INTEGER");
/*
        sqLiteDatabase.execSQL("ALTER TABLE " + imgTable + " RENAME TO " + imgTable + "_old;");
        sqLiteDatabase.execSQL("CREATE TABLE "+imgTable+" ("+
                colImgID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colImgName+" TEXT, "+
                colImgFileName+" TEXT, "+
                colImgShortFileName+" TEXT, "+
                colImgSmallFileName+" TEXT, "+
                colImgComment+" TEXT, "+
                colImgDateCreate+" TEXT, "+
                colImgDateChange+" TEXT, " +
                colImgSmall + " TEXT, " +
                colImgIsCrypt + " INTEGER)");
        sqLiteDatabase.execSQL("INSERT INTO images (" + colImgName + ", " + colImgFileName +", " + colImgShortFileName + ", " + colImgSmallFileName + ", " + colImgComment + ", " + colImgDateCreate + ", " + colImgDateChange + ", " + colImgSmall + ", " + colImgIsCrypt + ")" +
                "SELECT " + colImgName + ", " + colImgFileName + ", " + colImgShortFileName + ", " + colImgSmallFileName + ", " + colImgComment + ", " + colImgDateCreate + ", " + colImgDateChange + ", " + colImgSmall + ", " + colImgIsCrypt + " FROM images_old");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS images_old");
        sqLiteDatabase.execSQL("UPDATE images SET isCrypt = 0");

*/
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgDateCreate + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgSmall + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgLarge + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgDateChange + " TEXT");
        //db.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgDateChange + " TEXT"); Эту таблицу пока не шифруем

        //sqLiteDatabase.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteDateCreate + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteDateChange + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteIsCrypt + " INTEGER");

        //добавляем внешний ключ в images, если такого поля в данной таблице нет
/*        String sqlStr = "SELECT * FROM sqlite_master";// WHERE name LIKE '%" + colImages_main_id_fk + "%'";
        Cursor cur = sqLiteDatabase.rawQuery(sqlStr, new String[]{});
        cur.moveToFirst();
        while (!cur.isAfterLast()){
            cur.moveToNext();
        }

 */
    }

    public void updateWholeDB(SQLiteDatabase sqLiteDatabase){
        //сюда будем писать обнолвения данных после обновления структуры базы
        //SQLiteDatabase db;
        //db = this.getWritableDatabase();

        //Вычисляем текущую дату и форматируем ее
//        Date currentDate = new Date();
// Форматирование даты как "день.месяц.год"
/*        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        sqLiteDatabase.execSQL("UPDATE " + passTable + " SET " + colPassDateCreate +"='" + dateText + "' WHERE " + colPassDateCreate + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + passTable + " SET " + colPassDateChange +"='" + dateText + "' WHERE " + colPassDateChange + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + passTable + " SET " + colPassIsCrypt +"=1");

        sqLiteDatabase.execSQL("UPDATE " + imgTable + " SET " + colImgDateCreate +"='" + dateText + "' WHERE " + colImgDateCreate + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + imgTable + " SET " + colImgDateChange +"='" + dateText + "' WHERE " + colImgDateChange + " IS NULL");

        sqLiteDatabase.execSQL("UPDATE " + notesTable + " SET " + colNoteDateCreate +"='" + dateText + "' WHERE " + colNoteDateCreate + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + notesTable + " SET " + colNoteDateChange +"='" + dateText + "' WHERE " + colNoteDateChange + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + notesTable + " SET " + colNoteIsCrypt +"=1");

 */
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgMainTable + " ADD " + colImages_main_id + " INTEGER");
/*        sqLiteDatabase.execSQL("DROP TABLE " + imgMainTable);
        sqLiteDatabase.execSQL("DROP TABLE " + imgTable);

        sqLiteDatabase.execSQL("CREATE TABLE " + imgTable + " ("+
                colImgID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colImgName+" TEXT, "+
                colImgFileName+" TEXT, "+
                colImgShortFileName+" TEXT, "+
                colImgSmallFileName+" TEXT, "+
                colImgComment+" TEXT, "+
                colImgDateCreate+" TEXT, "+
                colImgDateChange+" TEXT, " +
                colImgSmall + " TEXT, " +
                colImgIsCrypt + " INTEGER, " +
                colImages_main_id_fk + " INTEGER)");

        sqLiteDatabase.execSQL("CREATE TABLE "+imgMainTable+" ("+
                colImgMainID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colImgMainName + " TEXT, "+
                colImgMainComment + " TEXT, "+
                colImgMainDateCreate + " TEXT, "+
                colImgMainDateChange + " TEXT, " +
                colImgMainIsCrypt + " INTEGER, " +
                colImages_main_id_key + " INTEGER)");

        String sqlStr = "DELETE FROM " + imgMainTable;
        sqLiteDatabase.execSQL(sqlStr);*/
/*
        String sqlStr = "INSERT INTO " + imgMainTable + " ("
                + colImgMainName + ", "
                + colImgMainComment + ", "
                + colImgMainDateCreate + ", "
                + colImgMainDateChange + ", "
                + colImgMainIsCrypt + ")"
                + " SELECT "
                + colImgName + ", "
                + colImgComment + ", "
                + colImgDateCreate + ", "
                + colImgDateChange + ", "
                + colImgIsCrypt + " FROM " + imgTable;
        sqLiteDatabase.execSQL(sqlStr);

        sqlStr = "UPDATE " + imgTable + " SET " + colImages_main_id_fk + "=(SELECT " + colImgMainID + " FROM " + imgMainTable + " WHERE " + colImgName + "=" + colImgMainName + ")";
        sqLiteDatabase.execSQL(sqlStr);

        sqlStr = "UPDATE " + imgMainTable + " SET " + colImages_main_id_key + "=" + colImgMainID;
        sqLiteDatabase.execSQL(sqlStr);

 */
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + passTable);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + imgTable);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + notesTable);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + imgMainTable);
        altertable(sqLiteDatabase);
        updateWholeDB(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    //конструктор
    public DatabaseHelper(Context context, View v) {

        super(context, dbName, null, DB_VERSION);
        mContext = context;
        viewForSnackbar = v;

    }

    //получаем все отсортированные данные из таблицы паролей
    public Cursor getAllPass()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        cur = db.rawQuery("SELECT * " + "from " + passTable + " ORDER BY " + colPassDateChange + " DESC", new String[]{});
        /*if (sortBy == sortAsc) {
            cur = db.rawQuery("SELECT "
                    + colID + ", "
                    + colName + ", "
                    + colLogin + ", "
                    + colPass + ", "
                    + colComment + ", "
                    + colFavorite + ", "
                    + colPassDateCreate + ", "
                    + colPassDateChange + ", "
                    + colPassIsCrypt
                    + " FROM " + passTable + " ORDER BY UPPER(" + colName + ")", new String[]{});
        } else {
            cur = db.rawQuery("SELECT * " + "from " + passTable + " ORDER BY UPPER(" + colName + ")" + " DESC", new String[]{});
        }*/
        cur.moveToFirst();
        return cur;
    }

    //еще один способ получения всех данных из таблицы
    public Cursor getPassAllData() {
        SQLiteDatabase db=this.getReadableDatabase();
        return db.query(passTable, null, null, null, null, null, null);
    }

    public void insertEditPass(int id, String name, String login, String pass, String comment, String fav, String dateCreate, String dateChange, String isCrypto){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        String loginStr, nameStr, passStr, commentStr;

        //проверяем, если надо шифровать, шифруем, в противном случае оставляем тектс открытым
        if (isCrypto.equals("1")){
            loginStr = EncodeDecodeStr(login, CRYPTO_ENCODE);
            nameStr = EncodeDecodeStr(name, CRYPTO_ENCODE);
            passStr = EncodeDecodeStr(pass, CRYPTO_ENCODE);
            commentStr = EncodeDecodeStr(comment, CRYPTO_ENCODE);
        } else {
            nameStr = name;
            loginStr = login;
            passStr = pass;
            commentStr = comment;
        }

        if (id == 0) {
            strSQL = "INSERT INTO " + passTable + " (passName, passLogin, passPass, passComment, passFavorite, passDateCreate, passDateChange, isCrypt) " +
                    "VALUES ('" + nameStr + "', '"
                    + loginStr + "', '"
                    + passStr + "', '"
                    + commentStr +"', " + fav +", '" + dateCreate +"', '" + dateChange + "', " + isCrypto +")";
        }
        else {
            if (id > 0) {
                strSQL = "UPDATE " + passTable + " SET passName = '" + nameStr
                        + "', passLogin = '" + loginStr
                        + "', passPass = '" + passStr
                        + "', passComment = '" + commentStr
                        + "', passDateCreate = '" + dateCreate
                        + "', passDateChange = '" + dateChange
                        + "', isCrypt = " + isCrypto
                        + " WHERE _id = " + id;
            }
            else {
                if (id < 0){
                    strSQL = "DELETE FROM " + passTable + " WHERE _id = " + id + " * (-1)";
                }
            }
        }
        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        };
    }

    public void addRec() {
        ContentValues cv = new ContentValues();
        cv.put(colName, "Почта mail.ru");
        cv.put(colLogin, "DrValery@mail.ru");
        cv.put(colPass, "drindryn");
        cv.put(colComment, "сайт mail.ru");
        SQLiteDatabase db=this.getWritableDatabase();
        db.insert(passTable, null, cv);
    }

    public String backup_DB_pass(){
        if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &
                (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            File sd = Environment.getExternalStorageDirectory();
            String path = sd + "/" + dbName + ".xml";
        } else {
            SnackbarHelper.showW(mContext, viewForSnackbar, mContext.getString(R.string.message_permissions_not_granted));
            //new AlertDialog.Builder(mContext) .setMessage(mContext.getString(R.string.message_permissions_not_granted))
            //        .setIcon(android.R.drawable.ic_dialog_alert)
            //        .setPositiveButton(android.R.string.ok, null) .show();
            return "";
        }
        //SecretHelper sh = new SecretHelper();

        Cursor aCur = getAllPass();
        String aStr;
        //формируем базовый блок информации пока только из одного пункта и одного поля
        aStr = "<base>\n" + EncodeDecodeStr(CHECK_RECORD_FOR_BACKUP, CRYPTO_ENCODE).replace("\n", strEndRow).replace("\t", strTab) + "\n<endbase/>\n<pass>\n";

        String tmpStr = "";
        int aInt = 0, rowCounter = 0;

        //сначала пароли
        //aStr = aStr + "<pass>" + "\n";
        aCur.moveToFirst();
        //while (!aCur.isLast()) { делаем через счетчик строк, потому что если запись одна, то курсор уже стоит в конце и цикл while не выполняется
        for (rowCounter =0; rowCounter < aCur.getCount(); rowCounter++){
            for (aInt = 0; aInt < aCur.getColumnCount(); aInt++) {
                if (aInt == 0) {
                    tmpStr =  aCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + tmpStr;
                }
                else {
                    tmpStr = aCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + "\t" + tmpStr;
                }
            }
            aStr = aStr + "\n";
            aCur.moveToNext();
        }
        aCur.close();
        aStr = aStr + "<endpass/>";
/*
        //заметки
        Cursor bCur = getAllNotes();
        aStr = aStr + "<endpass/>\n<notes>\n";
        bCur.moveToFirst();
//        while (!bCur.isLast()) { делаем через счетчик строк, потому что если запись одна, то курсор уже стоит в конце и цикли while не выполняется
        for (rowCounter =0; rowCounter < bCur.getCount(); rowCounter++){
            for (aInt = 0; aInt < bCur.getColumnCount(); aInt++) {
                if (aInt == 0) {
                    tmpStr =  bCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + tmpStr;
                }
                else {
                    tmpStr = bCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + "\t" + tmpStr;
                }
            }
            aStr = aStr + "\n";
            bCur.moveToNext();
        }
        bCur.close();

        //изборажения
        Cursor cCur = getAllImg();
        aStr = aStr + "<endnotes/>\n<images>\n";
        cCur.moveToFirst();
//        while (!сCur.isLast()) { делаем через счетчик строк, потому что если запись одна, то курсор уже стоит в конце и цикл while не выполняется
        for (rowCounter =0; rowCounter < cCur.getCount(); rowCounter++){
            for (aInt = 0; aInt < cCur.getColumnCount(); aInt++) {
                if (aInt == 0) {
                    tmpStr =  cCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + tmpStr;
                }
                else {
                    tmpStr = cCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("") & (!cCur.getColumnName(aInt).equals(colImages_main_id_fk))) {
                        tmpStr = " ";
                    } else if (tmpStr.equals("") & cCur.getColumnName(aInt).equals(colImages_main_id_fk)) {
                        tmpStr = "0";
                    }
                    aStr = aStr + "\t" + tmpStr;
                }
            }
            aStr = aStr + "\n";
            cCur.moveToNext();
        }
        cCur.close();
        //aStr = aStr + "<endimages/>";

        //группы изборажений
        Cursor dCur = getAllImagesMain();
        aStr = aStr + "<endimages/>\n<images_main>\n";
        dCur.moveToFirst();
//        while (!сCur.isLast()) { делаем через счетчик строк, потому что если запись одна, то курсор уже стоит в конце и цикл while не выполняется
        for (rowCounter = 0; rowCounter < dCur.getCount(); rowCounter++){
            for (aInt = 0; aInt < dCur.getColumnCount(); aInt++) {
                if (aInt == 0) {
                    tmpStr =  dCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + tmpStr;
                }
                else {
                    tmpStr = dCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + "\t" + tmpStr;
                }
            }
            aStr = aStr + "\n";
            dCur.moveToNext();
        }
        dCur.close();
        aStr = aStr + "<endimages_main/>";
*/
        //DatabaseDump databaseDump = new DatabaseDump(this.getReadableDatabase(), this.getWritableDatabase().getPath());
        //databaseDump.exportData();
        return aStr;
    }

    public void deletePassAll(){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "DELETE FROM " + passTable;
        db.execSQL(strSQL);
    }

    //получаем фильтрованные данные из таблицы изображений
    public Cursor getAllImgWhere(String columnName, String strWhere){
        SQLiteDatabase db=this.getReadableDatabase();
        String strSelect = "SELECT * from "+imgTable + " WHERE " + columnName + " LIKE '%" + strWhere + "%'" + " ORDER BY _id";
        Cursor cur=db.rawQuery(strSelect, new String[]{});
        cur.moveToFirst();
        return cur;
    }

    public Cursor getAllImgWhere(String[] columnName, String strWhere){
        SQLiteDatabase db=this.getReadableDatabase();
        String aWhere = "";
        for (int i  = 0; i <= columnName.length - 1; i++){
            if (aWhere.equals("")){
                aWhere = " WHERE " + columnName[i] + " LIKE '%" + strWhere + "%'" ;
            } else {
                aWhere = aWhere + " OR " +columnName[i] + " LIKE '%" + strWhere + "%'" ;
            }
        }
        String strSelect = "SELECT * from " + imgTable  + aWhere + " ORDER BY _id";
        Cursor cur=db.rawQuery(strSelect, new String[]{});
        cur.moveToFirst();
        return cur;
    }

    public void updateFavoritePass(int passID, int passFavorite){
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL = "";
        strSQL = "UPDATE " + passTable + " SET passFavorite = " + Integer.toString(passFavorite)
                + " WHERE _id = " + Integer.toString(passID);
        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        };
    }

    public void updateIsCryptoPass(int passID, int passCrypto){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        String loginStr, nameStr, passStr, commentStr;
        Cursor aCur = sqlQuery("SELECT passName, passLogin, passPass, passComment FROM pass WHERE _id = " + Integer.toString(passID));
        aCur.moveToFirst();

        //проверяем, если надо шифровать, шифруем, в противном случае оставляем текст открытым
        if (passCrypto ==1){
            loginStr = EncodeDecodeStr(aCur.getString(1), CRYPTO_ENCODE);
            nameStr = EncodeDecodeStr(aCur.getString(0), CRYPTO_ENCODE);
            passStr = EncodeDecodeStr(aCur.getString(2), CRYPTO_ENCODE);
            commentStr = EncodeDecodeStr(aCur.getString(3), CRYPTO_ENCODE);
        } else {
            nameStr = EncodeDecodeStr(aCur.getString(0), CRYPTO_DECODE);;
            loginStr = EncodeDecodeStr(aCur.getString(1), CRYPTO_DECODE);;
            passStr = EncodeDecodeStr(aCur.getString(2), CRYPTO_DECODE);;
            commentStr = EncodeDecodeStr(aCur.getString(3), CRYPTO_DECODE);;
        }

        strSQL = "UPDATE " + passTable + " SET passName = '" + nameStr
                + "', passLogin = '" + loginStr
                + "', passPass = '" + passStr
                + "', passComment = '" + commentStr
                + "', passDateChange = datetime('now')"
                + ", isCrypt = " + Integer.toString(passCrypto)
                + " WHERE _id = " + Integer.toString(passID);

        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        };
    }

    //получаем все отсортированные данные из таблицы паролей
    public Cursor getAllPassFav(int favValue)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        if (favValue == whereFav) {
            cur = db.rawQuery("SELECT * " + "from " + passTable + " WHERE passFavorite = " + Integer.toString(favValue) + " ORDER BY _id", new String[]{});
        } else {
            cur = db.rawQuery("SELECT * " + "from " + passTable + " ORDER BY _id", new String[]{});
        }
        cur.moveToFirst();
        return cur;
    }

    public Cursor getAllPassFavWhere(int sortBy, int favValue, String searchText) {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        if (sortBy == sortAsc) {
            if (favValue == whereFav) {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE passFavorite = " + Integer.toString(favValue)
                        + " AND (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " ORDER BY " + colPassDateChange +" DESC";
                cur = db.rawQuery(strSQL, new String[]{});
            } else {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " ORDER BY " + colPassDateChange + " DESC";
                cur = db.rawQuery(strSQL, new String[]{});
            }
        } else {
            if (favValue == whereFav) {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " AND passFavorite = " + Integer.toString(favValue) + " ORDER BY " + colPassDateChange + " DESC";
                cur = db.rawQuery(strSQL, new String[]{});
            } else {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " ORDER BY " + colPassDateChange + " DESC";
                cur = db.rawQuery(strSQL, new String[]{});
            }
        }
        cur.moveToFirst();
        return cur;
    }

    public String EncodeDecodeStr(String aStr, int xCode){
        //passApp.
        //получаем значение из глобальной переменной
        String pswd = ((passApp)mContext).getPass();
        String tmpStr = "";
        SecretHelper sh;
        sh = new SecretHelper();
        if (xCode == CRYPTO_ENCODE){
            tmpStr = sh.EncodeStr(aStr, pswd);
        }
        else if (xCode == CRYPTO_DECODE){
            tmpStr = sh.DecodeStr(aStr, pswd);
        }
        return tmpStr;
    }

    public void insertPassForRestore(int id, String name, String login, String pass, String comment, String fav, String dateCreate, String dateChange, String isCrypto){
        //Отдельная функция для создания записей из бэкапа. Тут надо восстановить ровно так как есть в бэкапе, без шифрования/дешифрования
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        String loginStr, nameStr, passStr, commentStr;

        nameStr = name;
        loginStr = login;
        passStr = pass;
        commentStr = comment;

        strSQL = "INSERT INTO " + passTable + " (passName, passLogin, passPass, passComment, passFavorite, passDateCreate, passDateChange, isCrypt) " +
                "VALUES ('" + nameStr + "', '"
                + loginStr + "', '"
                + passStr + "', '"
                + commentStr +"', " + fav +", '" + dateCreate +"', '" + dateChange + "', " + isCrypto +")";

        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        };
    }


    public boolean changePassword(String oldPass, String newPass){
        //При смене пароля надо перешифровать все данные в БД. Чтобы была возможность в случае неуспеха откатить изменения будем делать все в рамках одной транзакции.
        //Надеюсь что транзакционность работает и можно будет провести множество Update в одной транзакции.
        //Вся перекодировка делается здесь, без вызова стандартных функций INSERT/UPDATE потому что глобально парлль еще не поменян.
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        Cursor passCur, notesCur, imgCur, imgMainCur;
        boolean succesTransaction = false;
        SecretHelper sh;
        sh = new SecretHelper();

        passCur = db.rawQuery("SELECT * FROM pass WHERE isCrypt = 1", new String[]{});
        passCur.moveToFirst();

/* для lite версии нам не нужно менять что-либо в других таблицах
        notesCur = db.rawQuery("SELECT * FROM notes WHERE isCrypt = 1", new String[]{});
        notesCur.moveToFirst();

        imgCur = db.rawQuery("SELECT * FROM images WHERE isCrypt = 1", new String[]{});
        imgCur.moveToFirst();

        imgMainCur = db.rawQuery("SELECT * FROM images_main WHERE isCrypt = 1", new String[]{});
        imgMainCur.moveToFirst();

 */

        db.beginTransaction();
        try {
            //Код по update таблиц
            while (!passCur.isAfterLast()){
                strSQL = "UPDATE pass SET "
                        + "passName = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(1), oldPass), newPass)
                        + "', passLogin = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(2), oldPass), newPass)
                        + "', passPass = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(3), oldPass), newPass)
                        + "', passComment = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(4), oldPass), newPass) + "'"
                        + " WHERE _id = " + passCur.getString(0);
                db.execSQL(strSQL);

                passCur.moveToNext();
            }

/* для lite версии нам не нужно менять что-либо в других таблицах
            while (!notesCur.isAfterLast()){
                strSQL = "UPDATE notes SET "
                        + "notesNoteName = '" + sh.EncodeStr(sh.DecodeStr(notesCur.getString(1), oldPass), newPass) + "'"
                        + " WHERE _id = " + notesCur.getString(0);
                db.execSQL(strSQL);

                notesCur.moveToNext();
            }

            while (!imgMainCur.isAfterLast()){
                strSQL = "UPDATE " + imgMainTable + " SET "
                        + colImgMainName + " = '" + sh.EncodeStr(sh.DecodeStr(imgMainCur.getString(1), oldPass), newPass) + "', "
                        + colImgMainComment + " = '" + sh.EncodeStr(sh.DecodeStr(imgMainCur.getString(2), oldPass), newPass) + "' "
                        + " WHERE " + colImgMainID + " = " + imgMainCur.getString(0);
                db.execSQL(strSQL);

                imgMainCur.moveToNext();
            }

            boolean isDecode = true;
            boolean isEncode = true;
            while (!imgCur.isAfterLast()){
                //сюда вставить код по перешифрованию файла
                String fileName = imgCur.getString(imgCur.getColumnIndex("imgFileName"));
                isDecode = isDecode && sh.DecodeFile(mContext, Uri.parse(fileName), oldPass);
                isEncode = isEncode && sh.EncodeFile(mContext, Uri.parse(fileName), newPass);
                strSQL = "UPDATE images SET "
                        + "imgName = '" + sh.EncodeStr(sh.DecodeStr(imgCur.getString(imgCur.getColumnIndex("imgName")), oldPass), newPass)
                        + "', imgComment = '" + sh.EncodeStr(sh.DecodeStr(imgCur.getString(imgCur.getColumnIndex("imgComment")), oldPass), newPass)
                        + "', imgSmallFile = '"
                        + sh.EncodeStr(sh.DecodeStr(imgCur.getString(imgCur.getColumnIndex("imgSmallFile")), oldPass), newPass) + "'"
                        + " WHERE _id = " + imgCur.getString(0);
                db.execSQL(strSQL);
                imgCur.moveToNext();
            }

 */
/* для lite версии нам не нужно менять что-либо в других таблицах
            if (isDecode && isEncode) {

 */
                db.setTransactionSuccessful();
                succesTransaction = true;
/* для lite версии нам не нужно менять что-либо в других таблицах
            }

 */
        }
        finally {
            db.endTransaction();
        }

        return succesTransaction;
    }

    public Cursor sqlQuery(String sqlCode){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(sqlCode, new String[]{});
    }

    public SQLiteDatabase getDB(){
        return this.getWritableDatabase();
    }

    public String getCurDate() {
        //Вычисляем текущую дату и форматируем ее
        Date currentDate = new Date();
        // Форматирование даты как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        return dateText;
    }

}
