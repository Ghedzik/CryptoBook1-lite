package com.shinkaryuk.passbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Загрузчик данных из БД в адаптер для RecyclerView

public class ArrayDataSourceNotes extends RecyclerView.Adapter<ArrayDataSourceNotes.ViewHolder>
        implements ItemTouchHelperAdapter{
    private SQLiteDatabase database;
    private DatabaseHelper sqliteHelper;
    private String[] passRow, imgRow, noteRow;
    private Context mContext;
    private LayoutInflater inflater;
    private List<note> mPass;
    private notesActivity parentActivity;
    SecretHelper sh;
    private String strPswd;
    View viewForSnackbar;
    private Animation show_view, hide_view, cl_show, click_button_scale;



    ArrayDataSourceNotes(Context context, View v){

        sqliteHelper = new DatabaseHelper(context.getApplicationContext(), v);
        passRow = new String[9];
        imgRow = new String[8];
        noteRow = new String[5];

        mContext = context;
        parentActivity = (notesActivity) mContext;
        this.inflater = LayoutInflater.from(context);

        show_view = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.alpha_show_view);
        hide_view = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.alpha_hide_view);
        cl_show = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.cl_show);
        click_button_scale = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.click_button_scale);


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
    private List<note> fillPassArray(){
        String strSearch = ((passApp)mContext.getApplicationContext()).getSearchStr();
        Integer showFav = ((passApp)mContext.getApplicationContext()).getShowFavorites();
        Cursor cursor = sqliteHelper.getAllNotes();
        if (mPass.size()>0){
            mPass.clear();
        }
        if(cursor!=null){
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    note item;
                    if (cursor.getString(cursor.getColumnIndex("isCrypt")).equals("0") ||
                            cursor.getString(cursor.getColumnIndex("isCrypt")).equals("") ||
                            cursor.getString(cursor.getColumnIndex("isCrypt")).isEmpty()) {
                        item = new note(
                                cursor.getString(cursor.getColumnIndex("_id")),
                                cursor.getString(cursor.getColumnIndex("notesNoteName")),
                                cursor.getString(cursor.getColumnIndex("notesDateCreate")),
                                cursor.getString(cursor.getColumnIndex("notesDateChange")),
                                cursor.getString(cursor.getColumnIndex("isCrypt")));
                    } else {
                        item = new note(
                                cursor.getString(cursor.getColumnIndex("_id")),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("notesNoteName")), strPswd),
                                cursor.getString(cursor.getColumnIndex("notesDateCreate")),
                                cursor.getString(cursor.getColumnIndex("notesDateChange")),
                                cursor.getString(cursor.getColumnIndex("isCrypt")));
                    }
                    //item.setDataPass(cursor); //в методе парсим поля курсора в объект
                    //Добавляем в массив, используя условия по Избранным и строке поиска
                    if (strSearch.equals("")) {
                        mPass.add(item);
                    } else {
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

    public class note{
        private String id;
        private String name;
        private String datecreate;
        private String datechange;
        private String crypt;
        private int editing;

        public note(String vId, String vName, String vDatecreate, String vDatechange, String vCrypt){
            id = vId;
            name = vName;
            datecreate = vDatecreate;
            datechange = vDatechange;
            crypt = vCrypt;
            editing = 0;
        }

        public note(String vId, String vName, String vDatecreate, String vDatechange, String vCrypt, int isEditing){
            id = vId;
            name = vName;
            datecreate = vDatecreate;
            datechange = vDatechange;
            crypt = vCrypt;
            editing = isEditing;
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

        public String getCrypt(){
            return crypt;
        }

        public void setCrypt (String value){
            crypt = value;
        }

        public int getEditing(){
            return editing;
        }

        public void setEditing (int value){
            editing = value;
        }

    }

    @Override
    public ArrayDataSourceNotes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_notes, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //final ImageView imageFav, imageCrypt;
        final TextView nameView;
        final TextView dateCreate;
        final View divider, divider5;
        //final ImageButton edit;
        final ImageView imageCrypt;
        final TextView tvCreateNote, tvChangeNote, tvCreateNote3, tvChangeNote3;
        final EditText etCommentNoteL;
        final ImageButton buttonNoteOkL, buttonNoteCancelL, btEditInWindowNote;
        final ImageView ivExpandNote, ivIsOpenEditorNote;


        public ViewHolder(View view){
            super(view);
            imageCrypt = view.findViewById(R.id.colIsCryptoImgNote);
            nameView = view.findViewById(R.id.colNameNotes);
            dateCreate = view.findViewById(R.id.tvDateCreateNote);
            divider = view.findViewById(R.id.dividerNote);

            tvCreateNote = view.findViewById(R.id.tvCreateNote);
            tvChangeNote = view.findViewById(R.id.tvChangeNote);
            tvCreateNote3 = view.findViewById(R.id.tvCreateNote3);
            tvChangeNote3 = view.findViewById(R.id.tvChangeNote3);
            etCommentNoteL = view.findViewById(R.id.etCommentNoteL);
            divider5 = view.findViewById(R.id.divider5);
            buttonNoteOkL = view.findViewById(R.id.buttonNoteOkL);
            buttonNoteCancelL = view.findViewById(R.id.buttonNoteCancelL);
            btEditInWindowNote = view.findViewById(R.id.btEditInWindowNote);
            ivExpandNote = view.findViewById(R.id.ivExpandNote);
            ivIsOpenEditorNote = view.findViewById(R.id.ivIsOpenEditorNote);

            //delete = (ImageButton) view.findViewById(R.id.btDelRV);
            //edit = (ImageButton) view.findViewById(R.id.btEditRV);

            //delete.setVisibility(View.GONE);
            //edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(final ArrayDataSourceNotes.ViewHolder holder, final int position) {
        String strSearch = ((passApp)mContext.getApplicationContext()).getSearchStr();
        note mItem = mPass.get(position);
        holder.nameView.setText(mItem.getName());
        holder.dateCreate.setText(mItem.getDateCreate());

        holder.imageCrypt.setTag(position);
        holder.buttonNoteOkL.setTag(position);
        holder.buttonNoteCancelL.setTag(position);
        holder.btEditInWindowNote.setTag(position);
        holder.ivExpandNote.setTag(position);

        holder.etCommentNoteL.setText(mItem.getName());
        holder.tvCreateNote3.setText(mItem.getDateCreate());
        holder.tvChangeNote3.setText(mItem.getDateChange());

        if (mItem.getEditing() == 0) {
            holder.etCommentNoteL.setVisibility(View.GONE);
            holder.buttonNoteOkL.setVisibility(View.GONE);
            holder.buttonNoteCancelL.setVisibility(View.GONE);
            holder.tvCreateNote.setVisibility(View.GONE);
            holder.tvCreateNote3.setVisibility(View.GONE);
            holder.tvChangeNote.setVisibility(View.GONE);
            holder.tvChangeNote3.setVisibility(View.GONE);
            holder.ivIsOpenEditorNote.setVisibility(View.GONE);
            holder.btEditInWindowNote.setVisibility(View.GONE);
            holder.divider5.setVisibility(View.GONE);
            holder.ivExpandNote.setImageResource(R.mipmap.expand_item);

            holder.nameView.setTypeface(null, Typeface.NORMAL);
            holder.nameView.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
        } else if (mItem.getEditing() == 1) {
            holder.etCommentNoteL.setVisibility(View.VISIBLE);
            holder.etCommentNoteL.startAnimation(show_view);

            holder.buttonNoteOkL.setVisibility(View.VISIBLE);
            holder.buttonNoteOkL.startAnimation(show_view);

            holder.buttonNoteCancelL.setVisibility(View.VISIBLE);
            holder.buttonNoteCancelL.startAnimation(show_view);

            holder.tvCreateNote.setVisibility(View.VISIBLE);
            holder.tvCreateNote.startAnimation(show_view);

            holder.tvCreateNote3.setVisibility(View.VISIBLE);
            holder.tvCreateNote3.startAnimation(show_view);

            holder.tvChangeNote.setVisibility(View.VISIBLE);
            holder.tvChangeNote.startAnimation(show_view);

            holder.tvChangeNote3.setVisibility(View.VISIBLE);
            holder.tvChangeNote3.startAnimation(show_view);

            holder.ivIsOpenEditorNote.setVisibility(View.VISIBLE);
            holder.ivIsOpenEditorNote.startAnimation(show_view);

            holder.btEditInWindowNote.setVisibility(View.VISIBLE);
            holder.btEditInWindowNote.startAnimation(show_view);

            holder.divider5.setVisibility(View.VISIBLE);
            holder.divider5.startAnimation(show_view);

            holder.ivExpandNote.setImageResource(R.mipmap.collapse_item);

            holder.nameView.setTypeface(null, Typeface.BOLD);
            holder.nameView.setTextColor(Color.WHITE);
        }

        holder.buttonNoteOkL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                note aItem = mPass.get(aInt);

                //Вычисляем текущую дату и форматируем ее
                Date currentDate = new Date();
                // Форматирование даты как "день.месяц.год"
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String dateText = dateFormat.format(currentDate);

                AddEditRecord(Integer.parseInt(aItem.getId()),
                        holder.etCommentNoteL.getText().toString(),
                        aItem.getDateCreate(),
                        dateText,
                        aItem.getCrypt());
                aItem.setEditing(0);
                holder.etCommentNoteL.clearFocus();
                ((notesActivity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                SnackbarHelper.show(mContext, v, mContext.getResources().getString(R.string.message_item_save));

                notifyDataSetChanged();
            }
        });

        holder.btEditInWindowNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                note aItem = mPass.get(aInt);
                //Toast.makeText(mContext, aItem.getName(), Toast.LENGTH_LONG).show();

                holder.etCommentNoteL.clearFocus();
                ((notesActivity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                if(aItem.getEditing() == 0) {
                    allItemToNoEdit();
                    aItem.setEditing(1);
                } else aItem.setEditing(0);

                ((notesActivity)mContext).showEditForm(aItem);
            }
        });

        holder.buttonNoteCancelL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                note aItem = mPass.get(aInt);

                aItem.setEditing(0);

                holder.etCommentNoteL.clearFocus();
                ((notesActivity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                if (aItem.getId().equals("0"))
                    mPass.remove(aInt);

                SnackbarHelper.show(mContext, v,mContext.getResources().getString(R.string.message_item_not_save));

                notifyDataSetChanged();
            }
        });
/*
        holder.nameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    //((notesActivity)mContext).onStartDrag(holder);
                }
                return false;
            }
        });
*/

        holder.imageCrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                note aItem = mPass.get(aInt);
                //passCursor.mo
                if (aItem.getCrypt().equals("0")) {
                    sqliteHelper.updateIsCryptoNotes(Integer.parseInt(aItem.getId()), 1);
                    SnackbarHelper.show(mContext, v, mContext.getResources().getString(R.string.message_record_encrypted));
                } else {
                    sqliteHelper.updateIsCryptoNotes(Integer.parseInt(aItem.getId()), 0);
                    SnackbarHelper.showW(mContext, v,mContext.getResources().getString(R.string.message_record_decrypted));
                }
                //Cursor cCur = sqliteHelper.getAllPassFav(((passApp)mContext).getShowFavorites());
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
                note aItem = mPass.get(aInt);
                //Toast.makeText(mContext, aItem.getName(), Toast.LENGTH_LONG).show();

                RegUtils reg = new RegUtils(mContext);

                if (reg.getHowEdit() == RegUtils.EDIT_IN_WINDOW) {
                    ((notesActivity) mContext).showEditForm(aItem);
                    //refreshData();
                } else {
                    if (aItem.getEditing() == 0) {
                        allItemToNoEdit();
                        aItem.setEditing(1);
                    } else {
                        if (Integer.parseInt(aItem.getId()) > 0) {
                            aItem.setEditing(0);
                            SnackbarHelper.show(mContext, v, mContext.getResources().getString(R.string.message_item_not_save));
                        } else if (aItem.getId().equals("0")){
                            mPass.remove(aInt);
                            SnackbarHelper.show(mContext, v, mContext.getResources().getString(R.string.message_item_not_save));
                        }
                    }
                }
                notifyDataSetChanged();
            }
        });

        holder.ivExpandNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                note aItem = mPass.get(aInt);
                //Toast.makeText(mContext, aItem.getName(), Toast.LENGTH_LONG).show();
                RegUtils reg = new RegUtils(mContext);
                if (reg.getHowEdit() == RegUtils.EDIT_IN_WINDOW) {
                    ((notesActivity) mContext).showEditForm(aItem);
                    //refreshData();
                    notifyDataSetChanged();
                } else {
                    if (aItem.getEditing() == 0) {
                        allItemToNoEdit();
                        aItem.setEditing(1);
                    } else {
                        if (Integer.parseInt(aItem.getId()) > 0) {
                            aItem.setEditing(0);
                            SnackbarHelper.show(mContext, v, mContext.getResources().getString(R.string.message_item_not_save));
                        } else if (aItem.getId().equals("0")){
                            mPass.remove(aInt);
                            SnackbarHelper.show(mContext, v, mContext.getResources().getString(R.string.message_item_not_save));
                        }
                    }

                    notifyDataSetChanged();
                }
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
        //SnackbarHelper.showW(mContext, viewForSnackbar,"Попытка вправо");
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

    public void AddEditRecord(int id, String name, String dateCreate, String dateChange, String isCrypto){
        sqliteHelper.insertEditNotes(id, name, dateCreate, dateChange, isCrypto);
        refreshData();
    }

    public void showAlert(final String itemName, final Integer pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.alert_delete))
                .setMessage(mContext.getResources().getString(R.string.alert_do_delete) + itemName + "?")
                .setIcon(android.R.drawable.ic_delete)
                .setCancelable(false)
                .setNegativeButton(mContext.getResources().getString(R.string.control_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //resultAlert = false;
                                refreshData();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(mContext.getResources().getString(R.string.control_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        //resultAlert = true;
                        AddEditRecord(Integer.parseInt(mPass.get(pos).getId()) * (-1), "", "", "", "");
                        SnackbarHelper.showW(mContext, viewForSnackbar,mContext.getResources().getString(R.string.alert_record_deleted));
                        refreshData();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        //if (builder.)
    }

    private void allItemToNoEdit(){
        for (int i = 0; i < mPass.size(); i++) {
            mPass.get(i).setEditing(0);
        }
    }

    public void addNewEmptyItem(View v){
        note item;
        //Вычисляем текущую дату и форматируем ее
        Date currentDate = new Date();
        // Форматирование даты как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        item = new note(
                "0",
                "",
                dateText,
                dateText,
                "1",
                1);

        mPass.add(item);
        SnackbarHelper.show(mContext, v,mContext.getResources().getString(R.string.message_create_new_item));
        notifyDataSetChanged();
    }

}