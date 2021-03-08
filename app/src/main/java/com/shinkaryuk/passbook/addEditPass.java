package com.shinkaryuk.passbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class addEditPass extends AppCompatActivity {
    public final static int RESULT_EDIT_OK = 1;
    public final static int RESULT_EDIT_CANCELED = -1;
    public final static int RESULT_EDIT_DELETE = -2;
    public final static int PASS_NEW = 0;
    public final static int PASS_EDIT = 1;
    String intIdPass;
    String strNamePass;
    String strLoginPass;
    String strPassPass;
    String strCommentPass;
    String strDateCreate;
    String strDateChange;
    Boolean isCrypto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_pass);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.addEditPassToolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_addeditpass));
        setSupportActionBar(toolbar);

        Button btnDel = (Button) findViewById(R.id.btnDelete);
        CheckBox cbIsCrypto = findViewById(R.id.cb_IsCryptPass);

        intIdPass = getIntent().getExtras().getString("idPass");
        strNamePass = getIntent().getExtras().getString("namePass");
        strLoginPass = getIntent().getExtras().getString("loginPass");
        strPassPass = getIntent().getExtras().getString("passPass");
        strCommentPass = getIntent().getExtras().getString("commentPass");

        strDateCreate = getIntent().getExtras().getString("dateCreatePass");
        if (!strDateCreate.isEmpty()) strDateCreate = new DateTimeHelper().convertNormalDateToSQLite(strDateCreate);

        cbIsCrypto.setChecked((getIntent().getExtras().getString("isCryptoPass").equals("1")) ||
                intIdPass.equals("0"));//флаг шифровать или нет
        //isCrypto = (getIntent().getExtras().getString("isCryptoPass").equals("1"));//флаг шифровать или нет

        if (intIdPass.equals("0")){
            btnDel.setVisibility(View.GONE);
        }

        TextView tvNamePass = (TextView)findViewById(R.id.etNamePass);
        tvNamePass.setText(strNamePass);

        TextView tvLoginPass = (TextView)findViewById(R.id.etLoginPass);
        tvLoginPass.setText(strLoginPass);

        final EditText etPasswd = findViewById(R.id.etPassPass);
        etPasswd.setText(strPassPass);

        TextView tvCommentPass = (TextView)findViewById(R.id.etCommentPass);
        tvCommentPass.setText(strCommentPass);

        TextView tvDateCreate = (TextView) findViewById(R.id.tvDateCreate);
        TextView tvDateChange = (TextView) findViewById(R.id.tvDateChange);
        TextView tvLabelDateCreate = findViewById(R.id.tvLabelDateCreate);
        TextView tvLabelDateChange = findViewById(R.id.tvLabelDateChange);
        View divider4 = findViewById(R.id.divider4);

        String dateText = (new DateTimeHelper().getCurrentDateLikeSQLite());
        strDateChange = dateText;

        if (intIdPass.equals("0")) {
            tvDateCreate.setText(dateText);
            tvDateChange.setText(dateText);

            //прячем элементы с датами, т.к. при создании они не имеют смысла
/*            tvDateCreate.setVisibility(View.GONE);
            tvDateChange.setVisibility(View.GONE);
            tvLabelDateCreate.setVisibility(View.GONE);
            tvLabelDateChange.setVisibility(View.GONE);
            divider4.setVisibility(View.GONE);*/

            strDateCreate = dateText;
            strDateChange = dateText;
        }
        else {
            tvDateCreate.setText(strDateCreate);
            tvDateChange.setText(strDateChange);
        }

        //Показываем или скрываем пароль
        // get the show/hide password Checkbox
        CheckBox cbShowPassswb = (CheckBox) findViewById(R.id.cb_ShowPasswd);

        // add onCheckedListener on checkbox
        // when user clicks on this checkbox, this is the handler.
        cbShowPassswb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    etPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    etPasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

    public void cancelEdit(View v){
        setResult(RESULT_EDIT_CANCELED);
        finish();
    }

    public void okEdit(View v){

        Intent answerIntent = new Intent();

        TextView tvNamePass = (TextView)findViewById(R.id.etNamePass);

        TextView tvLoginPass = (TextView)findViewById(R.id.etLoginPass);

        TextView tvPassPass = (TextView)findViewById(R.id.etPassPass);

        TextView tvCommentPass = (TextView)findViewById(R.id.etCommentPass);

        CheckBox cbIsCrypto = (CheckBox) findViewById(R.id.cb_IsCryptPass);

        String aTmp = tvNamePass.getText().toString();
        answerIntent.putExtra("idPassNew", intIdPass);
        answerIntent.putExtra("namePassNew", tvNamePass.getText().toString());
        answerIntent.putExtra("loginPassNew", tvLoginPass.getText().toString());
        answerIntent.putExtra("passPassNew", tvPassPass.getText().toString());
        answerIntent.putExtra("commentPassNew", tvCommentPass.getText().toString());

//        TextView tvDateCreate = (TextView) findViewById(R.id.tvDateCreate);
//        TextView tvDateChange = (TextView) findViewById(R.id.tvDateChange);
//        answerIntent.putExtra("dateCreatePassNew", strDateCreate);//tvDateCreate.getText().toString());
//        answerIntent.putExtra("dateChangePassNew", strDateChange);//tvDateChange.getText().toString());

        if (cbIsCrypto.isChecked()){
            answerIntent.putExtra("isCryptoPass", 1);
        } else {
            answerIntent.putExtra("isCryptoPass", 0);
        }

        setResult(RESULT_EDIT_OK, answerIntent);
        finish();
    }

    public void deleteEdit(View v){

        Intent answerIntent = new Intent();

        answerIntent.putExtra("idPassNew", intIdPass);

        setResult(RESULT_EDIT_DELETE, answerIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        if (intIdPass.equals("0")) menu.removeItem(R.id.menuDelete);
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

    public void onClickGenPass(View v){
        SecretHelper sh = new SecretHelper();
        String newPass = sh.randomPassword(this);
        TextView tvPassPass = (TextView)findViewById(R.id.etPassPass);
        tvPassPass.setText(newPass);
    }

}
