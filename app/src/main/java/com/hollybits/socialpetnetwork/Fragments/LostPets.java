package com.hollybits.socialpetnetwork.Fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.LostPetAdapter;
import com.hollybits.socialpetnetwork.helper.MessageObserver;
import com.hollybits.socialpetnetwork.models.LostPet;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.notifications.NotificationsAcceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LostPets extends Fragment {

    @BindView(R.id.lost_pets_recycler_view)
    RecyclerView lostPetsRecyclerView;

    LostPetAdapter lostPetAdapter;
    List<LostPet> lostPetList;
    private Geocoder locationInfoSupplier = new Geocoder(FragmentDispatcher.getInstance());

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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

    private void getAllLostPetsFromUserDistrict() throws IOException {
        double longitude = Paper.book().read(com.hollybits.socialpetnetwork.Fragments.Map.LONGITUDE);
        double latitude = Paper.book().read(com.hollybits.socialpetnetwork.Fragments.Map.LATITUDE);
        List<Address> addresses = locationInfoSupplier.getFromLocation(latitude, longitude, 1);
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        if(addresses.size() == 1){
            MainActivity.getServerRequests().getAllLostPetsFromUserDistrict(authorisationCode, addresses.get(0)).enqueue(new Callback<List<LostPet>>() {
                @Override
                public void onResponse(Call<List<LostPet>> call, Response<List<LostPet>> response) {
                    lostPetList.clear();

                    lostPetList.addAll(response.body());
                    lostPetAdapter = new LostPetAdapter(lostPetList);
                    lostPetsRecyclerView.setAdapter(lostPetAdapter);
                    try{
                    LostPets.this.getActivity().runOnUiThread(()->{
                        lostPetAdapter.notifyDataSetChanged();
                    });
                    }catch (NullPointerException e ){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<List<LostPet>> call, Throwable t) {

                }
            });
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



    public void update(){
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
