package com.hollybits.socialpetnetwork.network;

import android.location.Address;
import android.util.Log;
import android.widget.Adapter;

import com.android.volley.toolbox.StringRequest;
import com.hollybits.socialpetnetwork.activity.LoginActivity;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.forms.InformationOfUserAndHisPet;
import com.hollybits.socialpetnetwork.forms.RegistrationForm;
import com.hollybits.socialpetnetwork.forms.UpdateTokenForm;
import com.hollybits.socialpetnetwork.helper.Test;
import com.hollybits.socialpetnetwork.models.Breed;
import com.hollybits.socialpetnetwork.models.Comment;
import com.hollybits.socialpetnetwork.models.Contact;
import com.hollybits.socialpetnetwork.models.Coordinates;
import com.hollybits.socialpetnetwork.models.Country;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.LostPet;
import com.hollybits.socialpetnetwork.models.Message;
import com.hollybits.socialpetnetwork.models.SearchForm;
import com.hollybits.socialpetnetwork.models.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Victor on 24.07.2018.
 */

public interface ServerRequests {

    String BASE_REMOTE_OUR = "https://206.189.61.135:8443/";
    String BASE_LOCAL = "https://10.0.2.2:8443/";
    String CURRENT_ENDPOINT = BASE_REMOTE_OUR;

    @GET("/api/getBreedsForType")
    Call<List<Breed>> getBreedsForType(@Query("petType")PetType petType);

    @GET("/api/loadCountryList")
    Call<List<Country>> getAllCountries();

    @POST("/registration")
    Call<String> sendRegistrationFormToTheServer(@Body RegistrationForm registrationForm);

    @POST("/registrationWithPhoto")
    Call<String> sendRegistrationFormToTheServer(@Body RegistrationForm registrationForm, @Part MultipartBody.Part img);


    @POST("/updateUsersToken")
    Call<String> updateToken(@HeaderMap Map<String, String> headers, @Body UpdateTokenForm tokenForm);

    @POST("/getInformationOfAnotherUser")
    Call<UserInfo> getInformationOfAnotherUser(@HeaderMap Map<String, String> headers, @Query("id") Long id);

    @POST("/online")
    Call<String> online(@HeaderMap Map<String, String> headers, @Query("id") Long id, @Query("time") String time);


    @POST("/login")
    Call<Void> login(@Body LoginActivity.Credentials credentials);

    @POST("/getAllInformationAboutUserAndPets")
    Call<InformationOfUserAndHisPet> getAllInformationAboutUserAndPets(@HeaderMap Map<String, String> headers, @Body Long id);

    @POST("/loadMapInfo")
    Call<Map<String, String>> getUserInfoMap(@HeaderMap Map<String, String> headers, @Query("id") Long id);

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
                                @Query("id") Long id,
                                @Query("attitude") byte attitude);

    @POST("/sendMessage")
    Call<String> sendMessage(@HeaderMap Map<String, String> headers,
                             @Body Message message,
                             @Query("id") Long id,
                             @Query("time") Long time);

    @POST("/makeLastMessageRead")
    Call<String> makeLastMessageRead(@HeaderMap Map<String, String> headers,
                                     @Query("friends_id") Long id,
                                     @Query("user_id") Long userID);


    @POST("/getUsersNearMe")
    Call<Map<Long,Coordinates>> getUsersNearMe(@HeaderMap Map<String, String> headers, @Body Address address, @Query("id") Long id);

    @POST("/getAllUnreadMessages")
    Call<List<Message>> getAllUnreadMessages(@HeaderMap Map<String, String> headers,
                                             @Query("user_1") Long user_1,
                                             @Query("user_2") Long user_2);

    @POST("/acceptFriendshipInvitation")
    Call<FriendInfo> acceptFriendshipInvitation(@HeaderMap Map<String, String> headers,
                                            @Query("acceptor") Long acceptorId,
                                            @Query("requester") Long requesterId,
                                            @Query("state") boolean state);


    @POST("/getUnReadMessagesAmount")
    Call<Integer> getUnReadMessagesAmount(@HeaderMap Map<String, String> headers,
                                          @Query("id") Long id);

    @POST("/checkIfUserIsOnline")
    Call<String> getUserLastActiveTime(@HeaderMap Map<String, String> headers,
                                     @Query("id") Long id);

    @POST("/getInfoAboutNewFriend")
    Call<FriendInfo> getInfoAboutNewFriend(@HeaderMap Map<String, String> headers,
                                           @Query("new_friend_id") Long id);

    @POST("/getFriendWhoAreNotInPhoneCache")
    Call<List<FriendInfo>> getFriendWhoAreNotInPhoneCache(@HeaderMap Map<String, String> headers,
                                                          @Query("list_friend_ids") List<Long> listOfIds,
                                                          @Query("user_id") Long id);

    @POST("/getLostDogs")
    Call<List<LostPet>> getAllLostPetsFromUserDistrict(@HeaderMap Map<String, String> headers,
                                                 @Body Address address);



    @POST("/sos")
    Call<Void> sos(@HeaderMap Map<String, String> headers,
                   @Query("id")Long userId,
                   @Query("petId") Long petId,
                   @Body Address address);

    @Multipart
    @POST("/updateMainPhoto")
    Call<Void> updateMainPhoto(@HeaderMap Map<String, String> headers,
                               @Part MultipartBody.Part img,
                               @Query("id") Long id);


    @POST("/getUsersPhoto")
    Call<ResponseBody> getPhoto(@HeaderMap Map<String, String> headers,
                                @Query("id") Long id,
                                @Query("photoId") Long photoId);
    @POST("/getUsersMainPhoto")
    Call<ResponseBody> getMainPhoto(@HeaderMap Map<String, String> headers,
                                @Query("id") Long id);

    @POST("/getUsersPhotoIds")
    Call<List<Long>> getIdsOfUserPhoto(@HeaderMap Map<String, String> headers,
                                       @Query("requesterId") Long id,
                                       @Query("targetId") Long targetId);
    @Multipart
    @POST("/addNewPhoto")
    Call<Long> addNewPhoto(@HeaderMap Map<String, String> headers,
                               @Part MultipartBody.Part img,
                               @Query("id") Long id);


    @POST("/search")
    Call<List<FriendInfo>> search(@HeaderMap Map<String, String> headers, @Body SearchForm searchForm);

    @POST("/userFoundHisPet")
    Call<String> userFoundHisPet(@HeaderMap Map<String, String> headers,
                                 @Query("pet_id") Long petID);

    @POST("/deleteUserFromFriendList")
    Call<String> deleteUserFromFriendList(@HeaderMap Map<String, String> headers,
                                          @Query("userId") Long userId,
                                          @Query("friendId") Long friendId);


    @POST("/newFriendShipRequest")
    Call<String> newFriendshipRequest(@HeaderMap Map<String, String> headers, @Query("from") Long idFrom,
                                      @Query("to") Long idTo);

    @POST("/getListOfFriendIDsWhoDeleteUserFromFriends")
    Call<List<Long>> getListOfFriendIDsWhoDeleteUserFromFriends(@HeaderMap Map<String, String> headers,
                                                                @Query("list_friend_ids") List<Long> listOfIds,
                                                                @Query("user_id") Long id);

    @POST("/addToFriendsWhenOneUserFoundPetOfAnotherUser")
    Call<FriendInfo> addToFriendsWhenOneUserFoundPetOfAnotherUser(@HeaderMap Map<String, String> headers,
                                                                  @Query("user_who_found_pet") Long userWhoFoundPet,
                                                                  @Query("user_who_lost_pet") Long userWhoLostPet);

    @POST("/getNumberOfFriendsOfAnotherUser")
    Call<Integer> getNumberOfFriendsOfAnotherUser(@HeaderMap Map<String, String> headers,
                                                  @Query("user") Long userWhoFoundPet);

    @POST("/getAllCommentOfCurrentPhoto")
    Call<List<Comment>> getAllCommentOfCurrentPhoto(@HeaderMap Map<String, String> headers, @Query("photo_id") Long photoID);

    @POST("/sendNewComment")
    Call<String> sendNewComment (@HeaderMap Map<String, String> headers,
                                 @Query("time") String time,
                                 @Query("user_id_who_left_a_comment") Long id,
                                 @Query("text") String text,
                                 @Query("photoID") Long photoID);
}
