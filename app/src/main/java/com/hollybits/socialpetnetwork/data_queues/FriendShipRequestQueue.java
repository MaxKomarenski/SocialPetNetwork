package com.hollybits.socialpetnetwork.data_queues;

import android.util.Log;

import com.hollybits.socialpetnetwork.Fragments.UserFriends;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.FriendshipRequestObservable;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.User;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Victor on 15.08.2018.
 */

public class FriendShipRequestQueue implements FriendshipRequestObservable {

    private static volatile FriendShipRequestQueue instance;


    private static Queue<InfoAboutUserFriendShipRequest> requests;


    private FriendShipRequestQueue(){
        requests = new ArrayDeque<>();
    }

    public static FriendShipRequestQueue getInstance() {
        FriendShipRequestQueue localInstance = instance;
        if (localInstance == null) {
            synchronized (FriendShipRequestQueue.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new FriendShipRequestQueue();
                }
            }
        }
        return localInstance;
    }



    public void add(InfoAboutUserFriendShipRequest elem){
        Log.d("FriendShipRequestQueue", "ADDING ELEM");
        requests.add(elem);
        notifyObservers();
        Thread thread = new Thread(() -> {
            List<InfoAboutUserFriendShipRequest> list = Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST);
            list.add(elem);
            Paper.book().write(MainActivity.FRIENDSHIP_REQUEST_LIST, list);
        });
        thread.start();
        notifyPersistance(elem);
    }

    private void notifyPersistance(InfoAboutUserFriendShipRequest elem){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().notifyPersistance(authorisationCode, elem.getRequestId(), currentUser.getId() ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("notifyPersistance", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public int size(){
        return requests.size();
    }

    public boolean isEmpty(){
        return requests.isEmpty();
    }


    public InfoAboutUserFriendShipRequest poll(){
        return requests.poll();
    }


}
