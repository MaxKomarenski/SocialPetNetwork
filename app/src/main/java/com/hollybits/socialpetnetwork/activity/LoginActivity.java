package com.hollybits.socialpetnetwork.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hollybits.socialpetnetwork.Fragments.Map;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.forms.UpdateTokenForm;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.network.ServerRequests;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */

    private final static String  DATE = "lastLoginDate";
    private static final long MILISECOND_IN_9_DAYS = 777_600_000;
    private Credentials credentials;

    @BindView(R.id.email_input_login)
    EditText emailInput;

    @BindView(R.id.password_input_login)
    EditText passwordInput;

    @BindView(R.id.login_button)
    ImageButton loginButton;

    @BindView(R.id.emailText)
    TextView emailTextView;

    @BindView(R.id.passwordText)
    TextView passwordView;

    @BindView(R.id.thank_text_1)
    TextView thankText1;

    @BindView(R.id.thank_text_2)
    TextView thankText2;

    public static class Credentials{
        public String email;
        public String password;
    }

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkLogin();

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        attachListeners();
        credentials = new Credentials();

        Typeface mainFont = Typeface.createFromAsset(this.getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        passwordView.setTypeface(mainFont);
        emailTextView.setTypeface(mainFont);
        thankText1.setTypeface(mainFont);
        thankText2.setTypeface(mainFont);

    }

    private void attachListeners(){

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Authenticating...");
                progressDialog.show();
                credentials.email = emailInput.getText().toString();
                credentials.password = passwordInput.getText().toString();
                login(credentials, false);
            }


        });
    }


    private boolean login(final Credentials credentials, final boolean isReLogin){

        MainActivity.getServerRequests().login(credentials).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    String code = response.headers().get("authorization");

                    System.err.println(code);

                    try {

                        if(!code.equals("Login Failed")){
                            if(!isReLogin) {
                                User user = new User();
                                user.setAuthorizationCode(code);
                                user.setCredentials(credentials);
                                Long id = Long.decode(response.headers().get("id"));
                                user.setId(id);
                                loadUserInfo(user);
                            }else {
                                User user = Paper.book().read(MainActivity.CURRENTUSER);
                                user.setAuthorizationCode(code);
                                Paper.book().write(MainActivity.CURRENTUSER, user);
                            }

                        }

                        dismissLoadingDialog(3000);
                        Log.d("LOGIN", "OK");

                    }catch (NullPointerException e){
                        dismissLoadingDialog(1000);
                        showErrorMessage();
                    }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                Log.d("LOGIN EXEPTION", t.getMessage());
            }
        });
        return true;
    }

    private void dismissLoadingDialog(int time){
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, time);
    }

    private void showErrorMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Error")
                .setMessage("Some issues with login. Please, try again.")
                .setCancelable(false)
                .setNegativeButton("OK, I'll try again",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadUserInfo(User user){

        //TODO load all info from server
        Paper.book().write(MainActivity.CURRENTUSER, user);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Log.d("loadUserInfo func: ",time.toString());
        Paper.book().write(DATE, time);
        Log.d("loadUserInfo ","Moving to profile");
        moveToProfile();
    }

    private void checkLogin(){
        User current = Paper.book().read(MainActivity.CURRENTUSER);
        if(current != null){
            Log.d("checkLogin ","Current is not null");
            Timestamp lastLoginDate = Paper.book().read(DATE);
            Timestamp currentDate = new Timestamp(System.currentTimeMillis());
            if(currentDate.getNanos() - lastLoginDate.getNanos() > MILISECOND_IN_9_DAYS){
                login(current.getCredentials(), true);
            }else {
                Log.d("checkLogin ","Moving to profile");
                moveToProfile();
            }
        }else {
            Log.d("checkLogin ", "Current is null waiting for auth");
        }
    }


    private  void moveToProfile(){
        getFCMToken();
        Intent intent = new Intent(LoginActivity.this, FragmentDispatcher.class);
        startActivity(intent);
    }


    private void getFCMToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }
                        User user = Paper.book().read(MainActivity.CURRENTUSER);

                        UpdateTokenForm form = new UpdateTokenForm();

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        form.setToken(Base64.encodeToString(token.getBytes(), Base64.NO_WRAP));
                        form.setId(Base64.encodeToString(user.getId().toString().getBytes(), Base64.NO_WRAP));


                        java.util.Map<String, String> heades = new HashMap<>();
                        heades.put("Authorization", user.getAuthorizationCode());

                        MainActivity.getServerRequests().updateToken(heades, form).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                                Log.d("TOKEN UPDATED", response.body());
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d("TOKEN UPDATE fail", t.getMessage());
                            }
                        });


                        // Log and toast

                        Log.d("TOKEN", token);
                    }
                });
    }

}

