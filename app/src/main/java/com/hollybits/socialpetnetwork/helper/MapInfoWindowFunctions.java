package com.hollybits.socialpetnetwork.helper;

import android.util.Log;

import com.hollybits.socialpetnetwork.Fragments.Chat;
import com.hollybits.socialpetnetwork.Fragments.FriendAccount;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.Message;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Victor on 04.09.2018.
 */

public class MapInfoWindowFunctions {


    private static final String walkWelcome = "Hey! Do you what to go for a walk with me?";



    public static void walkFunction(Long targetId){

        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Long timestamp = System.currentTimeMillis();
        Message message = new Message(walkWelcome, targetId, MainActivity.getCurrentUser().getId(),false);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().sendMessage(authorisationCode,
                message,
                currentUser.getId(),
                timestamp).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("response", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        List<Message> messageList = Paper.book(MainActivity.MESSAGE_BOOK).read(targetId.toString());
        messageList.add(message);
        Paper.book(MainActivity.MESSAGE_BOOK).write(targetId.toString(), messageList);

    }

    public static void infoFunction(Long targetId){

        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().getInformationOfAnotherUser(authorisationCode, targetId).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                Paper.book().write(MainActivity.CURRENT_CHOICE, response.body());
                FragmentDispatcher.launchFragment(FriendAccount.class);

            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });

    }

    public static void chatFunction(Long targetId, String name){

        Paper.book().write(MainActivity.ID_OF_FRIEND, targetId);
        Paper.book().write(MainActivity.NAME_OF_FRIEND, name);
        FragmentDispatcher.launchFragment(Chat.class);


    }





}
