package com.hollybits.socialpetnetwork.network;

import com.android.volley.toolbox.StringRequest;
import com.hollybits.socialpetnetwork.activity.LoginActivity;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.forms.RegistrationForm;
import com.hollybits.socialpetnetwork.models.Breed;
import com.hollybits.socialpetnetwork.models.Country;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Victor on 24.07.2018.
 */

public interface ServerRequests {

    String BASE_REMOTE_OUR = "http://206.189.61.135:8080/";
    String BASE_LOCAL = "http://10.0.2.2:8080/";
    String CURRENT_ENDPIONT = BASE_LOCAL;


    @GET("/api/getBreedsForType")
    Call<List<Breed>> getBreedsForType(@Query("petType")PetType petType);

    @GET("/api/loadCountryList")
    Call<List<Country>> getAllCountries();

    @POST("/registration")
    Call<String> sendRegistrationFormToTheServer(@Body RegistrationForm registrationForm);



}
