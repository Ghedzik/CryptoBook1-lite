package com.example.shinkaryuk.passbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.shinkaryuk.passbook.loginActivity.APP_PREFERENCES;
import static com.example.shinkaryuk.passbook.loginActivity.APP_PREFERENCES_PSW;

public class changeMainPasswd extends AppCompatActivity {

    public final static int RESULT_PASSWD_CANCELED = -1;
    public final static int RESULT_PASSWD_OK = 0;
    String oldMainPasswd = "";
    private SharedPreferences mSettings;
    public static String prefStrPswd = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_main_passwd);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //SecretHelper sh = new SecretHelper();

        prefStrPswd = mSettings.getString(APP_PREFERENCES_PSW, "");
        oldMainPasswd = prefStrPswd;

        //Показываем или скрываем пароль
        final EditText etOldPasswd = (EditText) findViewById(R.id.etOldPasswd);
        final EditText etNewPasswd = (EditText) findViewById(R.id.etNewPasswd);
        final EditText etNewConfirmPasswd = (EditText) findViewById(R.id.etConfirmNewPasswd);
        // get the show/hide password Checkbox
        CheckBox cbShowPasssmb = (CheckBox) findViewById(R.id.cbShowPswds);

        // Обрабатываем изменение чекбокса
        cbShowPasssmb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checkbox status is changed from uncheck to checked.
                if (!isChecked) {
                    // show password
                    etOldPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etNewPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etNewConfirmPasswd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    etOldPasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etNewPasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etNewConfirmPasswd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

    public void cancelChangeMainPasswd(View v){
        setResult(RESULT_PASSWD_CANCELED);
        finish();
    }

    public void okChangeMainPasswd(View v){
        TextView etOldPswd = findViewById(R.id.etOldPasswd);
        TextView etNewPswd = findViewById(R.id.etNewPasswd);
        TextView etNewConfirmPswd = findViewById(R.id.etConfirmNewPasswd);

        String aNewPasswd = etNewPswd.getText().toString();
        String aNewConfirmPasswd = etNewConfirmPswd.getText().toString();
        String oldPasswd = etOldPswd.getText().toString();

        ProgressBar pb = findViewById(R.id.progressBar1);

        SecretHelper sh = new SecretHelper();

        DatabaseHelper db;
        db = new DatabaseHelper(this, etNewConfirmPswd);

        if (sh.getPswd(oldPasswd).equals(sh.unHashPass(oldMainPasswd, oldPasswd))){
            if (aNewPasswd.equals(aNewConfirmPasswd)){
                //перед тем как сохранять новый пароль надо перешифровать всю базу
                pb.setVisibility(ProgressBar.VISIBLE);
                if (db.changePassword(sh.getPswd(oldPasswd), sh.getPswd(aNewConfirmPasswd))){
                    //сохраняем новый пароль
                    ((passApp)getApplicationContext()).setPass(sh.getPswd(aNewConfirmPasswd));

                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putString(APP_PREFERENCES_PSW, sh.hashPass(aNewConfirmPasswd));
                    editor.apply();

                    SnackbarHelper.show(this, v,"Пароль успешно изменен!");

                    setResult(RESULT_PASSWD_OK);
                } else {
                    SnackbarHelper.showW(this, v,"Ошибка! Произошел сбой перешифровки БД!");
                    setResult(RESULT_PASSWD_CANCELED);
                }
            }
            else{
                SnackbarHelper.showW(this, v,"Вы не подтвердили новый пароль");
                setResult(RESULT_PASSWD_CANCELED);

            }
        }
        else{
            SnackbarHelper.showW(this, v,"Неверно указан старый пароль");
            setResult(RESULT_PASSWD_CANCELED);
        }
        //вставить код по изменению пароля
//        setResult(RESULT_PASSWD_OK);
        pb.setVisibility(ProgressBar.INVISIBLE);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        //прячем ненужные пунткы меню
        menu.removeItem(R.id.menuDelete);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                okChangeMainPasswd(null);
                return true;
            case R.id.menuCancel:
                cancelChangeMainPasswd(null);
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
