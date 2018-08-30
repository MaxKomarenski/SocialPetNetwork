package com.hollybits.socialpetnetwork.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.enums.Attitude;
import com.hollybits.socialpetnetwork.models.Coordinates;
import com.hollybits.socialpetnetwork.models.User;
import com.kennyc.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Victor on 18.08.2018.
 */

public class MarkersOnMapDisplayer {

    private GoogleMap googleMap;
    private MarkerMover markerMover;
    private Long currentUserId;
    private Activity context;

    private User currentUser;
    private java.util.Map<String, String> code;
    private TextView petName ;
    private TextView owner ;
    private TextView petSex ;
    private TextView petAge ;
    private TextView petBreed;
    private java.util.Map<Integer, Pair<Integer, Integer>> possibleColors;


    private Map<Long, Marker> displayedMarkers;
    private Set<Long> unupdated;

    @SuppressLint("UseSparseArrays")
    public MarkersOnMapDisplayer(GoogleMap googleMap, Activity context) {
        this.googleMap = googleMap;
        markerMover = new MarkerMover();
        unupdated = new HashSet<>();
        displayedMarkers = new HashMap<>();
        this.context = context;
        currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        code = new HashMap<>();
        code.put("authorization", currentUser.getAuthorizationCode());

        possibleColors = new HashMap<>();
        possibleColors.put(Attitude.GOODWITHALL.ordinal(), new Pair<>(R.color.green, R.drawable.map_info_window_green));
        possibleColors.put(Attitude.GOODWITHMALE.ordinal(), new Pair<>(R.color.yellow, R.drawable.map_info_window_yellow));
        possibleColors.put(Attitude.GOODWITHFEMALE.ordinal(), new Pair<>(R.color.blue, R.drawable.map_info_window_blue));
        possibleColors.put(Attitude.BAD.ordinal(), new Pair<>(R.color.red, R.drawable.map_info_window_red));
    }

    public void displayMarkers(Map<Long, Coordinates> longCoordinatesMap){
        unupdated.addAll(displayedMarkers.keySet());
        for (Map.Entry<Long, Coordinates> entry:longCoordinatesMap.entrySet()) {
            if(currentUserId.equals(entry.getKey())){
                continue;
            }
            if(displayedMarkers.containsKey(entry.getKey())){
                markerMover.MoveMarkerToPosition(displayedMarkers.get(entry.getKey()), entry.getValue());
                unupdated.remove(entry.getKey());
            }else {
                BitmapDescriptor color;
                switch (entry.getValue().getAttitude()){
                    case 0:{
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        break;
                    }
                    case 1:{
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                        break;
                    }
                    case 2:{
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                        break;
                    }
                    case 3:{
                        color =BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        break;
                    }
                    default:{
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                    }
                }
                Marker newMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude()))
                        .icon(color)
                        .title(entry.getKey().toString()));
                newMarker.setSnippet(String.valueOf(entry.getValue().getAttitude()));

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        View view = context.getLayoutInflater().inflate(R.layout.user_info_on_map, null);
                        ConstraintLayout linearLayout = view.findViewById(R.id.user_info_on_map_layout);
                        ImageButton info = view.findViewById(R.id.info_button_in_map_info_window);
                        Long userId = Long.decode(marker.getTitle());
                        MainActivity.getServerRequests().getUserInfoMap(code,userId).enqueue(new Callback<Map<String, String>>() {
                            @Override
                            public void onResponse(Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {

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
                                Integer attitude = Integer.valueOf(marker.getSnippet());
                                int color = possibleColors.get(attitude).first;
                                linearLayout.setBackgroundTintList(context.getResources().getColorStateList(color));
                                info.setBackgroundTintList(context.getResources().getColorStateList(color));
                                new BottomSheet.Builder(context)
                                        .setView(view)
                                        // You can also show the custom view with some padding in DP (left, top, right, bottom)
                                        //.setCustomView(customView, 20, 20, 20, 0)
                                        .show();


                            }

                            @Override
                            public void onFailure(Call<java.util.Map<String, String>> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

                        return true;
                    }
                });
                displayedMarkers.put(entry.getKey(), newMarker);
                unupdated.remove(entry.getKey());
            }
        }

        for(Long l: unupdated){
            displayedMarkers.get(l).remove();
            displayedMarkers.remove(l);
        }
        unupdated.clear();

    }


    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }




}
