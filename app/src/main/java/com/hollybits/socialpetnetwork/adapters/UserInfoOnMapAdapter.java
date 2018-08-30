package com.hollybits.socialpetnetwork.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.hollybits.socialpetnetwork.Fragments.Map;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.enums.Attitude;
import com.hollybits.socialpetnetwork.helper.Test;
import com.hollybits.socialpetnetwork.models.User;

import java.util.HashMap;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Victor on 29.08.2018.
 */

public class UserInfoOnMapAdapter implements GoogleMap.InfoWindowAdapter {


    private Activity context;
    private java.util.Map<Integer, Pair<Integer, Integer>> possibleColors;
    private User currentUser;
    private java.util.Map<String, String> code;
    private TextView petName ;
    private TextView owner ;
    private TextView petSex ;
    private TextView petAge ;
    private TextView petBreed;

    public UserInfoOnMapAdapter(Activity context) {
        possibleColors = new HashMap<>();
        this.context = context;
        possibleColors.put(Attitude.GOODWITHALL.ordinal(), new Pair<>(R.color.green, R.color.green));
        possibleColors.put(Attitude.GOODWITHMALE.ordinal(), new Pair<>(R.color.yellow, R.drawable.map_info_window_yellow));
        possibleColors.put(Attitude.GOODWITHFEMALE.ordinal(), new Pair<>(R.color.blue, R.drawable.map_info_window_blue));
        possibleColors.put(Attitude.BAD.ordinal(), new Pair<>(R.color.red, R.drawable.map_info_window_red));
        currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        code = new HashMap<>();
        code.put("authorization", currentUser.getAuthorizationCode());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.user_info_on_map, null);
        ConstraintLayout linearLayout = view.findViewById(R.id.user_info_on_map_layout);
        ImageButton info = view.findViewById(R.id.info_button_in_map_info_window);
        Long userId = Long.decode(marker.getTitle());
        boolean state[] = new boolean[1];

        state[0] = false;

        MainActivity.getServerRequests().getUserInfoMap(code,userId).enqueue(new Callback<java.util.Map<String, String>>() {
            @Override
            public void onResponse(Call<java.util.Map<String, String>> call, @NonNull Response<java.util.Map<String, String>> response) {

                java.util.Map<String, String> map = response.body();
                petName = view.findViewById(R.id.pet_name_in_map_info_window);
                 owner = view.findViewById(R.id.owner_name_in_map_info_window);
                 petSex = view.findViewById(R.id.sex_in_map_info_window);
                 petAge = view.findViewById(R.id.age_in_map_info_window);
                 petBreed = view.findViewById(R.id.breed_name_in_map_info_window);

                petName.setText(map.get("petName"));
                owner.setText(map.get("owner"));
                petSex.setText(map.get("petSex"));
                petAge.setText(map.get("petAge"));
                petBreed.setText(map.get("petBreed"));
                Log.d("TUT","TUT");


            }

            @Override
            public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        Integer attitude = Integer.valueOf(marker.getSnippet());
        int color = possibleColors.get(attitude).first;
        linearLayout.setBackgroundColor(context.getResources().getColor(color));
        info.setBackgroundTintList(context.getResources().getColorStateList(color));

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {

        return null;
    }
}
