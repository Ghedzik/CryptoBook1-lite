package com.example.shinkaryuk.passbook;

import android.content.ContentValues;
import android.content.Context;
//import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
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
    public final static int DB_VERSION = 1;

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


    //Таблица заметок
    public static final String notesTable = "notes";
    public static final String colNotesID = "_id";
    public static final String colNotes = "notesNoteName";
    public static final String colNoteDateCreate = "notesDateCreate";
    public static final String colNoteDateChange = "notesDateChange";
    public static final String colNoteIsCrypt = "isCrypt"; //указывает зашифрована ли строка
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

    private Context mContext;


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

        db.execSQL("CREATE TABLE "+passTable+" ("+
                colID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colName+" TEXT, "+
                colLogin+" TEXT, "+
                colPass+" TEXT, "+
                colComment+" TEXT, "+
                colFavorite+" INTEGER, "+
                colPassDateCreate+" TEXT, "+
                colPassDateChange+" TEXT, " +
                colPassIsCrypt + " INTEGER)");

        db.execSQL("CREATE TABLE "+imgTable+" ("+
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

        db.execSQL("CREATE TABLE "+notesTable+" ("+
                colNotesID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                colNotes+" TEXT, "+
                colNoteDateCreate+" TEXT, "+
                colNoteDateChange+" TEXT, " +
                colNoteIsCrypt + " INTEGER)");

    }

    public void altertable(SQLiteDatabase sqLiteDatabase){
//Сюда будем писать изменения в структуры таблиц БД
        //SQLiteDatabase db;
        //sqLiteDatabase = this.getWritableDatabase();
        //sqLiteDatabase.execSQL("ALTER TABLE  " + passTable + " ADD " + colPassDateCreate + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + passTable + " ADD " + colPassDateChange + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + passTable + " ADD " + colPassIsCrypt + " INTEGER");

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


        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgDateCreate + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgSmall + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgLarge + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgDateChange + " TEXT");
        //db.execSQL("ALTER TABLE  " + imgTable + " ADD " + colImgDateChange + " TEXT"); Эту таблицу пока не шифруем

        //sqLiteDatabase.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteDateCreate + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteDateChange + " TEXT");
        //sqLiteDatabase.execSQL("ALTER TABLE  " + notesTable + " ADD " + colNoteIsCrypt + " INTEGER");
    }

    public void upgradeImages(){
        //делаем update таблицы для начала работы системы по принципу хранения изображений в БД
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM images", new String[]{});
        cur.moveToFirst();
        while (!cur.isAfterLast()){
            insertEditImg(cur.getInt(cur.getColumnIndex("_id")),
                    cur.getString(cur.getColumnIndex("imgName")),
                    cur.getString(cur.getColumnIndex("imgFileName")),
                    cur.getString(cur.getColumnIndex("imgComment")),
                    cur.getString(cur.getColumnIndex("imgShortFileName")),
                    cur.getString(cur.getColumnIndex("imgSmallFileName")),
                    cur.getString(cur.getColumnIndex("imgDateCreate")),
                    cur.getString(cur.getColumnIndex("imgDateChange")),
                    "0");
            cur.moveToNext();
        }
    }

    public void updateWholeDB(SQLiteDatabase sqLiteDatabase){
        //сюда будем писать обнолвения данных после обновления структуры базы
        //SQLiteDatabase db;
        //db = this.getWritableDatabase();

        //Вычисляем текущую дату и форматируем ее
        Date currentDate = new Date();
// Форматирование даты как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        sqLiteDatabase.execSQL("UPDATE " + passTable + " SET " + colPassDateCreate +"='" + dateText + "' WHERE " + colPassDateCreate + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + passTable + " SET " + colPassDateChange +"='" + dateText + "' WHERE " + colPassDateChange + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + passTable + " SET " + colPassIsCrypt +"=1");

        sqLiteDatabase.execSQL("UPDATE " + imgTable + " SET " + colImgDateCreate +"='" + dateText + "' WHERE " + colImgDateCreate + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + imgTable + " SET " + colImgDateChange +"='" + dateText + "' WHERE " + colImgDateChange + " IS NULL");

        sqLiteDatabase.execSQL("UPDATE " + notesTable + " SET " + colNoteDateCreate +"='" + dateText + "' WHERE " + colNoteDateCreate + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + notesTable + " SET " + colNoteDateChange +"='" + dateText + "' WHERE " + colNoteDateChange + " IS NULL");
        sqLiteDatabase.execSQL("UPDATE " + notesTable + " SET " + colNoteIsCrypt +"=1");
    }

    public void tmpUpdateTable(){
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        db.execSQL("UPDATE " + passTable + " SET " + colFavorite + " = 0 WHERE " + colFavorite + " IS NULL");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+passTable);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+imgTable);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+notesTable);
        altertable(sqLiteDatabase);
        updateWholeDB(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    //конструктор
    public DatabaseHelper(Context context) {

        super(context, dbName, null, DB_VERSION);
        mContext = context;

    }

    //получаем все отсортированные данные из таблицы паролей
    public Cursor getAllPass()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        cur = db.rawQuery("SELECT * " + "from " + passTable + " ORDER BY _id", new String[]{});
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

    //получаем все данные из таблицы изображений
    public Cursor getAllImg()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * from " + imgTable + " ORDER BY _id", new String[]{});
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

    public void insertEditImg(int id, String name, String img, String comment, String shortImg, String smallImg, String dateCreate, String dateChange, String isCrypto){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase dbr = this.getReadableDatabase();
        String strSQL = "";

        DbBitmapUtility dbBmp = new DbBitmapUtility();
        Bitmap aBmpSmall;
        String mName, mImg, mComment, mShortImg, mSmallImg, mSmallFile = "";

        if (id == 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            aBmpSmall = BitmapFactory.decodeFile(/*strPathImg*/ smallImg, options);
            if (isCrypto.equals("1")) {
                mSmallFile = EncodeDecodeStr(dbBmp.convertBitmapToBase64(aBmpSmall), CRYPTO_ENCODE);
            } else {
                mSmallFile = dbBmp.convertBitmapToBase64(aBmpSmall);
            }
        } else if (id > 0){
            Cursor tmpCur = getAllImgWhere(id);//dbr.rawQuery("SELECT imgSmallFile, isCrypt from images", new String[]{});
            Integer tmpCrypt = tmpCur.getInt(tmpCur.getColumnIndex("isCrypt"));
            mSmallFile = tmpCur.getString(tmpCur.getColumnIndex("imgSmallFile"));
            tmpCur.moveToFirst();
            if (tmpCrypt == Integer.parseInt(isCrypto)){
//                mSmallFile = tmpCur.getString(tmpCur.getColumnIndex("imgSmallFile"));
            } else if (isCrypto.equals("1")){
                mSmallFile = EncodeDecodeStr(mSmallFile, CRYPTO_ENCODE);
            } else if (isCrypto.equals("0")){
                mSmallFile = EncodeDecodeStr(mSmallFile, CRYPTO_DECODE);
            }
        }


        if (isCrypto.equals("1")) {
            mName = EncodeDecodeStr(name, CRYPTO_ENCODE);
            mImg = img;
            mComment = EncodeDecodeStr(comment, CRYPTO_ENCODE);
            mShortImg = shortImg;
            mSmallImg = smallImg;

        } else {
            mName = name;
            mImg = img;
            mComment = comment;
            mShortImg = shortImg;
            mSmallImg = smallImg;

        }

        if (id == 0) {
            strSQL = "INSERT INTO " + imgTable
                    + " (imgName, imgFileName, imgComment, imgShortFileName, imgSmallFileName, imgDateCreate, imgDateChange, imgSmallFile, isCrypt) VALUES ('"
                    + mName + "', '"
                    + mImg + "', '"
                    + mComment +"', '"
                    + mShortImg +"', '"
                    + mSmallImg + "', '"
                    + dateCreate + "', '"
                    + dateChange + "', '"
                    + mSmallFile + "', "
                    + isCrypto + ")";
        }
        else {
            if (id > 0) {
                strSQL = "UPDATE " + imgTable
                        + " SET imgName = '" + mName
                        + "', imgFileName = '" + mImg
                        + "', imgShortFileName = '" + mShortImg
                        + "', imgSmallFileName = '" + mSmallImg
                        + "', imgComment = '" + mComment
                        + "', imgDateCreate = '" + dateCreate
                        + "', imgDateChange = '" + dateChange
                        + "', imgSmallFile = '" + mSmallFile
                        + "', isCrypt = " + isCrypto
                        + " WHERE _id = " + Integer.toString(id);
            }
            else {
                if (id < 0){
                    strSQL = "DELETE FROM " + imgTable + " WHERE _id = " + Integer.toString(id * (-1));
                }
            }
        }
        if (db != null & !strSQL.equals("")) {
            try {
                db.execSQL(strSQL);
            }catch (Exception e) {
                e.printStackTrace();
                String msg = e.getMessage();
                CustomToast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            }

            //сюда надо добавить удаление временных файлов
        };
    }

    public void insertPassForRestore(int id, String name, String img, String comment, String shortImg, String smallImg, String dateCreate, String dateChange, String fileSmall, String isCrypto){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";

        DbBitmapUtility dbu = new DbBitmapUtility();

        String mName, mImg, mComment, mShortImg, mSmallImg, mSmallFile;
        if (fileSmall.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap aBmpSmall = BitmapFactory.decodeFile(/*strPathImg*/ smallImg, options);
            mSmallFile = dbu.convertBitmapToBase64(aBmpSmall);
            if (isCrypto.equals("1")){
                mSmallFile = EncodeDecodeStr(dbu.convertBitmapToBase64(aBmpSmall), CRYPTO_ENCODE);
            }
        } else {
            mSmallFile = fileSmall;
        }
        mName = name;
        mImg = img;
        mComment = comment;
        mShortImg = shortImg;
        mSmallImg = smallImg;
        //mSmallFile = fileSmall;


        if (id == 0) {
            strSQL = "INSERT INTO " + imgTable
                    + " (imgName, imgFileName, imgComment, imgShortFileName, imgSmallFileName, imgDateCreate, imgDateChange, imgSmallFile, isCrypt) VALUES ('"
                    + mName + "', '"
                    + mImg + "', '"
                    + mComment +"', '"
                    + mShortImg +"', '"
                    + mSmallImg + "', '"
                    + dateCreate + "', '"
                    + dateChange + "', '"
                    + mSmallFile + "', "
                    + isCrypto + ")";
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

    public void addRecImg() {
        ContentValues cv = new ContentValues();
        cv.put(colImgName, "Почта mail.ru");
        cv.put(colImgFileName, "");
        cv.put(colImgShortFileName, "");
        cv.put(colImgComment, "сайт mail.ru");
        SQLiteDatabase db=this.getWritableDatabase();
        db.insert(imgTable, null, cv);
    }

    public String backup_DB_pass(){
        File sd = Environment.getExternalStorageDirectory();
        String path = sd + "/" + dbName + ".xml";

        Cursor aCur = getAllPass();
        String aStr = "";
        String tmpStr = "";
        int aInt = 0, rowCounter = 0;

        //сначала пароли
        aStr = "<pass>" + "\n";
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
        //заметки
        Cursor cCur = getAllImg();
        aStr = aStr + "<endnotes/>\n<images>\n";
        cCur.moveToFirst();
//        while (!сCur.isLast()) { делаем через счетчик строк, потому что если запись одна, то курсор уже стоит в конце и цикли while не выполняется
        for (rowCounter =0; rowCounter < cCur.getCount(); rowCounter++){
            for (aInt = 0; aInt < cCur.getColumnCount(); aInt++) {
                if (aInt == 0) {
                    tmpStr =  cCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + tmpStr;
                }
                else {
                    tmpStr = cCur.getString(aInt).replace("\n", strEndRow).replace("\t", strTab);
                    if (tmpStr.equals("")) {tmpStr = " ";}
                    aStr = aStr + "\t" + tmpStr;
                }
            }
            aStr = aStr + "\n";
            cCur.moveToNext();
        }
        cCur.close();
        aStr = aStr + "<endimages/>";
        //DatabaseDump databaseDump = new DatabaseDump(this.getReadableDatabase(), this.getWritableDatabase().getPath());
        //databaseDump.exportData();
        return aStr;
    }

    public void deletePassAll(){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "DELETE FROM " + passTable;
        db.execSQL(strSQL);
    }

    public void deleteImgAll(){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "DELETE FROM " + imgTable;
        db.execSQL(strSQL);
    }

    public void deleteNotesAll(){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "DELETE FROM " + notesTable;
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

    public Cursor getAllImgWhere(Integer id){
        SQLiteDatabase db=this.getReadableDatabase();
        String strSelect = "SELECT * from "+imgTable + " WHERE _id = " + Integer.toString(id);
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

    public Cursor getAllNotes(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur=db.rawQuery("SELECT * "+"from "+ notesTable + " ORDER BY _id", new String[]{});
        cur.moveToFirst();
        return cur;
    }

    public void insertEditNotes(int notesID, String notesNotes, String noteDateCreate, String noteDateChange, String isCrypto){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        if (notesID == 0) {
            if (isCrypto.equals("1")) {
                strSQL = "INSERT INTO " + notesTable
                        + " (notesNoteName, notesDateCreate, notesDateChange, isCrypt) VALUES ('"
                        + EncodeDecodeStr(notesNotes, CRYPTO_ENCODE) + "','" + noteDateCreate + "', '" + noteDateChange + "', " + isCrypto + ")";
            } else {
                strSQL = "INSERT INTO " + notesTable
                        + " (notesNoteName, notesDateCreate, notesDateChange, isCrypt) VALUES ('"
                        + notesNotes + "','" + noteDateCreate + "', '" + noteDateChange + "', " + isCrypto + ")";
            }
        }
        else {
            if (notesID > 0) {
                if (isCrypto.equals("1")) {
                    strSQL = "UPDATE " + notesTable + " SET notesNoteName = '" + EncodeDecodeStr(notesNotes, CRYPTO_ENCODE)
                            + "', notesDateCreate = '" + noteDateCreate
                            + "', notesDateChange = '" + noteDateChange
                            + "', isCrypt = " + isCrypto
                            + " WHERE _id = " + Integer.toString(notesID);
                } else {
                    strSQL = "UPDATE " + notesTable + " SET notesNoteName = '" + notesNotes
                            + "', notesDateCreate = '" + noteDateCreate
                            + "', notesDateChange = '" + noteDateChange
                            + "', isCrypt = " + isCrypto
                            + " WHERE _id = " + Integer.toString(notesID);
                }
            }
            else {
                if (notesID < 0){
                    strSQL = "DELETE FROM " + notesTable + " WHERE _id = " + Integer.toString(notesID * (-1));
                }
            }
        }
        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        };
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
                + "', passDateChange = '" + getCurDate()
                + "', isCrypt = " + Integer.toString(passCrypto)
                + " WHERE _id = " + Integer.toString(passID);

        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        };
    }

    public void updateIsCryptoNotes(int noteID, int noteCrypto){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        String nameStr;
        Cursor aCur = sqlQuery("SELECT notesNoteName FROM notes WHERE _id = " + Integer.toString(noteID));
        aCur.moveToFirst();

        //проверяем, если надо шифровать, шифруем, в противном случае оставляем текст открытым
        if (noteCrypto ==1){
            nameStr = EncodeDecodeStr(aCur.getString(0), CRYPTO_ENCODE);

        } else {
            nameStr = EncodeDecodeStr(aCur.getString(0), CRYPTO_DECODE);
        }

        strSQL = "UPDATE " + notesTable + " SET notesNoteName = '" + nameStr
                + "', notesDateChange = '" + getCurDate()
                + "', isCrypt = " + Integer.toString(noteCrypto)
                + " WHERE _id = " + Integer.toString(noteID);

        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        };
    }

    public void updateIsCryptoImg(int imgID, int imgCrypto){
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        String nameImg, imgComment, imgSmallFile, fileName;
        Cursor aCur = sqlQuery("SELECT imgName, imgFileName, imgShortFileName, imgSmallFileName, imgComment, imgSmallFile FROM images WHERE _id = " + Integer.toString(imgID));
        aCur.moveToFirst();

        fileName = aCur.getString(aCur.getColumnIndex("imgFileName"));
        //проверяем, если надо шифровать, шифруем, в противном случае оставляем текст открытым
        if (imgCrypto ==1){
            nameImg = EncodeDecodeStr(aCur.getString(aCur.getColumnIndex("imgName")), CRYPTO_ENCODE);
            imgComment = EncodeDecodeStr(aCur.getString(aCur.getColumnIndex("imgComment")), CRYPTO_ENCODE);
            imgSmallFile = EncodeDecodeStr(aCur.getString(aCur.getColumnIndex("imgSmallFile")), CRYPTO_ENCODE);
        } else {
            nameImg = EncodeDecodeStr(aCur.getString(aCur.getColumnIndex("imgName")), CRYPTO_DECODE);
            imgComment = EncodeDecodeStr(aCur.getString(aCur.getColumnIndex("imgComment")), CRYPTO_DECODE);
            imgSmallFile = EncodeDecodeStr(aCur.getString(aCur.getColumnIndex("imgSmallFile")), CRYPTO_DECODE);
        }

        strSQL = "UPDATE " + imgTable + " SET imgName = '" + nameImg
                + "', imgComment = '" + imgComment
                + "', imgSmallFile = '" + imgSmallFile
                + "', imgDateChange = '" + getCurDate()
                + "', isCrypt = " + Integer.toString(imgCrypto)
                + " WHERE _id = " + Integer.toString(imgID);

        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        }

        if (imgCrypto == 1) {
            SecretHelper sh = new SecretHelper();
            sh.EncodeFile(mContext, Uri.parse(fileName), ((passApp)mContext).getPass());
        } else {
            SecretHelper sh = new SecretHelper();
            sh.DecodeFile(mContext, Uri.parse(fileName), ((passApp)mContext).getPass());
        }
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

    public Cursor getAllPassFavWhere(int sortBy, int favValue, String searchText)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cur;
        if (sortBy == sortAsc) {
            if (favValue == whereFav) {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE passFavorite = " + Integer.toString(favValue)
                        + " AND (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " ORDER BY UPPER(" + colName +")";
                cur = db.rawQuery(strSQL, new String[]{});
            } else {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " ORDER BY UPPER(" + colName + ")";
                cur = db.rawQuery(strSQL, new String[]{});
            }
        } else {
            if (favValue == whereFav) {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " AND passFavorite = " + Integer.toString(favValue) + " ORDER BY UPPER(" + colName + ") DESC";
                cur = db.rawQuery(strSQL, new String[]{});
            } else {
                String strSQL = "SELECT * " + "from " + passTable
                        + " WHERE (passName LIKE '%" + searchText + "%'" + " OR passComment LIKE '%" + searchText +"%')"
                        + " ORDER BY UPPER(" + colName + ") DESC";
                cur = db.rawQuery(strSQL, new String[]{});
            }
        }
        cur.moveToFirst();
        return cur;
    }

    public Cursor getAllNotesWhere(String searchText){
        SQLiteDatabase db=this.getReadableDatabase();
        String strSQL = "SELECT * "+"from " + notesTable + " WHERE notesNoteName LIKE '%" + searchText + "%'" + " ORDER BY _id";
        Cursor cur=db.rawQuery(strSQL, new String[]{});
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

    public void insertNotesForRestore(int notesID, String notesNotes, String noteDateCreate, String noteDateChange, String isCrypto){
        //Отдельная функция для создания записей из бэкапа. Тут надо восстановить ровно так как есть в бэкапе, без шифрования/дешифрования
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        strSQL = "INSERT INTO " + notesTable
                        + " (notesNoteName, notesDateCreate, notesDateChange, isCrypt) VALUES ('"
                        + notesNotes + "','" + noteDateCreate + "', '" + noteDateChange + "', " + isCrypto + ")";

        if (db != null & !strSQL.equals("")) {
            db.execSQL(strSQL);
        }
    }

    public boolean changePassword(String oldPass, String newPass){
        //При смене пароля надо перешифровать все данные в БД. Чтобы была возможность в случае неуспеха откатить изменения будем делать все в рамках одной транзакции.
        //Надеюсь что транзакционность работает и можно будет провести множество Update в одной транзакции.
        //Вся перекодировка делается здесь, без вызова стандартных функций INSERT/UPDATE потому что глобально парлль еще не поменян.
        SQLiteDatabase db=this.getWritableDatabase();
        String strSQL = "";
        Cursor passCur, notesCur, imgCur;
        boolean succesTransaction = false;
        SecretHelper sh;
        sh = new SecretHelper();

        passCur = db.rawQuery("SELECT * FROM pass WHERE isCrypt = 1", new String[]{});
        passCur.moveToFirst();

        notesCur = db.rawQuery("SELECT * FROM notes WHERE isCrypt = 1", new String[]{});
        notesCur.moveToFirst();

        imgCur = db.rawQuery("SELECT * FROM images WHERE isCrypt = 1", new String[]{});
        imgCur.moveToFirst();
        db.beginTransaction();
        try {
            //Код по update таблиц
            while (!passCur.isAfterLast()){
                strSQL = "UPDATE pass SET "
                        + "passName = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(1), oldPass), newPass)
                        + "', passLogin = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(2), oldPass), newPass)
                        + "', passPass = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(3), oldPass), newPass)
                        + "', passComment = '" + sh.EncodeStr(sh.DecodeStr(passCur.getString(4), oldPass), newPass)
                        + "' WHERE _id = " + passCur.getString(0);
                db.execSQL(strSQL);

                passCur.moveToNext();
            }

            while (!notesCur.isAfterLast()){
                strSQL = "UPDATE notes SET "
                        + "notesNoteName = '" + sh.EncodeStr(sh.DecodeStr(notesCur.getString(1), oldPass), newPass)
                        + "' WHERE _id = " + notesCur.getString(0);
                db.execSQL(strSQL);

                notesCur.moveToNext();
            }

            while (!imgCur.isAfterLast()){
                //сюда вставить код по перешифрованию файла
                String fileName = imgCur.getString(imgCur.getColumnIndex("imgFileName"));
                sh.DecodeFile(mContext, Uri.parse(fileName), oldPass);
                sh.EncodeFile(mContext, Uri.parse(fileName), newPass);
                strSQL = "UPDATE images SET "
                        + "imgName = '" + sh.EncodeStr(sh.DecodeStr(imgCur.getString(imgCur.getColumnIndex("imgName")), oldPass), newPass)
                        + "', imgComment = '" + sh.EncodeStr(sh.DecodeStr(imgCur.getString(imgCur.getColumnIndex("imgComment")), oldPass), newPass)
                        + "', imgSmallFile = '" + sh.EncodeStr(sh.DecodeStr(imgCur.getString(imgCur.getColumnIndex("imgSmallFile")), oldPass), newPass)
                        + "' WHERE _id = " + imgCur.getString(0);
                db.execSQL(strSQL);
                imgCur.moveToNext();
            }

            db.setTransactionSuccessful();
            succesTransaction = true;
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
