package com.hollybits.socialpetnetwork.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.dialogs.AcceptThatYourPetIsLostDialog;
import com.hollybits.socialpetnetwork.helper.MarkersOnMapDisplayer;
import com.hollybits.socialpetnetwork.models.Coordinates;
import com.hollybits.socialpetnetwork.models.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Map.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Map#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Map extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    //
    // private BottomSheet bottomSheet;
    private ScheduledExecutorService positionTracker;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static int ACCESS_FINE_LOCATION_CODE = 1001;
    private OnFragmentInteractionListener mListener;


    @BindView(R.id.mapView)
    MapView mMapView;

    @BindView(R.id.sos)
    ImageButton sos;

    @BindView(R.id.help)
    ImageButton help;


    private GoogleMap googleMap;
    private Geocoder locationInfoSupplier = new Geocoder(FragmentDispatcher.getInstance());

    private MarkersOnMapDisplayer markersOnMapDisplayer;
    private User currentUser;
    private java.util.Map<String, String> code;
//    @BindView(R.id.open_sos_menu_button)
//     Button openSosMenuButton;

    private FusedLocationProviderClient mFusedLocationClient;


    public Map() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                markersOnMapDisplayer = new MarkersOnMapDisplayer(googleMap, Map.this.getActivity());
                currentUser = Paper.book().read(MainActivity.CURRENTUSER);
                code = new HashMap<>();
                code.put("authorization", currentUser.getAuthorizationCode());

                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                Map.this.getActivity(), R.raw.map_style));

                if (!success) {
                    Log.e("MAP FRAGMENT", "Style parsing failed.");
                }

                // For showing a move to my location button
                if (ContextCompat.checkSelfPermission(Map.this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Map.this.getContext());
                    animateMapToUsersLocation();
                    positionTracker = Executors.newScheduledThreadPool(2);
                    positionTracker.scheduleAtFixedRate(Map.this::startTracking, 0, 1, TimeUnit.SECONDS);
                    positionTracker.scheduleAtFixedRate(Map.this::locateOthers, 1, 1, TimeUnit.SECONDS);
                    attachListeners();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
                }
                // For dropping a marker at a point on the Map
            }
        });
        return view;
    }


    private void startTracking() throws SecurityException {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        try {
                            List<Address> addresses = locationInfoSupplier.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses.size() == 1)
                                MainActivity.getServerRequests().updateMyPosition(code, addresses.get(0), currentUser.getId(), (byte) currentUser.getPets().get(0).getAttitude().ordinal()).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {

                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
    }


    @SuppressLint("MissingPermission")
    private void locateOthers() {
        markersOnMapDisplayer.setCurrentUserId(currentUser.getId());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        try {
                            List<Address> addresses = locationInfoSupplier.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            MainActivity.getServerRequests().getUsersNearMe(code, addresses.get(0), currentUser.getId()).enqueue(new Callback<java.util.Map<Long, Coordinates>>() {
                                @Override
                                public void onResponse(Call<java.util.Map<Long, Coordinates>> call, Response<java.util.Map<Long, Coordinates>> response) {
                                    if (response.code() == 200) {
                                        markersOnMapDisplayer.displayMarkers(response.body());
                                    }
                                }

                                @Override
                                public void onFailure(Call<java.util.Map<Long, Coordinates>> call, Throwable t) {

                                }
                            });

                        } catch (IOException e) {
                            Toast.makeText
                                    (Map.this.getContext(), "Some error has happen, Please check if location service is ON", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @SuppressLint("MissingPermission")
    private void animateMapToUsersLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
                        googleMap.animateCamera(cameraUpdate);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ACCESS_FINE_LOCATION_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    googleMap.setMyLocationEnabled(true);
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Map.this.getContext());
                    positionTracker = Executors.newScheduledThreadPool(2);
                    positionTracker.scheduleAtFixedRate(Map.this::startTracking, 0, 5, TimeUnit.SECONDS);
                    positionTracker.scheduleAtFixedRate(Map.this::locateOthers, 0, 1, TimeUnit.SECONDS);
                    animateMapToUsersLocation();
                    attachListeners();
                } catch (SecurityException e) {
                    Log.d("PERMISSION", "SecurityException");
                }
            } else {
                Log.d("PERMISSION", "REJECTED"); // TODO ERROR MESSAGE
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        positionTracker.shutdown();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void attachListeners() {
        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        sos.setVisibility(View.VISIBLE);
        sos.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                sos.startAnimation(buttonClick);
                AcceptThatYourPetIsLostDialog lostDialog = new AcceptThatYourPetIsLostDialog(Map.this.getContext());
                lostDialog.initialise(locationInfoSupplier, currentUser, code, mFusedLocationClient);
                lostDialog.show();
            }
        });

        help.setVisibility(View.VISIBLE);
        help.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                help.startAnimation(buttonClick);
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Paper.book().write(LATITUDE, location.getLatitude());
                            Paper.book().write(LONGITUDE, location.getLongitude());
                            //bottomSheet.dismiss();
                            FragmentDispatcher.launchFragment(LostPets.class);
                        } else {
                            Toast.makeText
                                    (Map.this.getContext(), "Some error has happen, Please check if location service are ON", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Map.
     */
    // TODO: Rename and change types and number of parameters
    public static Map newInstance(String param1, String param2) {
        Map fragment = new Map();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

}
