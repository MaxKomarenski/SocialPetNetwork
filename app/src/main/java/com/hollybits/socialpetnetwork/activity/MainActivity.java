package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollybits.socialpetnetwork.FragmentDispatcher;
import com.hollybits.socialpetnetwork.Fragments.Account;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.network.ServerRequests;

import io.paperdb.Paper;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {



    private static Retrofit retrofit;
    private static ServerRequests serverRequests;

    public static final String CURRENTUSER ="currentUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(ServerRequests.BASE_LOCKAL) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        serverRequests = retrofit.create(ServerRequests.class);
        Intent intent = new Intent(MainActivity.this, FragmentDispatcher.class);
        startActivity(intent);
    }

    public static ServerRequests getServerRequests() {
        return serverRequests;
    }

    public static void setServerRequests(ServerRequests serverRequests) {
        MainActivity.serverRequests = serverRequests;
    }

    public static User getCurrentUser(){
        return Paper.book().read(CURRENTUSER);
    }
    public static void saveCurrentUser(User user){
        Paper.book().write(CURRENTUSER,user);
    }
    public static void deleteCurrentUser(){
        Paper.book().delete(CURRENTUSER);
    }
}