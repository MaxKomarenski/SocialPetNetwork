package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.hollybits.socialpetnetwork.Fragments.Account;
import com.hollybits.socialpetnetwork.Fragments.EditInfo;
import com.hollybits.socialpetnetwork.Fragments.Store;
import com.hollybits.socialpetnetwork.Fragments.UsersGallery;
import com.hollybits.socialpetnetwork.Fragments.Messages;
import com.hollybits.socialpetnetwork.Fragments.StartingMenu;
import com.hollybits.socialpetnetwork.Fragments.UserFriends;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.data_queues.FriendShipRequestQueue;
import com.hollybits.socialpetnetwork.enums.GalleryMode;
import com.hollybits.socialpetnetwork.forms.InformationOfUserAndHisPet;
import com.hollybits.socialpetnetwork.helper.OnlineHandler;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.Exception;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.Pet;
import com.hollybits.socialpetnetwork.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDispatcher extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Map<Integer, Class> options;
    private static FragmentManager fragmentManager;
    private static FragmentDispatcher instance;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private MenuItem previoust;
    private  static MenuItem messagesMenuItem;
    private static  int  counterOfNewMessages;

    private static Class previoustFragment;
    private static Class currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_dispatcher);
        fragmentManager = getSupportFragmentManager();
        instance  = this;


        setContentView(R.layout.activity_fragment_dispatcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(toolbar!=null)
            getSupportActionBar().hide();

        executor.scheduleAtFixedRate(new OnlineHandler(),0, 4 , TimeUnit.MINUTES);
        new Thread(this::init).start();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();




        Fabric.with(this, new Crashlytics());
        Log.d("FABRIC", "FABRIC CRASH REPORT INITIALIZED");


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        messagesMenuItem = navigationView.getMenu().findItem(R.id.nav_messages);
        getUnReadMessagesAmount();

        MenuItem actions  = navigationView.getMenu().getItem(6);
        ImageButton button = actions.getActionView().findViewById(R.id.sign_out_in_actions_nav);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        ImageButton setttings = actions.getActionView().findViewById(R.id.settings_in_actions_nav);
        setttings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentDispatcher.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        ImageView editButton = navigationView.getHeaderView(0).findViewById(R.id.edit_profile_nav_header);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(EditInfo.class);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        loadUserInfo();
        previoustFragment = StartingMenu.class;
        currentFragment = StartingMenu.class;
        launchFragment(StartingMenu.class);
        setTitle("Account");

    }

    public static FragmentDispatcher getInstance() {
        return instance;
    }

    private void prepareFriendShipRequestsList(){
        if(Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST) == null)
            Paper.book().write(MainActivity.FRIENDSHIP_REQUEST_LIST, new ArrayList<>());
    }


    @Override
    public void onBackPressed() {
        Log.d("BACK TO:", previoustFragment.getName());
        launchFragment(previoustFragment);
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
        previoust = item;
        int id = item.getItemId();
        Class fragmentClass;
        if(options.containsKey(id)){
            fragmentClass = options.get(id);
        }else {
            fragmentClass = Account.class;
        }
        if(fragmentClass == UsersGallery.class){
            Paper.book().write(MainActivity.GALLERY_MODE, GalleryMode.USERS_MODE);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        launchFragment(fragmentClass);
        setTitle(item.getTitle());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void incCounter(){
        counterOfNewMessages++;
        instance.runOnUiThread(()->{
            TextView counter  = messagesMenuItem.getActionView().findViewById(R.id.message_counter_i);
            counter.setText(String.valueOf(counterOfNewMessages));
            counter.setVisibility(View.VISIBLE);
        });
    }

    public static void decCounter(int amount){
        counterOfNewMessages = counterOfNewMessages - amount;
        instance.runOnUiThread(()->{
            TextView counter  = messagesMenuItem.getActionView().findViewById(R.id.message_counter_i);
            counter.setText(String.valueOf(counterOfNewMessages));
            if(counterOfNewMessages <= 0){counter.setVisibility(View.GONE); counterOfNewMessages = 0;}
        });
    }

    private void getUnReadMessagesAmount(){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        MainActivity.getServerRequests().getUnReadMessagesAmount(authorisationCode, currentUser.getId()).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                TextView counter  = messagesMenuItem.getActionView().findViewById(R.id.message_counter_i);
                if(response.body()!=null && response.body() > 0) {
                    counter.setText(response.body().toString());
                    counter.setVisibility(View.VISIBLE);
                    counterOfNewMessages = response.body();

                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });


    }

    private void logOut(){
        sayServerThatAllFriendshipRequestIsDeletedFromCache();
        for(String s: Paper.book().getAllKeys()){
            Log.d("DELETING KEY: ", s);
            Paper.book().delete(s);
        }
        for (String s: Paper.book(MainActivity.MESSAGE_BOOK).getAllKeys()){
            Log.d("DELETING KEY: ", s);
            Paper.book(MainActivity.MESSAGE_BOOK).delete(s);
        }
        for (String s: Paper.book(PhotoManager.PAPER_BOOK_NAME).getAllKeys()){
            Log.d("DELETING KEY: ", s);
            Paper.book(PhotoManager.PAPER_BOOK_NAME).delete(s);
        }
        for (String s: Paper.book(PhotoManager.PAPER_BOOK_NAME_FRIENDS).getAllKeys()){
            Log.d("DELETING KEY: ", s);
            Paper.book(PhotoManager.PAPER_BOOK_NAME_FRIENDS).delete(s);
        }
        PhotoManager.destroy();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void loadUserInfo() {
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        Log.d("id", currentUser.getId().toString());

        MainActivity.getServerRequests().getAllInformationAboutUserAndPets(authorisationCode, currentUser.getId()).enqueue(new Callback<InformationOfUserAndHisPet>() {
            @Override
            public void onResponse(Call<InformationOfUserAndHisPet> call, Response<InformationOfUserAndHisPet> response) {
                InformationOfUserAndHisPet info = response.body();
                currentUser.setName(info.getName());
                currentUser.setPhone(info.getPhone());
                currentUser.setSurname(info.getSurname());
                currentUser.setCity(info.getCity());
                currentUser.setPets(info.getPet());
                Paper.book().write(MainActivity.CURRENTUSER, currentUser);
                Paper.book().write(MainActivity.CURRENT_PET, currentUser.getPets().get(0)); //TODO smth

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                TextView name = navigationView.getHeaderView(0).findViewById(R.id.name_surname_nav_header);
                Pet currentPet = Paper.book().read(MainActivity.CURRENT_PET);
                name.setText(currentPet.getName());
            }

            @Override
            public void onFailure(Call<InformationOfUserAndHisPet> call, Throwable t) {


            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("FRAGMENT DISPATCHER", "ON DESTROY");
        executor.shutdown();
    }

    private void init(){
        options = new HashMap<>();
        options.put(R.id.nav_account, Account.class);
        options.put(R.id.nav_map, com.hollybits.socialpetnetwork.Fragments.Map.class);
        options.put(R.id.nav_friends, UserFriends.class);
        options.put(R.id.nav_messages, Messages.class);
        options.put(R.id.nav_gallery, UsersGallery.class);
        options.put(R.id.nav_store, Store.class);
        prepareFriendShipRequestsList();
        getAllUnPersistentFriendShipRequests();
    }

    public static boolean launchFragment(Class fragmentClass){


        previoustFragment = currentFragment;
        currentFragment = fragmentClass;
        Log.d("Launching:", currentFragment.getName());
        Log.d("Previoust Fragment:", previoustFragment.getName());
        Fragment fragment = null;
        try {
            fragment = (Fragment) currentFragment.newInstance();
        }catch (java.lang.Exception e){
            e.printStackTrace();
        }

        fragmentManager.beginTransaction().replace(R.id.frame, fragment).commit();

        return true;
    }

    private void getAllUnPersistentFriendShipRequests(){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().getUnPersistentFriendShipRequests(authorisationCode, currentUser.getId()).enqueue(new Callback<List<InfoAboutUserFriendShipRequest>>() {
            @Override
            public void onResponse(Call<List<InfoAboutUserFriendShipRequest>> call, Response<List<InfoAboutUserFriendShipRequest>> response) {
                List<InfoAboutUserFriendShipRequest> requests = response.body();

                List<InfoAboutUserFriendShipRequest> list = Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST);
                for (InfoAboutUserFriendShipRequest info: requests) {

                    list.add(info);
                    FriendShipRequestQueue.getInstance().notifyPersistance(info);

                }

                Paper.book().write(MainActivity.FRIENDSHIP_REQUEST_LIST, list);
            }

            @Override
            public void onFailure(Call<List<InfoAboutUserFriendShipRequest>> call, Throwable t) {

            }
        });
    }



    private void sayServerThatAllFriendshipRequestIsDeletedFromCache(){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().allFriendshipRequestsIsDeletedFromCache(authorisationCode, currentUser.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("deleted from cache", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


}
