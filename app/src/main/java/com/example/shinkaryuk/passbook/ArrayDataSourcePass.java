package com.example.shinkaryuk.passbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Загрузчик данных из БД в адаптер для RecyclerView

public class ArrayDataSourcePass extends RecyclerView.Adapter<ArrayDataSourcePass.ViewHolder>
        implements ItemTouchHelperAdapter {
    private SQLiteDatabase database;
    private DatabaseHelper sqliteHelper;
    private String[] passRow, imgRow, noteRow;
    private Context mContext;
    private LayoutInflater inflater;
    private List<pass> mPass;
    private MainActivity parentActivity;
    SecretHelper sh;
    private String strPswd;
    private Animation show_view, hide_view, cl_show, click_button_scale;


    ArrayDataSourcePass(Context context){

        sqliteHelper = new DatabaseHelper(context.getApplicationContext());
        passRow = new String[9];
        imgRow = new String[8];
        noteRow = new String[5];

        mContext = context;
        parentActivity = (MainActivity) mContext;
        this.inflater = LayoutInflater.from(context);
        show_view = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.alpha_show_view);
        hide_view = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.alpha_hide_view);
        cl_show = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.cl_show);
        click_button_scale = AnimationUtils.loadAnimation(parentActivity.getApplication(), R.anim.click_button_scale);

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
    private List<pass> fillPassArray(){
        String strSearch = ((passApp)mContext.getApplicationContext()).getSearchStr();
        Integer showFav = ((passApp)mContext.getApplicationContext()).getShowFavorites();
        Cursor cursor = sqliteHelper.getAllPass();
        if (mPass.size()>0){
            mPass.clear();
        }
        if(cursor!=null){
            if(cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    pass item;
                    if (cursor.getString(cursor.getColumnIndex("isCrypt")).equals("0") ||
                            cursor.getString(cursor.getColumnIndex("isCrypt")).equals("") ||
                            cursor.getString(cursor.getColumnIndex("isCrypt")).isEmpty()) {
                        item = new pass(
                                cursor.getString(cursor.getColumnIndex("_id")),
                                cursor.getString(cursor.getColumnIndex("passName")),
                                cursor.getString(cursor.getColumnIndex("passLogin")),
                                cursor.getString(cursor.getColumnIndex("passPass")),
                                cursor.getString(cursor.getColumnIndex("passComment")),
                                cursor.getString(cursor.getColumnIndex("passFavorite")),
                                cursor.getString(cursor.getColumnIndex("passDateCreate")),
                                cursor.getString(cursor.getColumnIndex("passDateChange")),
                                cursor.getString(cursor.getColumnIndex("isCrypt")));
                    } else {
                        item = new pass(
                                cursor.getString(cursor.getColumnIndex("_id")),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("passName")), strPswd),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("passLogin")), strPswd),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("passPass")), strPswd),
                                sh.DecodeStr(cursor.getString(cursor.getColumnIndex("passComment")), strPswd),
                                cursor.getString(cursor.getColumnIndex("passFavorite")),
                                cursor.getString(cursor.getColumnIndex("passDateCreate")),
                                cursor.getString(cursor.getColumnIndex("passDateChange")),
                                cursor.getString(cursor.getColumnIndex("isCrypt")));
                    }
                    //item.setDataPass(cursor); //в методе парсим поля курсора в объект
                    //Добавляем в массив, используя условия по Избранным и строке поиска
                    if (strSearch.equals("") && showFav == 0) {
                        mPass.add(item);
                    } else if (strSearch.equals("") && showFav == 1) {
                        if (item.getFavorite().equals("1")) {
                            mPass.add(item);
                        }
                    } else if (!strSearch.equals("") && showFav == 0) {
                        int aIndex = item.getName().toLowerCase().indexOf(strSearch.toLowerCase());
                        if (aIndex >= 0) {
                            mPass.add(item);
                        }
                    } else if (!strSearch.equals("") && showFav == 1) {
                        int aIndex = item.getName().toLowerCase().indexOf(strSearch.toLowerCase());
                        if (aIndex >= 0 && item.getFavorite().equals("1")) {
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

    public class pass{
        private String id;
        private String name;
        private String login;
        private String password;
        private String comment;
        private String favorite;
        private String datecreate;
        private String datechange;
        private String crypt;
        private int editing;

        public pass(String vId, String vName, String vLogin, String vPassword, String vComment, String vFavorite, String vDatecreate, String vDatechange, String vCrypt){
            id = vId;
            name = vName;
            login = vLogin;
            password = vPassword;
            comment = vComment;
            favorite = vFavorite;
            datecreate = vDatecreate;
            datechange = vDatechange;
            crypt = vCrypt;
            editing = 0;
        }

        public pass(String vId, String vName, String vLogin, String vPassword, String vComment, String vFavorite, String vDatecreate, String vDatechange, String vCrypt, int isEditing){
            id = vId;
            name = vName;
            login = vLogin;
            password = vPassword;
            comment = vComment;
            favorite = vFavorite;
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

        public String getLogin(){
            return login;
        }

        public void setLogin (String value){
            login = value;
        }

        public String getPassword(){
            return password;
        }

        public void setPassword (String value){
            password = value;
        }

        public String getComment(){
            return comment;
        }

        public void setComment (String value){
            comment = value;
        }

        public String getFavorite(){
            return favorite;
        }

        public void setFavorite (String value){
            favorite = value;
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
    public ArrayDataSourcePass.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //final ImageView imageFav, imageCrypt;
        final TextView nameView;
        final TextView dateCreate;
        final View divider;
        //final ImageButton edit;
        final ImageView imageFav, imageCrypt, ivIsOpenEditor, ivExpandItem;

        final EditText etName, etLogin, etPass, etComment;
        final CheckBox showPass;
        final ImageButton btOk, btCancel, ibKeyGenerate;
        final TextView tvCreate1, tvCreate2, tvChange1, tvChange2, tvNamePass, tvLoginPass, tvPasswordPass, tvCommentPass;
        final ImageButton btEditInWindow;
        final View divider2, divider3;
        final ConstraintLayout clPass;

        public ViewHolder(View view){
            super(view);
            imageFav = (ImageView)view.findViewById(R.id.colFavImg);
            imageCrypt = (ImageView)view.findViewById(R.id.colIsCryptoImg);
            nameView = (TextView) view.findViewById(R.id.colNameText);
            dateCreate = (TextView) view.findViewById(R.id.tvDateCreatePass);
            divider = (View) view.findViewById(R.id.dividerPass);

            etName = (EditText) view.findViewById(R.id.etNamePass2);
            etLogin = (EditText) view.findViewById(R.id.etLoginPass2);
            etPass = (EditText) view.findViewById(R.id.etPassPass2);
            etComment = (EditText) view.findViewById(R.id.etCommentPass2);
            showPass = (CheckBox) view.findViewById(R.id.cb_ShowPasswd2);
            btOk = (ImageButton) view.findViewById(R.id.buttonPassOk);
            btCancel = (ImageButton) view.findViewById(R.id.buttonPassCancel);
            tvCreate1 = (TextView) view.findViewById(R.id.tvCreatePass1);
            tvCreate2 = (TextView) view.findViewById(R.id.tvCreatePass2);
            tvChange1 = (TextView) view.findViewById(R.id.tvChangePass1);
            tvChange2 = (TextView) view.findViewById(R.id.tvChangePass2);
            ibKeyGenerate = (ImageButton) view.findViewById(R.id.ibKeyGenerate);
            ivIsOpenEditor = (ImageView)view.findViewById(R.id.ivIsOpenEditor);
            tvNamePass = (TextView) view.findViewById(R.id.tvNamePass);
            tvLoginPass = (TextView) view.findViewById(R.id.tvLoginPass);
            tvPasswordPass = (TextView) view.findViewById(R.id.tvPasswordPass);
            tvCommentPass = (TextView) view.findViewById(R.id.tvCommentPass);
            btEditInWindow = (ImageButton) view.findViewById(R.id.btEditInWindow);
            divider2 = (View) view.findViewById(R.id.divider2);
            divider3 = (View) view.findViewById(R.id.divider3);
            ivExpandItem = (ImageView)view.findViewById(R.id.ivExpandItem);
            clPass = (ConstraintLayout)view.findViewById(R.id.clPass);

            //etName.setVisibility(View.GONE);
            //etLogin.setVisibility(View.GONE);
            //etPass.setVisibility(View.GONE);
            //etComment.setVisibility(View.GONE);

            //delete = (ImageButton) view.findViewById(R.id.btDelRV);
            //edit = (ImageButton) view.findViewById(R.id.btEditRV);

            //delete.setVisibility(View.GONE);
            //edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(final ArrayDataSourcePass.ViewHolder holder, final int position) {
        String strSearch = ((passApp)mContext.getApplicationContext()).getSearchStr();
        final pass mItem = mPass.get(position);
        holder.nameView.setText(mItem.getName());
        holder.dateCreate.setText(mItem.getDateCreate());

        if (mItem.getEditing() == 0) {
            holder.etName.setVisibility(View.GONE);
            holder.etLogin.setVisibility(View.GONE);
            holder.etPass.setVisibility(View.GONE);
            holder.etComment.setVisibility(View.GONE);
            holder.showPass.setVisibility(View.GONE);
            holder.btOk.setVisibility(View.GONE);
            holder.btCancel.setVisibility(View.GONE);
            holder.tvCreate1.setVisibility(View.GONE);
            holder.tvCreate2.setVisibility(View.GONE);
            holder.tvChange1.setVisibility(View.GONE);
            holder.tvChange2.setVisibility(View.GONE);
            holder.ibKeyGenerate.setVisibility(View.GONE);
            holder.ivIsOpenEditor.setVisibility(View.GONE);
            holder.tvNamePass.setVisibility(View.GONE);
            holder.tvLoginPass.setVisibility(View.GONE);
            holder.tvPasswordPass.setVisibility(View.GONE);
            holder.tvCommentPass.setVisibility(View.GONE);
            holder.btEditInWindow.setVisibility(View.GONE);
            holder.divider2.setVisibility(View.GONE);
            holder.divider3.setVisibility(View.GONE);

            //holder.ivExpandItem.startAnimation(rotateCounterClockwise);
            holder.ivExpandItem.setImageResource(R.mipmap.expand_item);

            holder.nameView.setTypeface(null, Typeface.NORMAL);
            holder.nameView.setTextColor(mContext.getResources().getColor(R.color.сolorTextBlack, null));
        } else if (mItem.getEditing() == 1) {

            //holder.clPass.startAnimation(cl_show);
            holder.etName.startAnimation(show_view);
            holder.etName.setVisibility(View.VISIBLE);
            //holder.etName.startAnimation(show_view);

            holder.etLogin.startAnimation(show_view);
            holder.etLogin.setVisibility(View.VISIBLE);

            holder.etPass.startAnimation(show_view);
            holder.etPass.setVisibility(View.VISIBLE);

            holder.etComment.startAnimation(show_view);
            holder.etComment.setVisibility(View.VISIBLE);

            holder.showPass.startAnimation(show_view);
            holder.showPass.setVisibility(View.VISIBLE);

            holder.btOk.startAnimation(show_view);
            holder.btOk.setVisibility(View.VISIBLE);

            holder.btCancel.startAnimation(show_view);
            holder.btCancel.setVisibility(View.VISIBLE);

            holder.tvCreate1.startAnimation(show_view);
            holder.tvCreate1.setVisibility(View.VISIBLE);

            holder.tvCreate2.startAnimation(show_view);
            holder.tvCreate2.setVisibility(View.VISIBLE);

            holder.tvChange1.startAnimation(show_view);
            holder.tvChange1.setVisibility(View.VISIBLE);

            holder.tvChange2.startAnimation(show_view);
            holder.tvChange2.setVisibility(View.VISIBLE);

            holder.ibKeyGenerate.startAnimation(show_view);
            holder.ibKeyGenerate.setVisibility(View.VISIBLE);

            holder.ivIsOpenEditor.startAnimation(show_view);
            holder.ivIsOpenEditor.setVisibility(View.VISIBLE);

            holder.tvNamePass.startAnimation(show_view);
            holder.tvNamePass.setVisibility(View.VISIBLE);

            holder.tvLoginPass.startAnimation(show_view);
            holder.tvLoginPass.setVisibility(View.VISIBLE);

            holder.tvPasswordPass.startAnimation(show_view);
            holder.tvPasswordPass.setVisibility(View.VISIBLE);

            holder.tvCommentPass.startAnimation(show_view);
            holder.tvCommentPass.setVisibility(View.VISIBLE);

            holder.btEditInWindow.startAnimation(show_view);
            holder.btEditInWindow.setVisibility(View.VISIBLE);

            holder.divider2.startAnimation(show_view);
            holder.divider2.setVisibility(View.VISIBLE);

            holder.divider3.startAnimation(show_view);
            holder.divider3.setVisibility(View.VISIBLE);

            //holder.ivExpandItem.startAnimation(rotate180);
            holder.ivExpandItem.setImageResource(R.mipmap.collapse_item);

            holder.nameView.setTypeface(null, Typeface.BOLD);
            holder.nameView.setTextColor(Color.WHITE);
        }

        holder.imageFav.setTag(position);
        holder.btOk.setTag(position);
        holder.btCancel.setTag(position);
        holder.btEditInWindow.setTag(position);
        holder.ivExpandItem.setTag(position);

        holder.ibKeyGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                v.startAnimation(click_button_scale);

                String newPass = sh.randomPassword(mContext);
                //TextView tvPassPass = (TextView)findViewById(R.id.etPassPass);
                holder.etPass.setText(newPass);
            }
        });

        holder.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                v.startAnimation(click_button_scale);

                int aInt = Integer.parseInt(v.getTag().toString());
                pass aItem = mPass.get(aInt);

                aItem.setEditing(0);

                holder.etName.clearFocus();
                holder.etLogin.clearFocus();
                holder.etPass.clearFocus();
                holder.etComment.clearFocus();
                ((MainActivity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                if (aItem.getId().equals("0"))
                    mPass.remove(aInt);

                notifyDataSetChanged();
            }
        });

        holder.btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                v.startAnimation(click_button_scale);

                int aInt = Integer.parseInt(v.getTag().toString());
                pass aItem = mPass.get(aInt);

                //Вычисляем текущую дату и форматируем ее
                Date currentDate = new Date();
                // Форматирование даты как "день.месяц.год"
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String dateText = dateFormat.format(currentDate);

                AddEditRecord(Integer.parseInt(aItem.getId()),
                        holder.etName.getText().toString(),
                        holder.etLogin.getText().toString(),
                        holder.etPass.getText().toString(),
                        holder.etComment.getText().toString(),
                        aItem.getFavorite(),
                        aItem.getDateCreate(),
                        dateText,
                        aItem.getCrypt());

                aItem.setEditing(0);

                holder.etName.clearFocus();
                holder.etLogin.clearFocus();
                holder.etPass.clearFocus();
                holder.etComment.clearFocus();
                ((MainActivity)mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                notifyDataSetChanged();
            }
        });

        holder.btEditInWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                v.startAnimation(click_button_scale);

                int aInt = Integer.parseInt(v.getTag().toString());
                pass aItem = mPass.get(aInt);
                //Toast.makeText(mContext, aItem.getName(), Toast.LENGTH_LONG).show();

                aItem.setName(holder.etName.getText().toString());
                aItem.setLogin(holder.etLogin.getText().toString());
                aItem.setPassword(holder.etPass.getText().toString());
                aItem.setComment(holder.etComment.getText().toString());

                if(aItem.getEditing() == 0) {
                    allPassToNoEdit();
                    aItem.setEditing(1);
                } else {
                    aItem.setEditing(0);
                }

                ((MainActivity)mContext).showEditForm(aItem);
                //refreshData();
                //notifyDataSetChanged();
            }
        });
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

*/        holder.showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    holder.etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    holder.etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        holder.imageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                pass aItem = mPass.get(aInt);
                //passCursor.mo
                if (aItem.getFavorite().equals("0")) {
                    sqliteHelper.updateFavoritePass(Integer.parseInt(aItem.getId()), 1);
                } else {
                    sqliteHelper.updateFavoritePass(Integer.parseInt(aItem.getId()), 0);
                }
                //Cursor cCur = sqliteHelper.getAllPassFav(((passApp)mContext).getShowFavorites());
                fillPassArray();//swapCursor(cCur);
                notifyDataSetChanged();
            }
        });

        holder.imageCrypt.setTag(position);
        holder.imageCrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                pass aItem = mPass.get(aInt);
                //passCursor.mo
                if (aItem.getCrypt().equals("0")) {
                    sqliteHelper.updateIsCryptoPass(Integer.parseInt(aItem.getId()), 1);
                } else {
                    sqliteHelper.updateIsCryptoPass(Integer.parseInt(aItem.getId()), 0);
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

        holder.etName.setText(mItem.getName());
        holder.etLogin.setText(mItem.getLogin());
        holder.etPass.setText(mItem.getPassword());
        holder.etComment.setText(mItem.getComment());
        holder.tvCreate2.setText(mItem.getDateCreate());
        holder.tvChange2.setText(mItem.getDateChange());

        holder.nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                pass aItem = mPass.get(aInt);
                //Toast.makeText(mContext, aItem.getName(), Toast.LENGTH_LONG).show();
                RegUtils reg = new RegUtils(mContext);
                if (reg.getHowEdit() == RegUtils.EDIT_IN_WINDOW) {
                    ((MainActivity) mContext).showEditForm(aItem);
                    //refreshData();
                    notifyDataSetChanged();
                } else {

                    if (aItem.getEditing() == 0) {
                        allPassToNoEdit();
                        aItem.setEditing(1);
                    } else {
                        aItem.setEditing(0);
                    }

                    notifyDataSetChanged();
                }
            }
        });

        holder.ivExpandItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int aInt = Integer.parseInt(v.getTag().toString());
                pass aItem = mPass.get(aInt);
                //Toast.makeText(mContext, aItem.getName(), Toast.LENGTH_LONG).show();

                //((MainActivity)mContext).showEditForm(aItem);
                //refreshData();
                //notifyDataSetChanged();

                if(aItem.getEditing() == 0) {
                    allPassToNoEdit();
                    aItem.setEditing(1);
                } else {
                    aItem.setEditing(0);
                }

                notifyDataSetChanged();
            }
        });

        //рисуем звездочку Избранное
        if (mItem.getFavorite().equals("0") || mItem.getFavorite().equals("")) {
            holder.imageFav.setImageResource(android.R.drawable.btn_star_big_off);
        } else if (mItem.getFavorite().equals("1")) {
            holder.imageFav.setImageResource(android.R.drawable.btn_star_big_on);
        }

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
            holder.imageFav.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlackL, null));
            holder.nameView.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlackL, null));
        } else {
            holder.imageCrypt.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlack, null));
            holder.imageFav.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlack, null));
            holder.nameView.setBackgroundColor(mContext.getResources().getColor(R.color.сolorBackgroundBlack, null));
        }
    }

    @Override
    public int getItemCount() {
        return mPass.size();
    }

    @Override
    public void onItemDismiss(int position) {
        showAlert("запись", position);
        //notifyItemRemoved(position);
    }

    public void onItemDismissR(int position) {
        //mItems.remove(position);
        CustomToast.makeText(mContext, "Попытка вправо", Toast.LENGTH_LONG).show();
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

    public void AddEditRecord(int id, String name, String login, String pass, String comment, String fav, String dateCreate, String dateChange, String isCrypto){
        sqliteHelper.insertEditPass(id, name, login, pass, comment, fav, dateCreate, dateChange, isCrypto);
        refreshData();
    }

    public void showAlert(final String itemName, final Integer pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Удаление!")
                .setMessage("Запись будет безвозвратно удалена! \nУдалить " + itemName + "?")
                .setIcon(android.R.drawable.ic_delete)
                .setCancelable(false)
                .setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //resultAlert = false;
                                refreshData();
                                dialog.cancel();
                            }
                        })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        //resultAlert = true;

                        AddEditRecord(Integer.parseInt(mPass.get(pos).getId()) * (-1), "", "", "", "", "", "", "", "");
                        refreshData();
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        //if (builder.)
    }

    private void allPassToNoEdit(){
        for (int i = 0; i < mPass.size(); i++) {
            mPass.get(i).setEditing(0);
        }
    }

    public void addNewEmptyItem(){
        pass item;

        //Вычисляем текущую дату и форматируем ее
        Date currentDate = new Date();
        // Форматирование даты как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        item = new pass(
                "0",
                "",
                "",
                "",
                "",
                "0",
                dateText,
                dateText,
                "1",
                1);

        mPass.add(item);
        notifyDataSetChanged();
    }

}


