package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
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
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.network.ServerRequests;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkLogin();
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
                credentials.email = emailInput.getText().toString();
                credentials.password = passwordInput.getText().toString();
                login(credentials, false);
            }


        });
    }


    private boolean login(final Credentials credentials, final boolean isReLogin){

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = ServerRequests.CURRENT_ENDPIONT+"/login";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", credentials.email);
            jsonBody.put("password", credentials.password);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        String code = response.headers.get("authorization");
                        System.err.println(code);
                        if(!code.equals("Login Failed")){
                            if(!isReLogin) {
                                User user = new User();
                                user.setAuthorizationCode(code);
                                user.setCredentials(credentials);
                                Long id = Long.decode(response.headers.get("id"));
                                user.setId(id);
                                loadUserInfo(user);
                            }else {
                                User user = Paper.book().read(MainActivity.CURRENTUSER);
                                user.setAuthorizationCode(code);
                                Paper.book().write(MainActivity.CURRENTUSER, user);
                            }
                        }
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
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
        Intent intent = new Intent(LoginActivity.this, FragmentDispatcher.class);
        startActivity(intent);
    }

}

