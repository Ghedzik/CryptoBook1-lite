package com.shinkaryuk.passbook;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    public final static int RESULT_EDIT_OK = 1;
    public final static int RESULT_EDIT_CANCELED = -1;
    public final static int RESULT_EDIT_DELETE = -2;
    public final static int PASS_NEW = 0;
    public final static int PASS_EDIT = 1;
    public final static int CB_ACTIVITY_SETTING = 100; //для запуска формы Настройки
    Boolean isDialogMode = false; //данная переменная нужна для того чтобы не закрывать данное окно при вызове редактирования или настроек

    private SimpleItemTouchHelperCallback callback;
    private ItemTouchHelper mItemTouchHelper;
    RecyclerView recyclerView;
    Boolean isShowMiniFabs = false;
    SearchManager searchManager;
    SearchView searchView;
    MenuItem searchMenu;

    RegUtils mRegUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String showAndCreateNew = "";

        mRegUtils = new RegUtils(this);

        //Создаем верхний тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_rv);
        toolbar.setTitle(getResources().getString(R.string.title_activity_pass));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.rvTestPass);
        // создаем адаптер
        ArrayDataSourcePass adapter = new ArrayDataSourcePass(this, recyclerView);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        callback = new SimpleItemTouchHelperCallback(adapter, this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("showAndNew") != null) {
                showAndCreateNew = getIntent().getExtras().getString("showAndNew");
            }
            if (showAndCreateNew.equals("1")) {
                FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
                isShowMiniFabs = true;
                onClickMiniFAB(fab1);
            }
        }
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
            ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();

            return true;
        } else if (id == R.id.action_refresh) {
            //refreshCursor();
            ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pass) {

        } else if (id == R.id.nav_manage) {
            ShowDlgSettings();

        } else if (id == R.id.nav_info) {
            ShowInfo();

        } else if (id == R.id.nav_about) {
            ShowAbout();
        }

        else if (id == R.id.menuTest) {
            ShowTestActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
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

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        searchMenu = menu.add("searchMenu").setVisible(false).setActionView(searchView);
        searchMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

//не работает анимация
        ((LinearLayout) searchView).setLayoutTransition(new LayoutTransition());

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(newText);

        //refreshCursor();
        ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(query);

        //refreshCursor();
        ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
        return true;
    }

    @Override
    public boolean onClose() {

        ((passApp)getApplicationContext()).setSearchStr("");

        //refreshCursor();
        ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
        return false;
    }

    //Нажатие на fabAddPass
    public void onClickShowDlgAddPass(View v) {
        onClickMiniFAB(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String language = Integer.toString(resultCode);
        int a_id;

        if (resultCode == RESULT_EDIT_OK) {
            switch (requestCode) {
                case PASS_NEW:
                    a_id = Integer.parseInt(data.getExtras().getString("idPassNew"));
                    String aName = data.getExtras().getString("namePassNew");
                    String aLogin = data.getExtras().getString("loginPassNew");
                    String aPass = data.getExtras().getString("passPassNew");
                    String aComment = data.getExtras().getString("commentPassNew");
 //                   String aDateCreate = data.getExtras().getString("dateCreatePassNew");
 //                   String aDateChange = data.getExtras().getString("dateChangePassNew");
                    int isCrypt = data.getExtras().getInt("isCryptoPass");

                    ((ArrayDataSourcePass) recyclerView.getAdapter()).AddEditRecord(0, aName, aLogin, aPass, aComment, "0", "", "", Integer.toString(isCrypt));
                    SnackbarHelper.show(this, recyclerView,getResources().getString(R.string.message_item_save1) + "'" + aName + "'"
                            + getResources().getString(R.string.message_item_save2));
                    //showHideMiniFabs();
                    break;
                case PASS_EDIT:
                    a_id = Integer.parseInt(data.getExtras().getString("idPassNew"));
                    String eName = data.getExtras().getString("namePassNew");
                    String eLogin = data.getExtras().getString("loginPassNew");
                    String ePass = data.getExtras().getString("passPassNew");
                    String eComment = data.getExtras().getString("commentPassNew");
 //                   String eDateCreate = data.getExtras().getString("dateCreatePassNew");
 //                   String eDateChange = data.getExtras().getString("dateChangePassNew");
                    int isCryptE = data.getExtras().getInt("isCryptoPass");

                    ((ArrayDataSourcePass)recyclerView.getAdapter()).AddEditRecord(a_id, eName, eLogin, ePass, eComment, "", "", "", Integer.toString(isCryptE));
                    SnackbarHelper.show(this, recyclerView,getResources().getString(R.string.message_item_save1) + "'" + eName + "'"
                            + getResources().getString(R.string.message_item_save2));
                    break;
            }
        } else if (resultCode == RESULT_EDIT_DELETE) {
            a_id = Integer.parseInt(data.getExtras().getString("idPassNew"));
            ((ArrayDataSourcePass)recyclerView.getAdapter()).AddEditRecord(a_id * (-1), "", "", "", "", "", "", "", "");
        }

        ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
        //Toast.makeText(MainActivity.this, language, Toast.LENGTH_LONG).show();
        isDialogMode = false;
    }

    public void showEditForm(ArrayDataSourcePass.pass item){
        Intent intent = new Intent(MainActivity.this, addEditPass.class);

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
        Intent intent = new Intent(MainActivity.this, settings.class);
        isDialogMode = true;

        startActivityForResult(intent, CB_ACTIVITY_SETTING);
        //refreshCursor();
//        ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
    }

    public void ShowInfo() {
        Intent intent = new Intent(MainActivity.this, activity_information.class);
        startActivity(intent);
        isDialogMode = true;
        //refreshCursor();
    }

    public void ShowAbout() {
        SnackbarHelper.showAbout(this);
        isDialogMode = true;
    }

    //Обработчик для бокового меню
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified()) {
            searchView.clearFocus();
            searchManager.stopSearch();
            searchMenu.collapseActionView();
            onClose();
            searchView.setIconified(true);
            //searchMenu.

            //searchManager.getGlobalSearchActivity().
            //searchView.setVisibility(View.GONE);
        } else if (((ArrayDataSourcePass)recyclerView.getAdapter()).isEditing()) {
            ((ArrayDataSourcePass)recyclerView.getAdapter()).allPassToNoEdit();
            ((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
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
        //((ArrayDataSourcePass)recyclerView.getAdapter()).refreshData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isDialogMode)
            this.finish();
        ((passApp)getApplicationContext()).setSearchStr("");
    }

    public void onClickMiniFAB(View v){
        //isShowMiniFabs = !isShowMiniFabs;

        FloatingActionButton fabBase = (FloatingActionButton) findViewById(R.id.fabAddPass_rv);
        Animation fabBaseAnim = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_main_anim);
        fabBase.startAnimation(fabBaseAnim);

        //Toast.makeText(this, "FAB1", Toast.LENGTH_LONG).show();
        //для лайт версии если паролей больше 7 то ничего не делаем, но предупреждаем
        if (getCountPass() >= 10) {

            //showHideMiniFabs();
            SnackbarHelper.showW(this, recyclerView,getResources().getString(R.string.message_lite_version));
            return;
        }
        if (mRegUtils.getHowEdit() == RegUtils.EDIT_IN_WINDOW) {
            Intent intent = new Intent(MainActivity.this, addEditPass.class);

            intent.putExtra("idPass", "0");
            intent.putExtra("namePass", "");
            intent.putExtra("loginPass", "");
            intent.putExtra("passPass", "");
            intent.putExtra("commentPass", "");
            intent.putExtra("dateCreatePass", "");
            intent.putExtra("dateChangePass", "");
            intent.putExtra("isCryptoPass", "");

            isDialogMode = true;
            startActivityForResult(intent, PASS_NEW);
            //showHideMiniFabs();
        } else {
            ((ArrayDataSourcePass) recyclerView.getAdapter()).addNewEmptyItem(v);
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        }

        //showHideMiniFabs();
    }

    public void ShowTestActivity() {
        //Intent intent = new Intent(MainActivity.this, BottomNavActivity.class);
        //isDialogMode = false;
        //startActivity(intent);
        //finish();
    }

    //для лайт версии определение количества записей в таблице
    public int getCountPass(){
        DatabaseHelper passDB = new DatabaseHelper(this, recyclerView);

        Cursor passCursor = passDB.getAllPass();
        return passCursor.getCount();
    }

}
