package com.hollybits.socialpetnetwork.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Victor on 20.11.2018.
 */

public class MainApplication extends Application {


    private static Retrofit retrofit;
    private static ServerRequests serverRequests;


    @Override
    public void onCreate() {
        Paper.init(this);
        if(retrofitInit()){
            Log.d("RETROFIT INIT", "OK");
        } else {
            //TODO Error!!!!!!!!!!!!!
            Log.d("RETROFIT INIT", "FAIL");
        }
        super.onCreate();
    }


    private boolean retrofitInit(){
        Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss")
                .setLenient()
                .create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(ServerRequests.CURRENT_ENDPOINT)
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


}
