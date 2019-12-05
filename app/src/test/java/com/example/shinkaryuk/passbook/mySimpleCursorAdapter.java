package com.example.shinkaryuk.passbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import java.io.File;

import static com.example.shinkaryuk.passbook.MainActivity.PASS_EDIT;

/**
 * Created by shinkaryuk on 10.12.2017.
 */

public class mySimpleCursorAdapter extends SimpleCursorAdapter implements View.OnTouchListener {
    int aCounter1 = 0;
    int aCounter2 = 0;
    String nameTable = "";
    private static final String passTable = "pass";
    private static final String imgTable = "images";
    private static final String notesTable = "notes";

    private Cursor aCursor;
    private Context mContext;
    private Context aContext;
    SecretHelper sh;
    private String strPswd;
    View.OnClickListener onClickFavImg, onClickCryptoImg, onClickCryptoImgNote, onClickTextView, onClickEditButton;
    DatabaseHelper cryptoDB;
    private final int INVALID = -1;
    protected int DELETE_POS = -1;
    private ListView mList;
    public Boolean resultAlert;
    DialogInterface.OnClickListener onClickOK, onClickCancel;

    public mySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, String tableName, Context actContext) {
        super(context, layout, c, from, to, flags);
        nameTable = tableName;
        mContext = context;
        aContext = actContext;
        cryptoDB = new DatabaseHelper(mContext);
        resultAlert = false;
        //aCursor = c;
        //mList = list;

        onClickFavImg = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v, "Нажалась", Toast.LENGTH_LONG).show();
                int aInt = Integer.parseInt(v.getTag().toString());
                getCursor().moveToPosition(aInt);
                //passCursor.mo
                if (getCursor().getInt(5) == 0) {
                    cryptoDB.updateFavoritePass(getCursor().getInt(0), 1);
                } else {
                    cryptoDB.updateFavoritePass(getCursor().getInt(0), 0);
                }
                Cursor cCur = cryptoDB.getAllPassFav(((passApp)mContext).getShowFavorites());
                swapCursor(cCur);
            }
        };

        onClickCryptoImg = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v, "Нажалась", Toast.LENGTH_LONG).show();
                int aInt = Integer.parseInt(v.getTag().toString());
                getCursor().moveToPosition(aInt);
                //passCursor.mo
                if (getCursor().getInt(8) == 0) {
                    cryptoDB.updateIsCryptoPass(getCursor().getInt(0), 1);
                } else {
                    cryptoDB.updateIsCryptoPass(getCursor().getInt(0), 0);
                }
                Cursor cCur = cryptoDB.getAllPassFav(((passApp)mContext).getShowFavorites());
                swapCursor(cCur);
                notifyDataSetChanged();
            }
        };

        onClickCryptoImgNote = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v, "Нажалась", Toast.LENGTH_LONG).show();
                int aInt = Integer.parseInt(v.getTag().toString());
                getCursor().moveToPosition(aInt);
                //passCursor.mo
                if (getCursor().getInt(4) == 0) {
                    cryptoDB.updateIsCryptoNotes(getCursor().getInt(0), 1);
                } else {
                    cryptoDB.updateIsCryptoNotes(getCursor().getInt(0), 0);
                }
                Cursor cCur = cryptoDB.getAllNotes();
                swapCursor(cCur);
                notifyDataSetChanged();
            }
        };

        onClickEditButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int aInt = Integer.parseInt(v.getTag().toString());
                getCursor().moveToPosition(aInt);
                //((MainActivity)aContext).showEditForm(aInt);
            }
        };

        sh = new SecretHelper();
        strPswd = ((passApp)mContext).getPass();
    }
/*
    @Override
    public void setViewText(TextView v, String text) {
        // если нужный нам элемент, то разрисовываем
        if (nameTable.equals(passTable)) {//проверяем имя таблицы, чтобы рисовать правильно pass и images
            if (v.getId() == R.id.colFavImg) {//проверяем поле - если checkbox, то текст заменяем на "" и устанавливаем флаг
                // метод супер-класса, который вставляет текст
                super.setViewText(v, "");
                CheckBox cbFav = (CheckBox) v;
                if (text.equals("0")){
                    cbFav.setChecked(false);
                } else {
                    cbFav.setChecked(true);
                }

            }else {
                super.setViewText(v, text);
            }
        }else {
            super.setViewText(v, text);
        }
    }*/

    @Override
    public void setViewImage(ImageView v, String value){
        // разрисовываем ImageView
        if (nameTable.equals(passTable)) {//рисуем images Favorites для таблицы паролей
            if (v.getId() == R.id.colFavImg) {
                if (value.equals("0") || value.equals("")) {
                    v.setImageResource(android.R.drawable.btn_star_big_off);
                } else if (value.equals("1")) {
                    v.setImageResource(android.R.drawable.btn_star_big_on);
                }
                int aPos = this.getCursor().getPosition();
                v.setTag(aPos);
                v.setOnClickListener(onClickFavImg);
//            v.setTag(aCursor.getInt(0));
            }
            else if (v.getId() == R.id.colIsCryptoImg){
                if (value.equals("0") || value.equals("")) {
                    v.setImageResource(R.mipmap.ic_unlock_outline_white_24dp);
                } else if (value.equals("1")) {
                    v.setImageResource(R.mipmap.ic_lock_outline_white_24dp);
                }
                int aPos = this.getCursor().getPosition();
                v.setTag(aPos);
                v.setOnClickListener(onClickCryptoImg);
            }
            /*else if (v.getId() == R.id.btDel){
                //добавляем иконку на кнопку Delete
                v.setImageResource(android.R.drawable.ic_menu_delete);
            }
            else if (v.getId() == R.id.btEdit) {
                //добавляем иконку на кнопку Edit
                v.setImageResource(android.R.drawable.ic_menu_edit);
            }*/
        } /*else if (nameTable.equals(notesTable)){//прорисовываем иконки для кнопок удалить редактировать на таблице заметок
            if (v.getId() == R.id.btDelNote){
                //добавляем иконку на кнопку Delete
                v.setImageResource(android.R.drawable.ic_menu_delete);
            }
            else if (v.getId() == R.id.btEditNote) {
                //добавляем иконку на кнопку Edit
                v.setImageResource(android.R.drawable.ic_menu_edit);
            }
            else if (v.getId() == R.id.colIsCryptoImgNote) {
                if (value.equals("0") || value.equals("") || value.isEmpty()) {
                    v.setImageResource(R.mipmap.ic_unlock_outline_white_24dp);
                } else if (value.equals("1")) {
                    v.setImageResource(R.mipmap.ic_lock_outline_white_24dp);
                }
                int aPos = this.getCursor().getPosition();
                v.setTag(aPos);
                v.setOnClickListener(onClickCryptoImgNote);
            }
        } else {
            if (v.getId() == R.id.btDelImg){
                //добавляем иконку на кнопку Delete
                v.setImageResource(android.R.drawable.ic_menu_delete);
            }
            else if (v.getId() == R.id.btEditImg) {
                //добавляем иконку на кнопку Edit
                v.setImageResource(android.R.drawable.ic_menu_edit);
            }*/ else {
                // метод супер-класса в нем по идее должно добавляться изображение для ImageEdit маленьких фото для документов и сканов
                super.setViewImage(v, value);
            }


    }

    @Override
    public void setViewText(TextView v, String text) {
        // расшифровываем значения полей из таблиц pass (name, login, password, comment), notes (notes)
        String decodeStr = "";
        if (nameTable.equals(notesTable) || nameTable.equals(passTable)){
            if (v.getId() == R.id.colNameNotes){
                if (this.getCursor().getInt(4) == 1){
                    decodeStr = sh.DecodeStr(text, strPswd);
                } else {
                    decodeStr = text;
                }
            } else if (v.getId() == R.id.colNameText){
                int aInt = this.getCursor().getColumnIndex("isCrypt");
                int isCrypt = this.getCursor().getInt(aInt);
                if (isCrypt == 1){
                    decodeStr = sh.DecodeStr(text, strPswd);

                } else {
                    decodeStr = text;
                }
            } else {
                decodeStr = text;
            }
        } else {
            decodeStr = text;
        }

        /*Подкрашиваем строки удовлетворящие поиску (строка поиска сохраняется заранее в глобальной переменной
        в модуле passApp. Если строка не удовлетворяет строке поиска, возвращаем цвет текста обратно
        */
        String aStr = ((passApp)mContext).getSearchStr();
        if (!aStr.equals("")) {
            int aIndex = decodeStr.toLowerCase().indexOf(aStr.toLowerCase());
            if (aIndex >= 0) {
                v.setTextColor(mContext.getResources().getColor(R.color.сolorTextFound, null));
            } else {
                v.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
            }
        } else {
            v.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
        }

        super.setViewText(v, decodeStr);
/*
        if (nameTable.equals(notesTable) || nameTable.equals(passTable)) {//проверяем имя таблицы, чтобы рисовать правильно pass и images
            if ((v.getId() == R.id.colNameNotes ||
                    v.getId() == R.id.colNameText) | (aCursor.getInt(8)) == 1) {//проверяем поле - если поле Notes или название строки пароля, то текст расшифровываем
                // метод супер-класса, который вставляет текст
                super.setViewText(v, sh.DecodeStr(text, strPswd));
            } else {
                super.setViewText(v, text);
            }
        }else {
            super.setViewText(v, text);
        }*/
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        if (convertView == null) {
            if (nameTable.equals(passTable)) {
                convertView = LayoutInflater.from(/*MainActivity.this*/ mContext).inflate(R.layout.item, null);
            } else if (nameTable.equals(notesTable)){
                convertView = LayoutInflater.from(/*MainActivity.this*/ mContext).inflate(R.layout.item_notes, null);
            }else if (nameTable.equals(imgTable)){
                convertView = LayoutInflater.from(/*MainActivity.this*/ mContext).inflate(R.layout.item_img, null);
            }
        }

        this.getCursor().moveToPosition(position);

        if (nameTable.equals(passTable)) {
            TextView text = ViewHolderPattern.get(convertView, R.id.colNameText);
            setViewText(text, this.getCursor().getString(1));

            TextView textDate = ViewHolderPattern.get(convertView, R.id.tvDateCreatePass);
            setViewText(textDate, this.getCursor().getString(6));
            textDate.setOnTouchListener(this);

            ImageView imageFav = ViewHolderPattern.get(convertView, R.id.colFavImg);
            ImageView imageCrypto = ViewHolderPattern.get(convertView, R.id.colIsCryptoImg);
            setViewImage(imageFav, this.getCursor().getString(5));
            setViewImage(imageCrypto, this.getCursor().getString(8));

            /*ImageButton delete = ViewHolderPattern.get(convertView, R.id.btDel);
            ImageButton edit = ViewHolderPattern.get(convertView, R.id.btEdit);
            if (DELETE_POS == position) {
                delete.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                textDate.setVisibility(View.GONE);
            } else {
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                textDate.setVisibility(View.VISIBLE);
            }

            delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showAlert("запись", position);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //((MainActivity)aContext).showEditForm(position);
                    DELETE_POS = INVALID;
                }
            });

            setViewImage(delete, "-1");
            setViewImage(edit, "-1");
*/
        } else if (nameTable.equals(notesTable)) {
            TextView text = ViewHolderPattern.get(convertView, R.id.colNameNotes);
            setViewText(text, this.getCursor().getString(1));

            TextView textDate = ViewHolderPattern.get(convertView, R.id.tvDateCreateNote);
            setViewText(textDate, this.getCursor().getString(2));

            ImageView imageCrypto = ViewHolderPattern.get(convertView, R.id.colIsCryptoImgNote);
            setViewImage(imageCrypto, this.getCursor().getString(4));

            /*ImageButton delete = ViewHolderPattern.get(convertView, R.id.btDelNote);
            ImageButton edit = ViewHolderPattern.get(convertView, R.id.btEditNote);
            if (DELETE_POS == position) {
                delete.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                textDate.setVisibility(View.GONE);
            } else {
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                textDate.setVisibility(View.VISIBLE);
            }

            delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showAlert("заметку", position);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((notesActivity)aContext).showEditForm(position);
                    DELETE_POS = INVALID;
                }
            });

            setViewImage(delete, "-1");
            setViewImage(edit, "-1");
*/

        } else if (nameTable.equals(imgTable)) {
            TextView text = ViewHolderPattern.get(convertView, R.id.colNameImg);
            setViewText(text, this.getCursor().getString(1));

            TextView textDate = ViewHolderPattern.get(convertView, R.id.tvDateCreateImg);
            setViewText(textDate, this.getCursor().getString(6));

            ImageView imageSmall = ViewHolderPattern.get(convertView, R.id.colImg);
            setViewImage(imageSmall, this.getCursor().getString(4));

            /*ImageButton delete = ViewHolderPattern.get(convertView, R.id.btDelImg);
            ImageButton edit = ViewHolderPattern.get(convertView, R.id.btEditImg);
            if (DELETE_POS == position) {
                delete.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                textDate.setVisibility(View.GONE);
            } else {
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                textDate.setVisibility(View.VISIBLE);
            }

            imageSmall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((imagesActivity)aContext).showImage(position);
                    DELETE_POS = INVALID;
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showAlert("документ", position);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((imagesActivity)aContext).showEditForm(position);
                    DELETE_POS = INVALID;
                }
            });

            setViewImage(delete, "-1");
            setViewImage(edit, "-1");*/
        }

        return convertView;
    }

    public void onSwipeItem(boolean isRight, int position) {
        if (isRight == false) {
            DELETE_POS = position;
        } else if (DELETE_POS == position) {
            DELETE_POS = INVALID;
        }
        //
        notifyDataSetChanged();
    }

    public void deletePass(int pos) {
        //
        //удаляем элемент из БД
        this.getCursor().moveToPosition(pos);
        cryptoDB.insertEditPass(this.getCursor().getInt(0) * (-1), "", "", "", "", "", "", "", "");
        Cursor cCur = cryptoDB.getAllPassFav(((passApp) mContext).getShowFavorites());
        swapCursor(cCur);
        DELETE_POS = INVALID;
        notifyDataSetChanged();

    }

    public void deleteNote(int pos) {
        //
        //удаляем элемент из БД
        this.getCursor().moveToPosition(pos);
        cryptoDB.insertEditNotes(this.getCursor().getInt(0) * (-1), "", "", "", "0");
        Cursor cCur = cryptoDB.getAllNotes();
        swapCursor(cCur);
        DELETE_POS = INVALID;
        notifyDataSetChanged();
    }

    public void deleteImg(int pos) {
        //
        //удаляем элемент из БД
        this.getCursor().moveToPosition(pos);
        cryptoDB.insertEditImg(this.getCursor().getInt(0) * (-1), "", "", "", "", "", "", "");
        Cursor cCur = cryptoDB.getAllImg();
        swapCursor(cCur);
        DELETE_POS = INVALID;
        notifyDataSetChanged();
    }

    public static class ViewHolderPattern {
        // I added a generic return type to reduce the casting noise in client
        // code
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    public void showAlert(final String itemName, final Integer pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
        builder.setTitle("Удаление!")
                .setMessage("Удалить " + itemName + "?")
                .setIcon(android.R.drawable.ic_delete)
                .setCancelable(false)
                .setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //resultAlert = false;
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        //resultAlert = true;
                        if (itemName.equals("документ")) {
                            deleteImg(pos);
                        } else if (itemName.equals("заметку")) {
                            deleteNote(pos);
                        } else if (itemName.equals("запись")) {
                            deletePass(pos);
                        }
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        //if (builder.)
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                /*touchFlag = true;
                offset_x = (int) event.getX();
                offset_y = (int) event.getY();
                selected_item = v;
                imageParams = (RelativeLayout.LayoutParams)v.getLayoutParams();*/
                break;
            case MotionEvent.ACTION_UP:
                /*selected_item = null;
                touchFlag = false;*/
                break;
            default:
                break;
        }
        return false;
    }

}
