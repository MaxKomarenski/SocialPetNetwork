package com.hollybits.socialpetnetwork.helper;

import android.util.Log;

import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Victor on 14.08.2018.
 */

public class OnlineHandler implements Runnable {
    @Override
    public void run() {
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        try {
            Response<String> r = MainActivity.getServerRequests().online(authorisationCode, currentUser.getId()).execute();
            Log.d("OnlineHandler", String.valueOf(r.code()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
