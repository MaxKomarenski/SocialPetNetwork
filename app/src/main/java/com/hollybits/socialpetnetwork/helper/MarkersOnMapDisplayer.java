package com.hollybits.socialpetnetwork.helper;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.enums.Attitude;
import com.hollybits.socialpetnetwork.models.Coordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Victor on 18.08.2018.
 */

public class MarkersOnMapDisplayer {

    private GoogleMap googleMap;
    private MarkerMover markerMover;
    private Long currentUserId;


    private Map<Long, Marker> displayedMarkers;
    private Set<Long> unupdated;

    @SuppressLint("UseSparseArrays")
    public MarkersOnMapDisplayer(GoogleMap googleMap) {
        this.googleMap = googleMap;
        markerMover = new MarkerMover();
        unupdated = new HashSet<>();
        displayedMarkers = new HashMap<>();
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
                int color;
                switch (entry.getValue().getAttitude()){
                    case 0:{
                        color = R.color.green;
                        break;
                    }
                    case 1:{
                        color = R.color.yellow;
                        break;
                    }
                    case 2:{
                        color = R.color.yellow;
                        break;
                    }
                    case 3:{
                        color = R.color.red;
                        break;
                    }
                    default:{
                        color = R.color.yellow;
                    }
                }
                Marker newMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(color))
                        .title("user"));
                newMarker.setSnippet("Attitude = "+ entry.getValue().getAttitude());
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
