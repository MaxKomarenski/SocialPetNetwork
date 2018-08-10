package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.Fragments.Account;
import com.hollybits.socialpetnetwork.Fragments.StartingMenu;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.LoginActivity;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class FragmentDispatcher extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Map<Integer, Class> options;
    private static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_menu_dispatcher);
        fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_fragment_dispatcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar!=null)
            getSupportActionBar().hide();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        launchFragment(StartingMenu.class);
        setTitle("Account");

    }

    @Override
    public void onBackPressed() {
        launchFragment(Account.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fragment_dispatcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass;
        TextView textView = item.getActionView().findViewById(R.id.text_in_menu);
        textView.setTextColor(getResources().getColor(R.color.active_item));
        if(options.containsKey(id)){
            fragmentClass = options.get(id);
        } else if (!options.containsKey(id)){
            logOut();
            return false;
        }else {
            fragmentClass = Account.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();
        item.setCheckable(true);
        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut(){
        for(String s: Paper.book().getAllKeys()){
            Paper.book().delete(s);
        }
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void init(){
        options = new HashMap<>();

        options.put(R.id.nav_account, Account.class);
        options.put(R.id.nav_map, com.hollybits.socialpetnetwork.Fragments.Map.class);
    }

    public static boolean launchFragment(Class fragmentClass){

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        return true;
    }
}
