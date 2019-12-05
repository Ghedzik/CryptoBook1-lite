package com.example.shinkaryuk.passbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class addEditNotes extends AppCompatActivity {
    public final static int RESULT_EDIT_OK = 1;
    public final static int RESULT_EDIT_CANCELED = -1;
    public final static int RESULT_EDIT_DELETE = -2;
    String intIdNotes;
    String strNotesNote;
    Button btnOk;
    Button btnCancel;
    String strDateCreate = "";
    String strDateChange = "";
    Boolean isCrypto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addEditNotesToolbar);
        toolbar.setTitle("Заметки");
        setSupportActionBar(toolbar);

        Button btnOk = (Button) findViewById(R.id.btnSave);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnDel = (Button) findViewById(R.id.btnDelete);
        CheckBox cbIsCrypto = (CheckBox) findViewById(R.id.cb_IsCryptNotes);

        intIdNotes = getIntent().getExtras().getString("idNotes");
        strNotesNote = getIntent().getExtras().getString("notesNote");

        //Раскомментировать после того как все добавим
        strDateCreate = getIntent().getExtras().getString("dateCreateNote");
        strDateChange = getIntent().getExtras().getString("dateChangeNote");

        cbIsCrypto.setChecked((getIntent().getExtras().getString("isCryptoNote").equals("1")) ||
                intIdNotes.equals("0"));//флаг шифровать или нет);//флаг шифровать или нет
        //isCrypto = (getIntent().getExtras().getString("isCryptoPass") == "1");//флаг шифровать или нет

        TextView tvNamePass = (TextView)findViewById(R.id.etNotesNote);
        tvNamePass.setText(strNotesNote);

        TextView tvDateCreate = (TextView) findViewById(R.id.tvDateCreateNote);
        TextView tvDateChange = (TextView) findViewById(R.id.tvDateChangeNote);

        if (intIdNotes.equals("0")){
            btnDel.setVisibility(View.GONE);
        }
//Вычисляем текущую дату и форматируем ее
        Date currentDate = new Date();
// Форматирование даты как "день.месяц.год"
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);

        if ((strDateCreate.isEmpty()) || (strDateCreate =="")) {
            tvDateCreate.setText(dateText);
            tvDateChange.setText(dateText);

            strDateCreate = dateText;
            strDateChange = dateText;
        }
        else {
            tvDateCreate.setText(strDateCreate);
            tvDateChange.setText(strDateChange);

            strDateChange = dateText;
        }

    }

    public void cancelEdit(View v){
        setResult(RESULT_EDIT_CANCELED);
        finish();
    }

    public void okEdit(View v){

        Intent answerIntent = new Intent();

        TextView tvNotesNote = (TextView)findViewById(R.id.etNotesNote);

        CheckBox cbIsCrypto = (CheckBox) findViewById(R.id.cb_IsCryptNotes);

        String aTmp = tvNotesNote.getText().toString();
        answerIntent.putExtra("idNotesNew", intIdNotes);
        answerIntent.putExtra("notesNoteNew", tvNotesNote.getText().toString());

        TextView tvDateCreate = (TextView) findViewById(R.id.tvDateCreateNote);
        TextView tvDateChange = (TextView) findViewById(R.id.tvDateChangeNote);
        answerIntent.putExtra("notesCreateDateNew", strDateCreate);//tvDateCreate.getText().toString());
        answerIntent.putExtra("notesChangeDateNew", strDateChange);//tvDateChange.getText().toString());

        if (cbIsCrypto.isChecked()){
            answerIntent.putExtra("isCryptoNew", "1");
        } else {
            answerIntent.putExtra("isCryptoNew", "0");
        }

        setResult(RESULT_EDIT_OK, answerIntent);
        finish();
    }

    public void deleteEdit(View v){

        Intent answerIntent = new Intent();

        answerIntent.putExtra("idNotesNew", intIdNotes);

        setResult(RESULT_EDIT_DELETE, answerIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        if (intIdNotes.equals("0")) menu.removeItem(R.id.menuDelete);
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

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

}
