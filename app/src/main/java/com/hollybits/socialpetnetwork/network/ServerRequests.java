package com.hollybits.socialpetnetwork.network;

import com.android.volley.toolbox.StringRequest;
import com.hollybits.socialpetnetwork.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Victor on 24.07.2018.
 */

public interface ServerRequests {

    String BASE_REMOTE_OUR = "http://206.189.61.135:8080/";
    String BASE_LOCAL = "http://10.0.2.2:8080/";
    String CURRENT_ENDPIONT = BASE_LOCAL;





}
