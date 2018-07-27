package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

        //TODO delete this
//        for(String s: Paper.book().getAllKeys()){
//            Paper.book().delete(s);
//        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(ServerRequests.BASE_LOCAL) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        serverRequests = retrofit.create(ServerRequests.class);
        Intent intent = new Intent(MainActivity.this, LoginActivity
                .class);
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
