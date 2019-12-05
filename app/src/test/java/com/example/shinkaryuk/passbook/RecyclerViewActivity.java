package com.example.shinkaryuk.passbook;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

public class RecyclerViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    public final static int RESULT_EDIT_OK = 1;
    public final static int RESULT_EDIT_CANCELED = -1;
    public final static int RESULT_EDIT_DELETE = -2;
    public final static int PASS_NEW = 0;
    public final static int PASS_EDIT = 1;
    Boolean isDialogMode = false; //данная переменная нужня для того чтобы не закрывать данное окно при вызове редактирования или настроек

    private SimpleItemTouchHelperCallback callback;
    private ItemTouchHelper mItemTouchHelper;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        //Создаем верхний тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_rv);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout_rv);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.rvTestPass);
        // создаем адаптер
        ArrayDataSource adapter = new ArrayDataSource(this);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_favorites) {
            MenuItem miFav = item;
            Boolean isCheck = miFav.isChecked();
            miFav.setChecked(!isCheck);
            if (miFav.isChecked()) {
                ((passApp)getApplication()).setShowFavorites(1);
                miFav.setIcon(android.R.drawable.btn_star_big_on);
              } else {
                ((passApp)getApplication()).setShowFavorites(0);
                miFav.setIcon(android.R.drawable.btn_star_big_off);
            }
            //refreshCursor();
            ((ArrayDataSource)recyclerView.getAdapter()).refreshData();

            return true;
        } else if (id == R.id.action_refresh) {
            //refreshCursor();
            ((ArrayDataSource)recyclerView.getAdapter()).refreshData();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pass) {

        } else if (id == R.id.nav_image) {
            Intent intent = new Intent(RecyclerViewActivity.this, imagesActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_manage) {
            ShowDlgSettings();

        } else if (id == R.id.naw_notes) {
            Intent intent = new Intent(RecyclerViewActivity.this, notesActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_info) {
            ShowInfo();

        } else if (id == R.id.nav_about) {
            ShowAbout();

        } else if (id == R.id.nav_recycler) {
            //ShowRecycler();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout_rv);
        drawer.closeDrawer(GravityCompat.START);

        //refreshCursor();

        return true;
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
    public boolean onQueryTextChange(String newText) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(newText);

        //refreshCursor();
        ((ArrayDataSource)recyclerView.getAdapter()).refreshData();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(query);

        //refreshCursor();
        ((ArrayDataSource)recyclerView.getAdapter()).refreshData();
        return true;
    }

    @Override
    public boolean onClose() {

        ((passApp)getApplicationContext()).setSearchStr("");

        //refreshCursor();
        ((ArrayDataSource)recyclerView.getAdapter()).refreshData();
        return false;
    }

    //Нажатие на fabAddPass
    public void onClickShowDlgAddPass(View v) {
        showDlgAddPass();
    }

    //Вызов диалога по созданию новой записи
    public void showDlgAddPass() {
        Intent intent = new Intent(RecyclerViewActivity.this, addEditPass.class);

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

                ((ArrayDataSource)recyclerView.getAdapter()).AddEditRecord(0, aName, aLogin, aPass, aComment, "0", aDateCreate, aDateChange, Integer.toString(isCrypt));
            }
            if (requestCode == PASS_EDIT) {
                ((ArrayDataSource)recyclerView.getAdapter()).AddEditRecord(a_id, aName, aLogin, aPass, aComment, "", aDateCreate, aDateChange, Integer.toString(isCrypt));

            }
            //refreshCursor();
        } else {
            if (resultCode == RESULT_EDIT_DELETE) {
                a_id = data.getExtras().getInt("idPassNew");
                ((ArrayDataSource)recyclerView.getAdapter()).AddEditRecord(a_id * (-1), "", "", "", "", "", "", "", "");

                //refreshCursor();
            }
        }
        //Toast.makeText(MainActivity.this, language, Toast.LENGTH_LONG).show();
        isDialogMode = false;
    }

    public void showEditForm(ArrayDataSource.pass item){
        Intent intent = new Intent(RecyclerViewActivity.this, addEditPass.class);

        intent.putExtra("idPass", item.getId());
        intent.putExtra("namePass", item.getName());
        intent.putExtra("loginPass", item.getLogin());
        intent.putExtra("passPass", item.getPassword());
        intent.putExtra("commentPass", item.getComment());
        intent.putExtra("dateCreatePass", item.getDateCreate());//DateCreate
        intent.putExtra("dateChangePass", item.getDateChange());//DateChange
        intent.putExtra("isCryptoPass", item.getCrypt());//isCrypto


        isDialogMode = true;


        startActivityForResult(intent, PASS_EDIT);
    }

    public void ShowDlgSettings() {
        Intent intent = new Intent(RecyclerViewActivity.this, settings.class);
        startActivity(intent);
        isDialogMode = true;
        //refreshCursor();
        ((ArrayDataSource)recyclerView.getAdapter()).refreshData();
    }

    public void ShowInfo() {
        Intent intent = new Intent(RecyclerViewActivity.this, activity_information.class);
        startActivity(intent);
        isDialogMode = true;
        //refreshCursor();
    }

    public void ShowAbout() {
        new AlertDialog.Builder(this) .setMessage(R.string.about_message) .setPositiveButton(android.R.string.ok, null) .show();
    }

    //Обработчик для бокового меню
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout_rv);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((passApp)getApplicationContext()).setSearchStr("");
        //passDB.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        ((passApp)getApplicationContext()).setSearchStr("");
        ((ArrayDataSource)recyclerView.getAdapter()).refreshData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isDialogMode)
            this.finish();
        ((passApp)getApplicationContext()).setSearchStr("");
    }


}
