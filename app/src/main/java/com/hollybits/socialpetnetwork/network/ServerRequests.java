package com.hollybits.socialpetnetwork.network;

import android.location.Address;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.hollybits.socialpetnetwork.activity.LoginActivity;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.forms.InformationOfUserAndHisPet;
import com.hollybits.socialpetnetwork.forms.RegistrationForm;
import com.hollybits.socialpetnetwork.forms.UpdateTokenForm;
import com.hollybits.socialpetnetwork.models.Breed;
import com.hollybits.socialpetnetwork.models.Contact;
import com.hollybits.socialpetnetwork.models.Country;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.Message;
import com.hollybits.socialpetnetwork.models.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Victor on 24.07.2018.
 */

public interface ServerRequests {

    String BASE_REMOTE_OUR = "https://206.189.61.135:8443/";
    String BASE_LOCAL = "https://10.0.2.2:8443/";
    String CURRENT_ENDPIONT = BASE_REMOTE_OUR;


    @GET("/api/getBreedsForType")
    Call<List<Breed>> getBreedsForType(@Query("petType")PetType petType);

    @GET("/api/loadCountryList")
    Call<List<Country>> getAllCountries();

    @POST("/registration")
    Call<String> sendRegistrationFormToTheServer(@Body RegistrationForm registrationForm);

    @POST("/updateUsersToken")
    Call<String> updateToken(@HeaderMap Map<String, String> headers, @Body UpdateTokenForm tokenForm);

    @POST("/getInformationOfAnotherUser")
    Call<UserInfo> getInformationOfAnotherUser(@HeaderMap Map<String, String> headers, @Query("id") Long id);

    @POST("/online")
    Call<String> online(@HeaderMap Map<String, String> headers, @Query("id") Long id);

    @POST("/login")
    Call<Void> login(@Body LoginActivity.Credentials credentials);

    @POST("/getAllInformationAboutUserAndPets")
    Call<InformationOfUserAndHisPet> getAllInformationAboutUserAndPets(@HeaderMap Map<String, String> headers, @Body Long id);

    @POST("/getUsersFriends")
    Call<Set<FriendInfo>> getAllUserFriends(@HeaderMap Map<String, String> headers, @Query("id") Long id);

    @POST("/persist")
    Call<String> notifyPersistance(@HeaderMap Map<String, String> headers, @Query("requestId") Long id, @Query("userID")Long userId);

    @POST("/getUnPersistentFriendShipRequests")
    Call<List<InfoAboutUserFriendShipRequest>> getUnPersistentFriendShipRequests(@HeaderMap Map<String, String> headers, @Query("id") Long id);

    @POST("/allFriendshipRequestsIsDeletedFromCache")
    Call<String> allFriendshipRequestsIsDeletedFromCache(@HeaderMap Map<String, String> headers, @Query("id") Long id);

    @POST("/getAllContactsOfCurrentUser")
    Call<List<Contact>> getAllContactsOfCurrentUser(@HeaderMap Map<String, String> headers, @Query("id") Long id);

    @POST("/getAllMessages")
    Call<List<Message>> getAllMessagesWithCurrentFriend(@HeaderMap Map<String, String> headers,
                                                        @Query("user_id") Long userId,
                                                        @Query("friend_id") Long friendId);

    @POST("/updateUserPosition")
    Call<Void> updateMyPosition(@HeaderMap Map<String, String> headers,
                                @Body Address address,
                                @Query("id") Long id);

    @POST("/sendMessage")
    Call<String> sendMessage(@HeaderMap Map<String, String> headers,
                             @Body Message message,
                             @Query("id") Long id,
                             @Query("time") Long time);
}
