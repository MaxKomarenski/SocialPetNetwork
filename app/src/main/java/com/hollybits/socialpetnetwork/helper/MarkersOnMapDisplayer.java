package com.hollybits.socialpetnetwork.helper;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hollybits.socialpetnetwork.models.Coordinates;

import java.util.ArrayList;
import java.util.HashMap;
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

    public MarkersOnMapDisplayer(GoogleMap googleMap) {
        this.googleMap = googleMap;
        markerMover = new MarkerMover();
        displayedMarkers = new HashMap<>();
    }

    public void displayMarkers(Map<Long, Coordinates> longCoordinatesMap){
        Log.d("DISPLAYER", "START");
        unupdated = displayedMarkers.keySet();
        for (Map.Entry<Long, Coordinates> entry:longCoordinatesMap.entrySet()) {
            Log.d("DISPLAYER:", entry.getKey()+" "+entry.getValue().getLatitude()+ " "+ entry.getValue().getLongitude());
            if(currentUserId.equals(entry.getKey())){
                Log.d("DISPLAYER:", "Current user not displayed");
            }
            if(displayedMarkers.containsKey(entry.getKey())){
                markerMover.MoveMarkerToPosition(displayedMarkers.get(entry.getKey()), entry.getValue());
                unupdated.remove(entry.getKey());
            }else {
                Marker newMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(entry.getValue().getLatitude(), entry.getValue().getLongitude()))
                        .title("user"));
                newMarker.setSnippet(entry.getKey().toString());
                displayedMarkers.put(entry.getKey(), newMarker);
            }
            for (Long l: unupdated
                 ) {
                displayedMarkers.get(l).remove();
                displayedMarkers.remove(l);
            }
        }
    }


    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }




}
