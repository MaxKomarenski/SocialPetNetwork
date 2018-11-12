package com.hollybits.socialpetnetwork.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.LostPetAdapter;
import com.hollybits.socialpetnetwork.helper.MessageObserver;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.LostPet;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;
import com.hollybits.socialpetnetwork.notifications.NotificationsAcceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LostPets extends Fragment {

    @BindView(R.id.lost_pets_recycler_view)
    RecyclerView lostPetsRecyclerView;

    @BindView(R.id.lost_pets_text_in_lost_pets_fragment)
    TextView topicText;

    @BindView(R.id.back_to_map_button)
    Button backToMap;

    @BindView(R.id.contact_with_owner_img_button)
    ImageButton contactButton;

    @BindView(R.id.information_img_button)
    ImageButton informationButton;

    @BindView(R.id.i_found_my_pet)
    ImageButton foundMyPet;

    @BindView(R.id.contact_lin_layout)
    LinearLayout contactLinLayout;

    @BindView(R.id.found_lin_layout)
    LinearLayout foundLinLayout;

    @BindView(R.id.info_lin_layout)
    LinearLayout infoLinLayout;

    @BindViews({R.id.profile_text, R.id.found_my_pet_text, R.id.write_to_owner_text})
    List<TextView> textsUnderButtons;


    LostPetAdapter lostPetAdapter;
    List<LostPet> lostPetList;
    private Geocoder locationInfoSupplier = new Geocoder(FragmentDispatcher.getInstance());
    private Typeface breedFont, mainFont;
    Double longitude;
    Double latitude;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FusedLocationProviderClient mFusedLocationClient;
    private static LostPets instance;

    public static LostPets getInstance() {
        return instance;
    }

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LostPets() {
        // Required empty public constructor
    }

    public static LostPets newInstance(String param1, String param2) {
        LostPets fragment = new LostPets();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_lost_pets, container, false);
        ButterKnife.bind(this, view);
        instance = this;

        breedFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/HelveticaNeueCyr.ttf");
        mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");

        for (TextView text :
                textsUnderButtons) {
            text.setTypeface(mainFont);
        }

        backToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(com.hollybits.socialpetnetwork.Fragments.Map.class);
            }
        });

        topicText.setTypeface(mainFont);

        lostPetList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        lostPetsRecyclerView.setLayoutManager(layoutManager);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        lostPetsRecyclerView.setItemAnimator(animator);


        try {
            getAllLostPetsFromUserDistrict();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return view;
    }

    public void showDownBar(LostPet lostPet) {
        infoLinLayout.setVisibility(View.VISIBLE);

        if (!lostPet.getUserId().equals(MainActivity.getCurrentUser().getId())) {
            foundLinLayout.setVisibility(View.GONE);
            contactLinLayout.setVisibility(View.VISIBLE);
            contactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isLostPetInFriendshipWithUser(lostPet.getUserId())) {
                        Paper.book().write(MainActivity.ID_OF_FRIEND, lostPet.getUserId());
                        Paper.book().write(MainActivity.NAME_OF_FRIEND, lostPet.getUserName());
                        FragmentDispatcher.launchFragment(Chat.class);

                    } else {
                        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
                        Map<String, String> authorisationCode = new HashMap<>();
                        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
                        MainActivity.getServerRequests().addToFriendsWhenOneUserFoundPetOfAnotherUser(authorisationCode,
                                currentUser.getId(), lostPet.getUserId()).enqueue(new Callback<FriendInfo>() {
                            @Override
                            public void onResponse(Call<FriendInfo> call, Response<FriendInfo> response) {
                                if (response.body() != null) {
                                    addNewFriendToPaperBook(response.body());
                                    Paper.book().delete(MainActivity.CONTACT_LIST);

                                    Paper.book().write(MainActivity.ID_OF_FRIEND, lostPet.getUserId());
                                    Paper.book().write(MainActivity.NAME_OF_FRIEND, lostPet.getUserName());
                                    FragmentDispatcher.launchFragment(Chat.class);
                                }
                            }

                            @Override
                            public void onFailure(Call<FriendInfo> call, Throwable t) {

                            }
                        });
                    }
                }
            });

            informationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Paper.book().write(MainActivity.WANTED, lostPet);
                    FragmentDispatcher.launchFragment(Wanted.class);
                }
            });
        } else {
            contactLinLayout.setVisibility(View.GONE);
            foundLinLayout.setVisibility(View.VISIBLE);
            foundMyPet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
                    Map<String, String> authorisationCode = new HashMap<>();
                    authorisationCode.put("authorization", currentUser.getAuthorizationCode());
                    MainActivity.getServerRequests().userFoundHisPet(authorisationCode, lostPet.getPetId()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            lostPetAdapter.deleteItem(lostPet);
                            lostPetAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            });

            informationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentDispatcher.launchFragment(Account.class);
                }
            });
        }
    }


    private void addNewFriendToPaperBook(FriendInfo newFriend) {
        List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);
        friends.add(newFriend);
        Paper.book().write(MainActivity.FRIEND_LIST, friends);
    }

    private void getAllLostPetsFromUserDistrict() throws IOException {
        longitude = Paper.book().read(com.hollybits.socialpetnetwork.Fragments.Map.LONGITUDE);
        latitude = Paper.book().read(com.hollybits.socialpetnetwork.Fragments.Map.LATITUDE);

        if (latitude == null || longitude == null) {
            getCoordinates();
        } else {
            loadInfo();
        }

    }

    private boolean isLostPetInFriendshipWithUser(Long id) {
        List<FriendInfo> userFriends = Paper.book().read(MainActivity.FRIEND_LIST);
        for (FriendInfo friendInfo : userFriends) {
            if (friendInfo.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    private void loadInfo() throws IOException {
        List<Address> addresses = locationInfoSupplier.getFromLocation(latitude, longitude, 1);
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());


        if (addresses.size() == 1) {
            MainActivity.getServerRequests().getAllLostPetsFromUserDistrict(authorisationCode, addresses.get(0)).enqueue(new Callback<List<LostPet>>() {
                @Override
                public void onResponse(Call<List<LostPet>> call, Response<List<LostPet>> response) {
                    if (response.body() != null) {
                        lostPetList.clear();
                        lostPetList.addAll(response.body());
                        lostPetAdapter = new LostPetAdapter(lostPetList, mainFont, breedFont, LostPets.this, LostPets.this);
                        lostPetsRecyclerView.setAdapter(lostPetAdapter);
                        try {
                            LostPets.this.getActivity().runOnUiThread(() -> {
                                lostPetAdapter.notifyDataSetChanged();
                            });
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<List<LostPet>> call, Throwable t) {

                }
            });
        }
    }


    private void getCoordinates() {


        if (this.getActivity() != null) {
            if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
                initializeCoordinates();
            } else {
                int ACCESS_FINE_LOCATION_CODE = 1001;
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_CODE);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeCoordinates() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                try {
                    loadInfo();
                } catch (IOException e) {
                    Toast.makeText(LostPets.this.getActivity(), "Cant find your location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1001) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
                    initializeCoordinates();
                } catch (SecurityException e) {
                    Log.d("PERMISSION", "SecurityException");
                }
            } else {
                Log.d("PERMISSION", "REJECTED"); // TODO ERROR MESSAGE
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        instance = null;

    }


    public void update() {
        try {
            getAllLostPetsFromUserDistrict();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
