package com.hollybits.socialpetnetwork.helper;


import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.hollybits.socialpetnetwork.models.Coordinates;



/**
 * Created by Victor on 18.08.2018.
 */

public class MarkerMover {



    public void MoveMarkerToPosition(Marker marker, Coordinates destination){


        final LatLng startPosition =  marker.getPosition();
        final LatLng finalPosition = new LatLng(destination.getLatitude(), destination.getLongitude());
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;


        float[] results = new float[1];
        Location.distanceBetween(startPosition.latitude, startPosition.longitude,
                finalPosition.latitude, finalPosition.longitude,
                results);
        if(results[0] < 5) return;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude*(1-t)+finalPosition.latitude*t,
                        startPosition.longitude*(1-t)+finalPosition.longitude*t);

                marker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });

        }
    }



