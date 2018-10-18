package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.os.Bundle;
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

import com.hollybits.socialpetnetwork.Fragments.Account;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_dispatcher);
        fragmentManager = getSupportFragmentManager();
        instance  = this;

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                //Catch your exception
                // Without System.exit() this will not work.
                User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
                Map<String, String> authorisationCode = new HashMap<>();
                authorisationCode.put("authorization", currentUser.getAuthorizationCode());
                Exception exception = new Exception();
                StringBuilder stringBuilder = new StringBuilder();
                for (StackTraceElement element:
                     paramThrowable.getStackTrace()) {
                    stringBuilder.append(element.toString());
                    stringBuilder.append("\n");
                }


                exception.setError(stringBuilder.toString());
                exception.setIdFrom(currentUser.getId());
                MainActivity.getServerRequests().recordException(authorisationCode, exception).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
                System.exit(2);
            }
        });



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


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.image_in_header_fragment_dispatcher);


        PhotoManager.loadDirectlyUserMainPhoto(imageView, this);
        messagesMenuItem = navigationView.getMenu().findItem(R.id.nav_messages);
        getUnReadMessagesAmount();
        ImageButton logout = navigationView.getHeaderView(0).findViewById(R.id.logout_imagebutton_in_nav_header);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        loadUserInfo();
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
        if(previoust != null){
            TextView textView = previoust.getActionView().findViewById(R.id.text_in_menu);
            textView.setTextColor(getResources().getColor(R.color.not_active_item));
        }
        previoust = item;
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass;
        TextView textView = item.getActionView().findViewById(R.id.text_in_menu);
        textView.setTextColor(getResources().getColor(R.color.active_item));
        if(options.containsKey(id)){
            fragmentClass = options.get(id);
        }else {
            fragmentClass = Account.class;
        }
        if(fragmentClass == UsersGallery.class){
            Paper.book().write(MainActivity.GALLERY_MODE, GalleryMode.USERS_MODE);
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

        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
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
