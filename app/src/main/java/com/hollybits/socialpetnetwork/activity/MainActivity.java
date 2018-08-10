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
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.network.ServerRequests;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;

import javax.net.ssl.HttpsURLConnection;
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
    private static Retrofit retrofit;
    private static ServerRequests serverRequests;

    private final static String  DATE = "lastLoginDate";
    private static final long MILISECOND_IN_9_DAYS = 777_600_000;

    public static final String CURRENT_PET = "currentPet";
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


        if(retrofitInit()){
            Log.d("RETROFIT INIT", "OK");
            checkLogin();
        }
        else {
            //TODO Error!!!!!!!!!!!!!
            Log.d("RETROFIT INIT", "FAIL");
        }



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

    private boolean retrofitInit(){
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setLenient()
                .create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ServerRequests.CURRENT_ENDPIONT)
                .addConverterFactory(GsonConverterFactory.create(gson));

        OkHttpClient okHttp;
        try {
            okHttp = new OkHttpClient.Builder()
                    .sslSocketFactory(getSSLConfig(this).getSocketFactory()).
                            hostnameVerifier((host, session)-> true)
                    .build();
        } catch (CertificateException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (KeyManagementException e) {
            e.printStackTrace();
            return false;
        }

        retrofit = builder.client(okHttp)//Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        serverRequests = retrofit.create(ServerRequests.class);

        return true;
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

    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        try (InputStream cert = context.getResources().openRawResource(R.raw.social_pet_net_sertificate)) {
            ca = cf.generateCertificate(cert);
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
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
