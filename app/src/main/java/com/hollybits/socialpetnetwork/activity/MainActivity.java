package com.hollybits.socialpetnetwork.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.network.ServerRequests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private static MainActivity instance;


    private final static String  DATE = "lastLoginDate";
    private static final long MILISECOND_IN_9_DAYS = 777_600_000;

    public static final String CURRENT_PET = "currentPet";
    public static final String CURRENTUSER ="currentUser";
    public static final String CURRENT_CHOICE = "CurrentChoice";
    public static final String FRIEND_LIST = "FriendList";
    public static final String FRIENDSHIP_REQUEST_LIST = "FriendshipRequestList";
    public static final String ID_OF_FRIEND = "FriendId";
    public static final String CONTACT_LIST = "ContactList";
    public static final String MESSAGE_BOOK = "Messages";
    public static final String NAME_OF_FRIEND = "NameOfFriend";
    public static final String GALLERY_MODE = "galleryMode";
    public static final String WANTED = "wanted";
    public static String PACKAGE_NAME;


    public MainActivity(){
        instance = this;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PACKAGE_NAME = getPackageName();

        checkLogin();





        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }









    private void checkLogin(){
        User current = Paper.book().read(MainActivity.CURRENTUSER);
        if(current != null){
            Log.d("checkLogin ","Current is not null");
            Timestamp lastLoginDate = Paper.book().read(DATE);
            Timestamp currentDate = new Timestamp(System.currentTimeMillis());
            if(currentDate.getNanos() - lastLoginDate.getNanos() > MILISECOND_IN_9_DAYS){
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }else {
                Log.d("checkLogin ","Moving to profile");
                moveToProfile();
            }
        }else {
            Log.d("checkLogin ", "Current is null waiting for auth");
        }
    }


    private  void moveToProfile(){
        Intent intent = new Intent(MainActivity.this, FragmentDispatcher.class);
        startActivity(intent);
    }




    public static ServerRequests getServerRequests() {
        return MainApplication.getServerRequests();
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
