package com.example.shinkaryuk.passbook;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements OnNavigationItemSelectedListener, SearchView.OnCloseListener, LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener, swipeListView.SwipeListViewCallback {//ListActivity {
    //Cursor passCursor;
    private mySimpleCursorAdapter passCursorAdapter;
    public DatabaseHelper passDB;
    private ListView lView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public final static int RESULT_EDIT_OK = 1;
    public final static int RESULT_EDIT_CANCELED = -1;
    public final static int RESULT_EDIT_DELETE = -2;
    public final static int PASS_NEW = 0;
    public final static int PASS_EDIT = 1;
//    private String strFilter = "";
    Boolean isDialogMode = false; //данная переменная нужня для того чтобы не закрывать данное окно при вызове редактирования или настроек

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Создаем верхний тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //создаем базу - класс описан ниже передаем контекст приложения this.getApplicationContext(), чтобы достать глобальную переменную
        passDB = new DatabaseHelper(this.getApplicationContext());

        lView = (ListView) findViewById(R.id.passlview);
        //lView.setAdapter(passCursorAdapter);
        swipeListView l = new swipeListView(this, this);
        l.exec();

        //устанавливаем обновление данных для свайпа вниз по листу
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCursor();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        String[] from = new String[]{passDB.colFavorite, passDB.colPassIsCrypt, passDB.colName};
        int[] to = new int[]{R.id.colFavImg, R.id.colIsCryptoImg, R.id.colNameText};

        passCursorAdapter = new mySimpleCursorAdapter(this.getApplicationContext(), R.layout.item, null/*passCursor*/, from, to, 1, "pass", this);

        lView.setAdapter(passCursorAdapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //getSupportLoaderManager().initLoader(0, null/*savedInstanceState*/, this);//(0, null, this);
        getSupportLoaderManager().initLoader(0, savedInstanceState, this);//(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String language = Integer.toString(resultCode);
        int a_id;

        if (resultCode == RESULT_EDIT_OK) {
            a_id = data.getExtras().getInt("idPassNew");
            String aName = data.getExtras().getString("namePassNew");
            String aLogin = data.getExtras().getString("loginPassNew");
            String aPass = data.getExtras().getString("passPassNew");
            String aComment = data.getExtras().getString("commentPassNew");
            String aDateCreate = data.getExtras().getString("dateCreatePassNew");
            String aDateChange = data.getExtras().getString("dateChangePassNew");
            int isCrypt = data.getExtras().getInt("isCryptoPass");

            if (requestCode == PASS_NEW) {

                passDB.insertEditPass(0, aName, aLogin, aPass, aComment, "0", aDateCreate, aDateChange, Integer.toString(isCrypt));
            }
            if (requestCode == PASS_EDIT) {
                passDB.insertEditPass(a_id, aName, aLogin, aPass, aComment, "", aDateCreate, aDateChange, Integer.toString(isCrypt));

            }
            refreshCursor();
        } else {
            if (resultCode == RESULT_EDIT_DELETE) {
                a_id = data.getExtras().getInt("idPassNew");
                passDB.insertEditPass(a_id * (-1), "", "", "", "", "", "", "", "");

                refreshCursor();
            }
        }
        //Toast.makeText(MainActivity.this, language, Toast.LENGTH_LONG).show();
        isDialogMode = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);


// Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

//не работает анимация
        ((LinearLayout) searchView).setLayoutTransition(new LayoutTransition());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            ShowDlgSettings();
            return true;
        }

        //обрабатываем нажатие пункта меня Создать
        if (id == R.id.action_add){
            showDlgAddPass();
            return true;
        }*/

        if (id == R.id.action_favorites) {
            MenuItem miFav = item;
            Boolean isCheck = miFav.isChecked();
            miFav.setChecked(!isCheck);
            if (miFav.isChecked()) {
                //isFavorite = 1;
                ((passApp)getApplication()).setShowFavorites(1);
                miFav.setIcon(android.R.drawable.btn_star_big_on);
                //passCursor = passDB.getAllPassFav(typeSort, isFavorite);
                //passCursorAdapter.swapCursor(passCursor);
            } else {
                //isFavorite = 0;
                ((passApp)getApplication()).setShowFavorites(0);
                miFav.setIcon(android.R.drawable.btn_star_big_off);
                //passCursor = passDB.getAllPassFav(typeSort, isFavorite);
                //passCursorAdapter.swapCursor(passCursor);
            }
            refreshCursor();
            return true;
        } else if (id == R.id.action_refresh)
        {
            refreshCursor();
        }
        return super.onOptionsItemSelected(item);
    }

    //Вызов диалога по созданию новой записи
    public void showDlgAddPass() {
        Intent intent = new Intent(MainActivity.this, addEditPass.class);

        intent.putExtra("idPass", "-1");
        intent.putExtra("namePass", "");
        intent.putExtra("loginPass", "");
        intent.putExtra("passPass", "");
        intent.putExtra("commentPass", "");
        intent.putExtra("dateCreatePass", "");
        intent.putExtra("dateChangePass", "");

        isDialogMode = true;
        startActivityForResult(intent, PASS_NEW);
    }

    //Нажатие на fabAddPass
    public void onClickShowDlgAddPass(View v) {
        showDlgAddPass();
    }

    public void ShowDlgSettings() {
        Intent intent = new Intent(MainActivity.this, settings.class);
        startActivity(intent);
        isDialogMode = true;
        refreshCursor();
    }

    public void ShowInfo() {
        Intent intent = new Intent(MainActivity.this, activity_information.class);
        startActivity(intent);
        isDialogMode = true;
        refreshCursor();
    }

    public void ShowRecycler() {
        Intent intent = new Intent(MainActivity.this, RecyclerViewActivity.class);
        startActivity(intent);
        isDialogMode = true;
        refreshCursor();
    }

    public void ShowAbout() {
        new AlertDialog.Builder(this) .setMessage(R.string.about_message) .setPositiveButton(android.R.string.ok, null) .show();
    }


    //Обработчик для бокового меню
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pass) {

        } else if (id == R.id.nav_image) {
            Intent intent = new Intent(MainActivity.this, imagesActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_manage) {
            ShowDlgSettings();

        } else if (id == R.id.naw_notes) {
            Intent intent = new Intent(MainActivity.this, notesActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_info) {
            ShowInfo();

        } else if (id == R.id.nav_about) {
            ShowAbout();

        } else if (id == R.id.nav_recycler) {
            ShowRecycler();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);

        refreshCursor();

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((passApp)getApplicationContext()).setSearchStr("");
        passDB.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        ((passApp)getApplicationContext()).setSearchStr("");
        refreshCursor();
    }

    private void refreshCursor(){
        //getSupportLoaderManager().getLoader(0).forceLoad();
        getSupportLoaderManager().restartLoader(0, null, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isDialogMode)
            this.finish();
        ((passApp)getApplicationContext()).setSearchStr("");
    }

    public void onClickcbFav(/*@NonNull */View v) {
        //ListView lView = (ListView) findViewById(R.id.passlview);
/*        int aInt = Integer.parseInt(v.getTag().toString());
        passCursorAdapter.getCursor().moveToPosition(aInt);
        //passCursor.mo
        if (passCursorAdapter.getCursor().getInt(5) == 0) {
            passDB.updateFavoritePass(passCursorAdapter.getCursor().getInt(0), 1);
        } else {
            passDB.updateFavoritePass(passCursorAdapter.getCursor().getInt(0), 0);
        }
        //passCursorAdapter.getCursor().moveToFirst();
        refreshCursor();*/
    }

    @Override
    public boolean onClose() {

        ((passApp)getApplicationContext()).setSearchStr("");

        refreshCursor();
        return false;
    }

    static class MyCursorLoader extends CursorLoader {
        DatabaseHelper db;
        Context context;// String searchText = "";
        int favorite;

        public MyCursorLoader(Context context, DatabaseHelper db, int isFav){//}, String filterString) {
            super(context);
            this.db = db;
            favorite = isFav;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cur = db.getAllPassFav(favorite);
            try {
                TimeUnit.SECONDS.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //imgCursor = cursor;
            return cur;
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, passDB, ((passApp)getApplication()).getShowFavorites());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        passCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        passCursorAdapter.swapCursor(null);


    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(newText);

        refreshCursor();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(query);

        refreshCursor();
        return true;
    }

    //обновляем данные при свайпе по экрану вниз !!!Но он почему-то не срабатывает. Работает метод в onCreate выше
    @Override
    public void onRefresh() {
        refreshCursor();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showEditForm(Integer pos){
        Intent intent = new Intent(MainActivity.this, addEditPass.class);
        passCursorAdapter.getCursor().moveToPosition(pos);

        if (passCursorAdapter.getCursor().getInt(8) == 1) {
            intent.putExtra("idPass", passCursorAdapter.getCursor().getInt(0));
            intent.putExtra("namePass", passDB.EncodeDecodeStr(passCursorAdapter.getCursor().getString(1), passDB.CRYPTO_DECODE));
            intent.putExtra("loginPass", passDB.EncodeDecodeStr(passCursorAdapter.getCursor().getString(2), passDB.CRYPTO_DECODE));
            intent.putExtra("passPass", passDB.EncodeDecodeStr(passCursorAdapter.getCursor().getString(3), passDB.CRYPTO_DECODE));
            intent.putExtra("commentPass", passDB.EncodeDecodeStr(passCursorAdapter.getCursor().getString(4), passDB.CRYPTO_DECODE));
            intent.putExtra("dateCreatePass", passCursorAdapter.getCursor().getString(6));//DateCreate
            intent.putExtra("dateChangePass", passCursorAdapter.getCursor().getString(7));//DateChange
            intent.putExtra("isCryptoPass", passCursorAdapter.getCursor().getInt(8));//isCrypto
        } else {
            intent.putExtra("idPass", passCursorAdapter.getCursor().getInt(0));
            intent.putExtra("namePass", passCursorAdapter.getCursor().getString(1));
            intent.putExtra("loginPass", passCursorAdapter.getCursor().getString(2));
            intent.putExtra("passPass", passCursorAdapter.getCursor().getString(3));
            intent.putExtra("commentPass", passCursorAdapter.getCursor().getString(4));
            intent.putExtra("dateCreatePass", passCursorAdapter.getCursor().getString(6));//DateCreate
            intent.putExtra("dateChangePass", passCursorAdapter.getCursor().getString(7));//DateChange
            intent.putExtra("isCryptoPass", passCursorAdapter.getCursor().getInt(8));//isCrypto
        }

        isDialogMode = true;


        startActivityForResult(intent, PASS_EDIT);
    }

    //Все что ниже было добавлено для работы свайпа
    @Override
    public ListView getListView() {
        return lView;
    }

    @Override
    public void onSwipeItem(boolean isRight, int position) {

        passCursorAdapter.onSwipeItem(isRight, position);
    }

    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
        showEditForm(position);

    }

}