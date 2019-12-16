package com.shinkaryuk.passbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Загрузчик данных из БД в адаптер для RecyclerView

public class ArrayDataSourceImg extends RecyclerView.Adapter<ArrayDataSourceImg.ViewHolder>
        implements ItemTouchHelperAdapter{
    private SQLiteDatabase database;
    private DatabaseHelper sqliteHelper;
    private String[] passRow, imgRow, noteRow;
    private Context mContext;
    private LayoutInflater inflater;
    private List<images> mPass;
    private imagesActivity parentActivity;
    SecretHelper sh;
    private String strPswd;
    DbBitmapUtility dbBmp;
    View viewForSnackbar;



    ArrayDataSourceImg(Context context, View v){

        sqliteHelper = new DatabaseHelper(context.getApplicationContext(), v);
        dbBmp = new DbBitmapUtility();
        passRow = new String[9];
        imgRow = new String[8];
        noteRow = new String[5];

        mContext = context;
        parentActivity = (imagesActivity) mContext;
        this.inflater = LayoutInflater.from(context);
        viewForSnackbar = v;

        sh = new SecretHelper();
        strPswd = ((passApp)mContext.getApplicationContext()).getPass();

        mPass = new ArrayList<>();

        fillPassArray();

    }

    public void open(){
        database = sqliteHelper.getWritableDatabase();
    }

    public void close() {
        sqliteHelper.close();
    }

    @NonNull
    public List<images> fillPassArray(){
        String strSearch = ((passApp)mContext.getApplicationContext()).getSearchStr();
        Cursor cursor = sqliteHelper.getAllImg();
        //cursor.
        if (mPass.size()>0){
            mPass.clear();
        }
        if(cursor!=null){
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    images item;
                    if (cursor.getString(cursor.getColumnIndex("isCrypt")).equals("0") ||
                            cursor.getString(cursor.getColumnIndex("isCrypt")).equals("") ||
                            cursor.getString(cursor.getColumnIndex("isCrypt")).isEmpty()) {
                        item = new images(
                                cursor.getString(cursor.getColumnIndex("_id")),
                                cursor.getString(cursor.getColumnIndex("imgName")),
                                cursor.getString(cursor.getColumnIndex("imgFileName")),
                                cursor.getString(cursor.getColumnIndex("imgShortFileName")),
                                cursor.getString(cursor.getColumnIndex("imgSmallFileName")),
                                cursor.getString(cursor.getColumnIndex("imgComment")),
                                cursor.getString(cursor.getColumnIndex("imgDateCreate")),
                                cursor.getString(cursor.getColumnIndex("imgDateChange")),
                                cursor.getString(cursor.getColumnIndex("imgSmallFile")),
                                cursor.getString(cursor.getColumnIndex("isCrypt")));
                    } else {
                        item = new images(
                                cursor.getString(cursor.getColumnIndex("_id")),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("imgName")), strPswd),
                                cursor.getString(cursor.getColumnIndex("imgFileName")),
                                cursor.getString(cursor.getColumnIndex("imgShortFileName")),
                                cursor.getString(cursor.getColumnIndex("imgSmallFileName")),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("imgComment")), strPswd),
                                cursor.getString(cursor.getColumnIndex("imgDateCreate")),
                                cursor.getString(cursor.getColumnIndex("imgDateChange")),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("imgSmallFile")), strPswd),
                                cursor.getString(cursor.getColumnIndex("isCrypt")));
                    }

                    //item.setDataPass(cursor); //в методе парсим поля курсора в объект
                    //Добавляем в массив, используя условия по Избранным и строке поиска
                    if (strSearch.equals("")) {
                        mPass.add(item);
                    } else if (!(strSearch.equals(""))) {
                        int aIndex = item.getName().toLowerCase().indexOf(strSearch.toLowerCase());
                        if (aIndex >= 0) {
                            mPass.add(item);
                        }
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return mPass;
    }

    public class images {
        private String id;
        private String name;
        private String fileName;
        private String shortFileName;
        private String smallNameFile;
        private String comment;
        private String datecreate;
        private String datechange;
        private String smallFile;
        private String crypt;

        public images(String vId, String vName, String vFileName, String vShortFileName, String vSmallNameFile, String vComment, String vDatecreate, String vDatechange, String vSmallFile, String vCrypt){
            id = vId;
            name = vName;
            fileName = vFileName;
            shortFileName = vShortFileName;
            comment = vComment;
            smallNameFile = vSmallNameFile;
            datecreate = vDatecreate;
            datechange = vDatechange;
            smallFile = vSmallFile;
            crypt = vCrypt;
        }

        public String getId(){
            return id;
        }

        public void setId (String value){
            id = value;
        }

        public String getName(){
            return name;
        }

        public void setName (String value){
            name = value;
        }

        public String getFileName(){
            return fileName;
        }

        public void setFileName (String value){
            fileName = value;
        }

        public String getShortFileName(){
            return shortFileName;
        }

        public void setShortFileName (String value){
            shortFileName = value;
        }

        public String getComment(){
            return comment;
        }

        public void setComment (String value){
            comment = value;
        }

        public String getSmallNameFile(){
            return smallNameFile;
        }

        public void setSmallNameFile (String value){
            smallNameFile = value;
        }

        public String getDateCreate(){
            return datecreate;
        }

        public void setDateCreate (String value){
            datecreate = value;
        }

        public String getDateChange(){
            return datechange;
        }

        public void setDateChange (String value){
            datechange = value;
        }

        public String getSmallFile(){
            return smallFile;
        }

        public void setSmallFile (String value){
            smallFile = value;
        }

        public String getCrypt(){
            return crypt;
        }

        public void setCrypt (String value){
            crypt = value;
        }

    }

    @Override
    public ArrayDataSourceImg.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_img, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //final ImageView imageFav, imageCrypt;
        final TextView nameView;
        final TextView dateCreate;
        final View divider;
        //final ImageButton edit;
        final ImageView imageSmall;
        final ImageView imageCrypt;
        final ProgressBar pb;

        public ViewHolder(View view){
            super(view);
            imageSmall = (ImageView)view.findViewById(R.id.colImg);
            nameView = (TextView) view.findViewById(R.id.colNameImg);
            dateCreate = (TextView) view.findViewById(R.id.tvDateCreateImg);
            imageCrypt = (ImageView)view.findViewById(R.id.colIsCryptoImgImg);
            pb = (ProgressBar) view.findViewById(R.id.pbImg);
            divider = (View) view.findViewById(R.id.dividerImg);

            //delete = (ImageButton) view.findViewById(R.id.btDelRV);
            //edit = (ImageButton) view.findViewById(R.id.btEditRV);

            //delete.setVisibility(View.GONE);
            //edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(final ArrayDataSourceImg.ViewHolder holder, final int position) {
        String strSearch = ((passApp)mContext.getApplicationContext()).getSearchStr();
        images mItem = mPass.get(position);
        holder.nameView.setText(mItem.getName());
        holder.dateCreate.setText(mItem.getDateCreate());
/*
        holder.nameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    //((MainActivity)mContext).onStartDrag(holder);
                }
                return false;
            }
        });
*/
        holder.imageCrypt.setTag(position);
        holder.imageCrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ProgressBar pb = (ProgressBar) ((imagesActivity)mContext).findViewById(R.id.pbImg);
                holder.pb.setVisibility(View.VISIBLE);
                int aInt = Integer.parseInt(v.getTag().toString());

                ArrayDataSourceImg.images aItem = mPass.get(aInt);
                //passCursor.mo
                if (aItem.getCrypt().equals("0")) {
                    sqliteHelper.updateIsCryptoImg(Integer.parseInt(aItem.getId()), 1);
                    SnackbarHelper.show(mContext, v, mContext.getResources().getString(R.string.message_record_encrypted));
                } else {
                    sqliteHelper.updateIsCryptoImg(Integer.parseInt(aItem.getId()), 0);
                    SnackbarHelper.showW(mContext, v,mContext.getResources().getString(R.string.message_record_decrypted));
                }
                //Cursor cCur = sqliteHelper.getAllPassFav(((passApp)mContext).getShowFavorites());

                fillPassArray();//swapCursor(cCur);
                holder.pb.setVisibility(View.GONE);
                notifyDataSetChanged();
            }
        });

        holder.imageSmall.setTag(position);
        //рисуем маленькое превью изобрадение
        //holder.imageSmall.setImageURI(Uri.parse(mItem.getSmallNameFile()));
        if (mItem.getSmallFile() != null){
            if (!mItem.getSmallFile().equals("")){
                holder.imageSmall.setImageBitmap(dbBmp.convertBase64ToBitmap(mItem.getSmallFile()));
            }
        }


        holder.imageSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                images aItem = mPass.get(aInt);
                ((imagesActivity)mContext).showImage(aItem);
                fillPassArray();//swapCursor(cCur);
                notifyDataSetChanged();
            }
        });


        holder.nameView.setTag(position);
        if (!strSearch.isEmpty() && !strSearch.equals("")) {
            int aIndex = holder.nameView.getText().toString().toLowerCase().indexOf(strSearch.toLowerCase());
            if (aIndex >= 0){
                holder.nameView.setTextColor(mContext.getResources().getColor(R.color.сolorTextFound, null));
            } else {
                holder.nameView.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
            }
        } else {
            holder.nameView.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
        }
        holder.nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                images aItem = mPass.get(aInt);
                //Toast.makeText(mContext, aItem.getName(), Toast.LENGTH_LONG).show();
                ((imagesActivity)mContext).showEditForm(aItem);
                refreshData();
            }
        });


        if (mItem.getCrypt().equals("0") || mItem.getCrypt().equals("")) {
            holder.imageCrypt.setImageResource(R.mipmap.ic_unlock_outline_white_24dp);
            holder.divider.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent, null));
            holder.nameView.setTextColor(mContext.getResources().getColor(R.color.colorAccentNoCrypt, null));
            holder.dateCreate.setTextColor(mContext.getResources().getColor(R.color.colorAccentNoCrypt, null));
        } else if (mItem.getCrypt().equals("1")) {
            holder.imageCrypt.setImageResource(R.mipmap.ic_lock_outline_white_24dp);
            holder.divider.setBackgroundColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
            holder.nameView.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
            holder.dateCreate.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
        }

        if ((position % 2) != 0) {
            holder.imageCrypt.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlackL, null));
            //holder.imageFav.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlackL, null));
            holder.nameView.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlackL, null));
        } else {
            holder.imageCrypt.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlack, null));
            //holder.imageFav.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlack, null));
            holder.nameView.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlack, null));
        }
    }

    @Override
    public int getItemCount() {
        return mPass.size();
    }

    @Override
    public void onItemDismiss(int position) {
        //mItems.remove(position);
        //Toast.makeText(mContext, "Попытка удалить", Toast.LENGTH_LONG).show();
        showAlert("", position);
        //notifyItemRemoved(position);
    }

    public void onItemDismissR(int position) {
        //mItems.remove(position);
        //SnackbarHelper.show(mContext, viewForSnackbar,"Попытка вправо");
        refreshData();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mPass, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mPass, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void refreshData(){
        fillPassArray();
        notifyDataSetChanged();
    }

    public void AddEditRecord(int id, String name, String fileName, String shortFileName, String comment, String smallFileName, String dateCreate, String dateChange, String isCrypto){
        sqliteHelper.insertEditImg(id, name, fileName, comment, shortFileName, smallFileName, dateCreate, dateChange, isCrypto);
        refreshData();
    }

    public void showAlert(final String itemName, final Integer pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.alert_delete))
                .setMessage(mContext.getResources().getString(R.string.alert_do_delete) + itemName + "?")
                .setIcon(android.R.drawable.ic_delete)
                .setCancelable(false)
                .setNegativeButton(mContext.getResources().getString(R.string.alert_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //resultAlert = false;
                                refreshData();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(mContext.getResources().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        //resultAlert = true;
                        AddEditRecord(Integer.parseInt(mPass.get(pos).getId()) * (-1), "", "", "", "", "", "", "", "");
                        SnackbarHelper.showW(mContext, viewForSnackbar, mContext.getResources().getString(R.string.alert_record_deleted));
                        refreshData();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        //if (builder.)
    }
}