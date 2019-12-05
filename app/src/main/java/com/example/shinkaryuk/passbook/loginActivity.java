package com.example.shinkaryuk.passbook;

//import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
//import android.widget.ToggleButton;

public class loginActivity extends AppCompatActivity {
    private int inputCounter; //счетчик ввода пароля
    // это будет именем файла настроек
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_PSW = "pswd";
    public static String prefStrPswd = "";
    public static String strPswd = "";
    private SharedPreferences mSettings;
    private int APP_PSWD_IS_NEW = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputCounter = 0;

        //SecretHelper sh;
        //sh = new SecretHelper();
/*        String eStr = sh.EncodeStr("1234567890abcdef", "1234567890abcdef");
        Toast.makeText(this, eStr, Toast.LENGTH_LONG).show();

        String dStr = sh.DecodeStr(eStr, "1234567890abcdef");
        Toast.makeText(this, eStr, Toast.LENGTH_LONG).show();
*/
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (!mSettings.contains(APP_PREFERENCES_PSW)) {
            Button btnOk = findViewById(R.id.btnLoginOk);
            btnOk.setText("Создать");
            APP_PSWD_IS_NEW = 1;
        }
        else {
            prefStrPswd = mSettings.getString(APP_PREFERENCES_PSW, "");
        }

        //Показываем или скрываем пароль
        final EditText etPasswd = (EditText) findViewById(R.id.etPasswd);
        // get the show/hide password Checkbox
        CheckBox cbShowPasssmb = (CheckBox) findViewById(R.id.cb_show_pass);

        // Обрабатываем изменение чекбокса
        cbShowPasssmb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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

        //final EditText editText = (EditText)findViewById(R.id.etPasswd);
        etPasswd.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onClickbtnOK(v);
                    return true;
                }
                return false;
            }
        });

        //Принудительно показываем клавиатуру при фокусе на поле ввода пароля
        etPasswd.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void onClickbtnOK(View v){
        inputCounter += 1;
        if (inputCounter > 3){
            CustomToast.makeText(this, "Досвидос!", Toast.LENGTH_LONG).show();

            finish();
        }

        SecretHelper sh;
        sh = new SecretHelper();

        EditText etPswd = findViewById(R.id.etPasswd);
        strPswd = etPswd.getText().toString();

        String tmpEmpty = "";
        if (strPswd.equals(tmpEmpty)) {
            CustomToast.makeText(this, "Введите пароль", Toast.LENGTH_LONG).show();
        }
        else if(APP_PSWD_IS_NEW == 1){
            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();
        }
        else if (sh.unHashPass(prefStrPswd, strPswd).equals(sh.getPswd(strPswd))){
//устанавливаем глобальную переменную
            String unHashStr = sh.unHashPass(prefStrPswd, strPswd);
            ((passApp)getApplicationContext()).setPass(unHashStr);

            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
            finish();
        }
        else {
            CustomToast.makeText(this, "Неверный пароль", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onPause() {
        String hashPswd;
        super.onPause();
        // Запоминаем данные
        if (APP_PSWD_IS_NEW == 1) {
//устанавливаем глобальную переменную
            SecretHelper sh;
            sh = new SecretHelper();
            hashPswd = sh.hashPass(strPswd);
            String unHashStr = sh.unHashPass(hashPswd, strPswd);
            ((passApp)getApplicationContext()).setPass(unHashStr);

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_PSW, hashPswd);
            editor.apply();
        }
    }

    public void onClickInfo(View view) {
        new AlertDialog.Builder(this) .setMessage(R.string.info_message) .setPositiveButton(android.R.string.ok, null) .show();
    }


}
