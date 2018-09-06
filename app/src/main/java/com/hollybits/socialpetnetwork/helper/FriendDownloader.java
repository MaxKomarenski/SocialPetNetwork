package com.hollybits.socialpetnetwork.helper;

import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.FriendshipRequestAdapter;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendDownloader {

    public static void downloadNewFriendAndAddHimToPaperBook(Long id){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        MainActivity.getServerRequests().getInfoAboutNewFriend(authorisationCode, id).enqueue(new Callback<FriendInfo>() {
            @Override
            public void onResponse(Call<FriendInfo> call, Response<FriendInfo> response) {
                FriendInfo newFriend = response.body();
                List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);
                friends.add(newFriend);
                Paper.book().write(MainActivity.FRIEND_LIST, friends);
                Paper.book().delete(MainActivity.CONTACT_LIST);

            }

            @Override
            public void onFailure(Call<FriendInfo> call, Throwable t) {

            }
        });
    }

    public static void downloadAllFriendWhoAreNotInTheCache(){

        List<Long> listOfFriendIds = new ArrayList<>();
        List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);

        for (FriendInfo info:
             friends) {
            listOfFriendIds.add(info.getId());
        }

        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        MainActivity.getServerRequests().getFriendWhoAreNotInPhoneCache(authorisationCode, listOfFriendIds, currentUser.getId()).enqueue(new Callback<List<FriendInfo>>() {
            @Override
            public void onResponse(Call<List<FriendInfo>> call, Response<List<FriendInfo>> response) {
                List<FriendInfo> newFriends = response.body();
                friends.addAll(newFriends);
                Paper.book().write(MainActivity.FRIEND_LIST, friends);
                Paper.book().delete(MainActivity.CONTACT_LIST);
            }

            @Override
            public void onFailure(Call<List<FriendInfo>> call, Throwable t) {

            }
        });
    }
}
