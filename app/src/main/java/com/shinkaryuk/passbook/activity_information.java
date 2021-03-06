package com.shinkaryuk.passbook;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class activity_information extends AppCompatActivity {

    public DatabaseHelper passDB;
    private Cursor passCursor;
    int aCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.infoToolbar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_info));
        setSupportActionBar(toolbar);

        TextView tViewPswd = (TextView) findViewById(R.id.tvCountPswd);
        TextView tViewImg = (TextView) findViewById(R.id.tvCountImg);
        TextView tViewNotes = (TextView) findViewById(R.id.tvCountNotes);
        TextView tViewPswdNoCrypt = (TextView) findViewById(R.id.tvCountPassNoCrypt);
        TextView tViewNotesNoCrypt = (TextView) findViewById(R.id.tvCountNotesNoCrypt);
        TextView tViewImgNoCrypt = (TextView) findViewById(R.id.tvCountImgNoCrypt);

        //для лайт версии только пароли, остальное не видно
        TextView labelCountImg = (TextView) findViewById(R.id.labelCountImg);
        TextView labelCountImgNoCrypt = (TextView) findViewById(R.id.labelCountImgNoCrypt);
        TextView labelCountNotes = (TextView) findViewById(R.id.labelCountNotes);
        TextView labelCountNotesNoCrypt = (TextView) findViewById(R.id.labelCountNotesNoCrypt);

        tViewImg.setVisibility(View.GONE);
        tViewNotes.setVisibility(View.GONE);
        tViewImgNoCrypt.setVisibility(View.GONE);
        tViewNotesNoCrypt.setVisibility(View.GONE);
        TextView tViewVersionInfo = findViewById(R.id.tvVersionInfo);

        labelCountImg.setVisibility(View.GONE);
        labelCountImgNoCrypt.setVisibility(View.GONE);
        labelCountNotes.setVisibility(View.GONE);
        labelCountNotesNoCrypt.setVisibility(View.GONE);
        //конец изменений для лайт версии


        //создаем базу - класс описан ниже
        passDB = new DatabaseHelper(this, toolbar);
/*
        passCursor = passDB.getAllPass();
        aCount = passCursor.getCount();
        tViewPswd.setText(Integer.toString(aCount));
        passCursor.close();
*/
        String aSql = "SELECT count(*) as cnt, isCrypt, 'pass' as tbl FROM pass GROUP BY isCrypt, tbl\n"
                + "UNION ALL\n"
                + "SELECT count(*) as cnt, isCrypt, 'img' as tbl FROM images GROUP BY isCrypt, tbl\n"
                + "UNION ALL\n"
                + "SELECT count(*) as cnt, isCrypt, 'note' as tbl FROM notes GROUP BY isCrypt, tbl";
        passCursor = passDB.sqlQuery(aSql);

        if (passCursor.getCount() == 0) return;

        Integer countPass = 0, countImg = 0, countNotes = 0, countPassNoCrypt = 0, countImgNoCrypt = 0, countNotesNoCrypt = 0;
        String caseStr;
        passCursor.moveToFirst();
        while (!passCursor.isAfterLast()){
            caseStr = passCursor.getString(passCursor.getColumnIndex("tbl"));
            switch (caseStr) {//passCursor.getColumnIndex("tbl"))) {
                case "pass":
                    countPass += passCursor.getInt(passCursor.getColumnIndex("cnt"));
                    if (passCursor.getInt(passCursor.getColumnIndex("isCrypt")) == 0) {
                        countPassNoCrypt += passCursor.getInt(passCursor.getColumnIndex("cnt"));
                    }
                    break;
                //для лайт версии только пароли, остальное не видно
                /*
                case "img":
                    countImg += passCursor.getInt(passCursor.getColumnIndex("cnt"));
                    if (passCursor.getInt(passCursor.getColumnIndex("isCrypt")) == 0) {
                        countImgNoCrypt += passCursor.getInt(passCursor.getColumnIndex("cnt"));
                    }
                    break;
                case "note":
                    countNotes += passCursor.getInt(passCursor.getColumnIndex("cnt"));
                    if (passCursor.getInt(passCursor.getColumnIndex("isCrypt")) == 0) {
                        countNotesNoCrypt += passCursor.getInt(passCursor.getColumnIndex("cnt"));
                    }
                    break;

                 */
            }
            passCursor.moveToNext();
        }
        tViewPswd.setText(Integer.toString(countPass));
        //для лайт версии только пароли, остальное не видно
        /*
        tViewImg.setText(Integer.toString(countImg));
        tViewNotes.setText(Integer.toString(countNotes));

         */

        tViewPswdNoCrypt.setText(Integer.toString(countPassNoCrypt));
        //для лайт версии только пароли, остальное не видно
        /*
        tViewImgNoCrypt.setText(Integer.toString(countImgNoCrypt));
        tViewNotesNoCrypt.setText(Integer.toString(countNotesNoCrypt));

         */

/*
        passCursor.moveToFirst();
        if (!passCursor.isAfterLast()) {
            tViewPswdNoCrypt.setText(passCursor.getString(passCursor.getColumnIndex("cnt")));
            aCount = passCursor.getInt(passCursor.getColumnIndex("cnt"));
        } else {
            tViewPswdNoCrypt.setText("0");
        }
        passCursor.moveToNext();
        if (!passCursor.isAfterLast()) {
            aCount += passCursor.getInt(passCursor.getColumnIndex("cnt"));
            tViewPswd.setText(Integer.toString(aCount));
        } else {
            tViewPswd.setText("0");
        }
        passCursor.close();

        passCursor = passDB.sqlQuery("SELECT count(*) as cnt, isCrypt FROM images GROUP BY isCrypt ORDER BY isCrypt");
        passCursor.moveToFirst();
        if (!passCursor.isAfterLast()) {
            tViewImgNoCrypt.setText(passCursor.getString(passCursor.getColumnIndex("cnt")));
            aCount = passCursor.getInt(passCursor.getColumnIndex("cnt"));
        } else {
            tViewImgNoCrypt.setText("0");
        }
        passCursor.moveToNext();
        if (!passCursor.isAfterLast()) {
            aCount += passCursor.getInt(passCursor.getColumnIndex("cnt"));
            tViewImg.setText(Integer.toString(aCount));
        } else {
            tViewImg.setText("0");
        }
        passCursor.close();

        passCursor = passDB.sqlQuery("SELECT count(*) as cnt, isCrypt FROM notes GROUP BY isCrypt ORDER BY isCrypt");
        passCursor.moveToFirst();
        if (!passCursor.isAfterLast()) {
            tViewNotesNoCrypt.setText(passCursor.getString(passCursor.getColumnIndex("cnt")));
            aCount = passCursor.getInt(passCursor.getColumnIndex("cnt"));
        } else {
            tViewNotesNoCrypt.setText("0");
        }
        passCursor.moveToNext();
        if (!passCursor.isAfterLast()) {
            aCount += passCursor.getInt(passCursor.getColumnIndex("cnt"));
            tViewNotes.setText(Integer.toString(aCount));
        } else {
            tViewNotes.setText("0");
        }*/
        passCursor.close();

        //выводим версию приложения
        String versionInfo = tViewVersionInfo.getText().toString() + " " + BuildConfig.VERSION_CODE + "." + BuildConfig.VERSION_NAME;
        tViewVersionInfo.setText(versionInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuBack:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
