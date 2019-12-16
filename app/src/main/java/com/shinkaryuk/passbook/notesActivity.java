package com.shinkaryuk.passbook;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

import static com.shinkaryuk.passbook.MainActivity.PASS_NEW;
import static com.shinkaryuk.passbook.imagesActivity.IMG_NEW;


public class notesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener  {//ListActivity {

    public DatabaseHelper notesDB;
    public final static int RESULT_EDIT_OK = 1;
    public final static int RESULT_EDIT_CANCELED = -1;
    public final static int RESULT_EDIT_DELETE = -2;
    public final static int NOTES_NEW = 20;
    public final static int NOTES_EDIT = 21;
    private String strFilter = "";
    private Boolean isDialogMode = false;

    SimpleItemTouchHelperCallback callback;
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
        toolbar.setTitle(getResources().getString(R.string.title_activity_notes));
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.rvTestPass);
        //создаем базу - класс описан ниже - передаем контекст приложения this.getApplicationContext(), чтобы достать глобальную переменную
        notesDB = new DatabaseHelper(this.getApplicationContext(), recyclerView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // создаем адаптер
        ArrayDataSourceNotes adapter = new ArrayDataSourceNotes(this, recyclerView);
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
                FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
                isShowMiniFabs = true;
                onClickMiniFAB(fab3);
            }
        }
    }

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String language = Integer.toString(resultCode);
        int a_id;

        if (resultCode == RESULT_EDIT_OK){
            switch (requestCode) {
                case PASS_NEW:
                    a_id = Integer.parseInt(data.getExtras().getString("idPassNew"));
                    String aName = data.getExtras().getString("namePassNew");
                    String aLogin = data.getExtras().getString("loginPassNew");
                    String aPass = data.getExtras().getString("passPassNew");
                    String aComment = data.getExtras().getString("commentPassNew");
                    String aDateCreate = data.getExtras().getString("dateCreatePassNew");
                    String aDateChange = data.getExtras().getString("dateChangePassNew");
                    int isCrypt = data.getExtras().getInt("isCryptoPass");

                    notesDB.insertEditPass(a_id, aName, aLogin, aPass, aComment, "0", aDateCreate, aDateChange, Integer.toString(isCrypt));
                    SnackbarHelper.show(this, recyclerView,getResources().getString(R.string.message_item_save1) + "'" + aName + "'"
                            + getResources().getString(R.string.message_item_save2));
                    //((ArrayDataSourcePass) recyclerView.getAdapter()).AddEditRecord(0, aName, aLogin, aPass, aComment, "0", aDateCreate, aDateChange, Integer.toString(isCrypt));
                    //showHideMiniFabs();
                    break;
                case IMG_NEW:
                    a_id = Integer.parseInt(data.getExtras().getString("idImgNew"));
                    String iName = data.getExtras().getString("nameImgNew");
                    String aPath = data.getExtras().getString("pathImgNew");
                    String iDateCreate = data.getExtras().getString("dateCreateImgNew");
                    String iDateChange = data.getExtras().getString("dateChangeImgNew");

                    String aShortPath = "";
                    if (!aPath.equals("")) {
                        aShortPath = Uri.parse(Uri.parse(aPath).getLastPathSegment()).getPath();
                        aShortPath = aShortPath.substring(0, aShortPath.lastIndexOf(".")) + ".jpg";//".bmp";
                    }
                    String iComment = data.getExtras().getString("commentImgNew");
                    //(ArrayDataSourceImg) AddEditRecord
                    String isCryptoImg = data.getExtras().getString("isCryptoNew");
                    notesDB.insertEditImg(0, iName, aPath, iComment, aShortPath, getFilesDir().getPath() + "/s_" + aShortPath, iDateCreate, iDateChange, isCryptoImg);
                    SnackbarHelper.show(this, recyclerView,getResources().getString(R.string.message_item_save1) + "'" + iName + "'"
                            + getResources().getString(R.string.message_item_save2));
                    //showHideMiniFabs();
                    break;
                case NOTES_NEW:
                    a_id = Integer.parseInt(data.getExtras().getString("idNotesNew"));
                    String nName = data.getExtras().getString("notesNoteNew");
                    String nDateCreate = data.getExtras().getString("notesCreateDateNew");
                    String nDateChange = data.getExtras().getString("notesChangeDateNew");
                    String isCrypto = data.getExtras().getString("isCryptoNew");
                    notesDB.insertEditNotes(0, nName, nDateCreate, nDateChange, isCrypto);
                    SnackbarHelper.show(this, recyclerView,getResources().getString(R.string.message_item_save1) + "'" + nName + "'"
                            + getResources().getString(R.string.message_item_save2));
                    //showHideMiniFabs();
                    break;
                case NOTES_EDIT:
                    a_id = Integer.parseInt(data.getExtras().getString("idNotesNew"));
                    String eName = data.getExtras().getString("notesNoteNew");
                    String eDateCreate = data.getExtras().getString("notesCreateDateNew");
                    String eDateChange = data.getExtras().getString("notesChangeDateNew");
                    String isCryptoE = data.getExtras().getString("isCryptoNew");
                    notesDB.insertEditNotes(a_id, eName, eDateCreate, eDateChange, isCryptoE);
                    SnackbarHelper.show(this, recyclerView,getResources().getString(R.string.message_item_save1) + "'" + eName + "'"
                            + getResources().getString(R.string.message_item_save2));
                    break;
            }
        } else if (resultCode == RESULT_EDIT_DELETE){
            a_id = Integer.parseInt(data.getExtras().getString("idNotesNew"));
            notesDB.insertEditNotes(a_id* (-1), "", "", "", "");
        }
        //Toast.makeText(MainActivity.this, language, Toast.LENGTH_LONG).show();
        //refreshCursor();
        ((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();
        isDialogMode = false;
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

        searchMenu = menu.add("searchMenu").setVisible(false).setActionView(searchView);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

//не работает анимация
        ((LinearLayout) searchView).setLayoutTransition(new LayoutTransition());
        searchView.setOnCloseListener(this);

        menu.removeItem(R.id.action_favorites);

        super.onCreateOptionsMenu(menu);

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

        //обрабатываем нажатие пункта меню Создать
        if (id == R.id.action_add){

            showDlgAddNotes();
        }*/

        if (id == R.id.action_refresh)
        {
            ((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();
        }

        return super.onOptionsItemSelected(item);
    }

    //Вызов диалога по созданию новой записи
    public void showDlgAddNotes(){
        showHideMiniFabs();
/*
        Intent intent = new Intent(notesActivity.this, addEditNotes.class);

        intent.putExtra("idNotes", "-1");
        intent.putExtra("notesNote", "");
        intent.putExtra("dateCreateNote", "");
        intent.putExtra("dateChangeNote", "");
        intent.putExtra("isCryptoNote", "");

        isDialogMode = true;
        startActivityForResult(intent, NOTES_NEW);*/
    }



    //Нажатие на fabAddPass
    public void onClickShowDlgAddPass(View v){
        showDlgAddNotes();
    }

    public void ShowDlgSettings(){
        Intent intent = new Intent(notesActivity.this, settings.class);
        startActivity(intent);
        isDialogMode = true;
        //refreshCursor();
        ((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();
    }

    public void ShowInfo(){
        Intent intent = new Intent(notesActivity.this, activity_information.class);
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
            Intent intent = new Intent(notesActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_image) {
            Intent intent = new Intent(notesActivity.this, imagesActivity.class);
            startActivity(intent);
            finish();

        }  else if (id == R.id.nav_manage) {
            ShowDlgSettings();
        } else if (id == R.id.naw_notes) {}

        else if (id == R.id.nav_info) {
            ShowInfo();

        } else if (id == R.id.nav_about) {
            ShowAbout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_layout);
        drawer.closeDrawer(GravityCompat.START);

        //refreshCursor();
        ((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        notesDB.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        //refreshCursor();
        //((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();
    }

    @Override
    public boolean onClose() {
        //refreshCursor();
        ((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isDialogMode) this.finish();
    }

    //Для работы поиска из тулбара
    @Override
    public boolean onQueryTextChange(String newText) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(newText);

        //refreshCursor();
        ((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // this is your adapter that will be filtered
        ((passApp)getApplicationContext()).setSearchStr(query);

        //refreshCursor();
        ((ArrayDataSourceNotes)recyclerView.getAdapter()).refreshData();
        return true;
    }

    public void showEditForm(ArrayDataSourceNotes.note item){
        // получаем элемент, на который нажал пользователь
        //String language = notesCursor.getString(0);

        //Toast.makeText(MainActivity.this, language, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(notesActivity.this, addEditNotes.class);
        //notesCursorAdapter.getCursor().moveToPosition(pos);
        intent.putExtra("idNotes", item.getId());
        intent.putExtra("notesNote", item.getName());
        intent.putExtra("dateCreateNote", item.getDateCreate());
        intent.putExtra("dateChangeNote", item.getDateChange());
        intent.putExtra("isCryptoNote", item.getCrypt());

        isDialogMode = true;
        startActivityForResult(intent, NOTES_EDIT);
    }

    public void onClickMiniFAB(View v){

        switch (v.getId()) {
            case R.id.fab_1:
                //Toast.makeText(this, "FAB1", Toast.LENGTH_LONG).show();
                if (mRegUtils.getHowEdit() == RegUtils.EDIT_IN_WINDOW) {
                    Intent intent = new Intent(notesActivity.this, addEditPass.class);

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
                    Intent intent = new Intent(notesActivity.this, MainActivity.class);
                    intent.putExtra("showAndNew", "1");
                    isDialogMode = false;
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.fab_2:
                //Toast.makeText(this, "FAB2", Toast.LENGTH_LONG).show();
                Intent intentImgDlg = new Intent(this, addEditImage.class);

                intentImgDlg.putExtra("idImg", "0");
                intentImgDlg.putExtra("nameImg", "");
                intentImgDlg.putExtra("pathImg", "");
                intentImgDlg.putExtra("commentImg", "");
                intentImgDlg.putExtra("dateCreateImg", "");
                intentImgDlg.putExtra("dateChangeImg", "");
                intentImgDlg.putExtra("isCryptoImg", "0");

                isDialogMode = true;
                startActivityForResult(intentImgDlg, IMG_NEW);
                //showHideMiniFabs();

                break;
            case R.id.fab_3:
                //Toast.makeText(this, "FAB3", Toast.LENGTH_LONG).show();
                if (mRegUtils.getHowEdit() == RegUtils.EDIT_IN_WINDOW) {
                    Intent intentNoteDlg = new Intent(this, addEditNotes.class);

                    intentNoteDlg.putExtra("idNotes", "0");
                    intentNoteDlg.putExtra("notesNote", "");
                    intentNoteDlg.putExtra("dateCreateNote", "");
                    intentNoteDlg.putExtra("dateChangeNote", "");
                    intentNoteDlg.putExtra("isCryptoNote", "");

                    isDialogMode = true;
                    startActivityForResult(intentNoteDlg, NOTES_NEW);
                    //showHideMiniFabs();
                } else {
                    ((ArrayDataSourceNotes) recyclerView.getAdapter()).addNewEmptyItem(v);
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }

                break;
        }
        showHideMiniFabs();
    }

    private void showHideMiniFabs() {
        isShowMiniFabs = !isShowMiniFabs;

        FloatingActionButton fabBase = (FloatingActionButton) findViewById(R.id.fabAddPass_rv);
        Animation fabBaseAnim = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_main_anim);
        fabBase.startAnimation(fabBaseAnim);
        //fabBase.setBackgroundResource(R.mipmap.ic_minus_black_24dp);
        View.OnClickListener clickFabMini = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onClickMiniFAB(v);
            }
        };

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab1.setOnClickListener(clickFabMini);
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        fab1.setClickable(true);

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab2.setOnClickListener(clickFabMini);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        fab2.setClickable(true);

        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab_3);
        fab3.setOnClickListener(clickFabMini);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        fab3.setClickable(true);

        if (isShowMiniFabs) {
            fabBase.setImageResource(R.mipmap.ic_minus_black_24dp);
            fabBase.setBackgroundColor(getResources().getColor(R.color.сolorFABDown));

            Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
            layoutParams1.rightMargin += (int) (fab1.getWidth() * 1.7);
            layoutParams1.bottomMargin += (int) (fab1.getHeight() * 0.25);
            fab1.setLayoutParams(layoutParams1);
            fab1.startAnimation(show_fab_1);

            Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
            layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
            layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
            fab2.setLayoutParams(layoutParams2);
            fab2.startAnimation(show_fab_2);

            Animation show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
            layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
            layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
            fab3.setLayoutParams(layoutParams3);
            fab3.startAnimation(show_fab_3);
        } else {
            fabBase.setImageResource(R.mipmap.ic_add_black_24dp);
            fabBase.setBackgroundColor(getResources().getColor(R.color.сolorTextBlack));

            Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
            layoutParams1.rightMargin += (int) (fab1.getWidth() * -1.7);
            layoutParams1.bottomMargin += (int) (fab1.getHeight() * -0.25);
            fab1.setLayoutParams(layoutParams1);
            fab1.startAnimation(hide_fab_1);

            Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
            layoutParams2.rightMargin += (int) (fab2.getWidth() * -1.5);
            layoutParams2.bottomMargin += (int) (fab2.getHeight() * -1.5);
            fab2.setLayoutParams(layoutParams2);
            fab2.startAnimation(hide_fab_2);

            Animation hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);
            layoutParams3.rightMargin += (int) (fab3.getWidth() * -0.25);
            layoutParams3.bottomMargin += (int) (fab3.getHeight() * -1.7);
            fab3.setLayoutParams(layoutParams3);
            fab3.startAnimation(hide_fab_3);
        }
    }

}
