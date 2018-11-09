package com.hollybits.socialpetnetwork.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.activity.SettingsActivity;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.LostPet;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Wanted extends Fragment {

    @BindView(R.id.photo_in_wanted)
    CircleImageView photo;

    @BindViews({R.id.breed_word_text_view, R.id.owner_text_in_wanted, R.id.where_lost_text_view_in_wanted})
    List<TextView> words;

    @BindView(R.id.name_in_wanted)
    TextView name;

    @BindView(R.id.name_of_breed_text_view)
    TextView breed;

    @BindView(R.id.owner_name_and_surname_in_wanted)
    TextView ownerName;

    @BindView(R.id.where_lost_in_wanted)
    TextView where;

    @BindView(R.id.header_text_view_in_wanted)
    TextView headerText;

    @BindView(R.id.open_navigation_drawer_image_button)
    ImageButton openDrawerButton;

    @BindView(R.id.settings_wanted_img_button)
    ImageButton settingsButton;

    @BindView(R.id.see_button_in_wanted)
    Button seeButton;

    Typeface font;
    Typeface infoFont;
    Typeface buttonFont;

    private DrawerLayout drawer;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private PhotoManager photoManager;;

    public Wanted() {
        // Required empty public constructor
    }

    public static Wanted newInstance(String param1, String param2) {
        Wanted fragment = new Wanted();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        LostPet lostPet = Paper.book().read(MainActivity.WANTED);
        name.setText(lostPet.getPetName());
        name.setTypeface(infoFont);

        ownerName.setText(lostPet.getUserName());
        ownerName.setTypeface(infoFont);

        breed.setText(lostPet.getBreed());
        breed.setTypeface(infoFont);

        where.setText(lostPet.getDistrict());
        where.setTypeface(infoFont);

        headerText.setText(lostPet.getPetName() + "'s lost post");
        headerText.setTypeface(infoFont);

        photoManager.loadFriendsMainPhoto(photo, lostPet.getUserId());

        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        });

        seeButton.setTypeface(font);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        seeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllInformationOfChosenUser(lostPet.getUserId());
            }
        });

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
        View view = inflater.inflate(R.layout.fragment_wanted, container, false);
        ButterKnife.bind(this, view);

        photoManager = new PhotoManager(Wanted.this);
        font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        infoFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Regular.ttf");
        //buttonFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/");

        for (TextView word: words){
            word.setTypeface(font);
        }

        return view;
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
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getAllInformationOfChosenUser(Long id){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().getInformationOfAnotherUser(authorisationCode, id).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                Paper.book().write(MainActivity.CURRENT_CHOICE, response.body());
                FragmentDispatcher.launchFragment(FriendAccount.class);

            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }
}
