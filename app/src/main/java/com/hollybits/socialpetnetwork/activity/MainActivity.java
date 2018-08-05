package com.hollybits.socialpetnetwork.activity;

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
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.network.ServerRequests;

import java.sql.Timestamp;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private static MainActivity instance;
    private static Retrofit retrofit;
    private static ServerRequests serverRequests;

    private final static String  DATE = "lastLoginDate";
    private static final long MILISECOND_IN_9_DAYS = 777_600_000;

    public static final String CURRENTUSER ="currentUser";

    @BindView(R.id.move_to_login)
    Button goToLogin;
    @BindView(R.id.move_to_sing_up)
    Button goToRegistration;


    @BindView(R.id.text_welcome)
    TextView welcome;
    @BindView(R.id.text_social_pet_net)
    TextView socialPetNetText;
    @BindView(R.id.text_havent_account)
    TextView haventAccount;
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
        Paper.init(this);

        //TODO delete this
//        for(String s: Paper.book().getAllKeys()){
//            Paper.book().delete(s);
//        }

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(ServerRequests.CURRENT_ENDPIONT) //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        serverRequests = retrofit.create(ServerRequests.class);
        checkLogin();

        Typeface nameFont = Typeface.createFromAsset(this.getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        welcome.setTypeface(nameFont);
        socialPetNetText.setTypeface(nameFont);
        haventAccount.setTypeface(nameFont);
        goToLogin.setTypeface(nameFont);
        goToRegistration.setTypeface(nameFont);


        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity
                        .class);
                startActivity(intent);
            }
        });

        goToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity
                        .class);
                startActivity(intent);
            }
        });

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
