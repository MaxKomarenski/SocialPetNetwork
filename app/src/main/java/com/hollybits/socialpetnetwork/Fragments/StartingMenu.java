package com.hollybits.socialpetnetwork.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.activity.SettingsActivity;
import com.hollybits.socialpetnetwork.adapters.ContactAdapter;
import com.hollybits.socialpetnetwork.enums.GalleryMode;
import com.hollybits.socialpetnetwork.helper.FriendDownloader;
import com.hollybits.socialpetnetwork.models.Contact;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.LostPet;
import com.hollybits.socialpetnetwork.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartingMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartingMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartingMenu extends Fragment {

    @BindView(R.id.settings_starting_menu_img_button)
    ImageButton settingsButton;

    @BindView(R.id.friends_in_starting_menu)
    ImageView friends;

    @BindView(R.id.profile_in_starting_menu)
    ImageView profile;

    @BindView(R.id.messages_in_starting_menu)
    ImageView messages;

    @BindView(R.id.map_in_starting_menu)
    ImageView map;

    @BindView(R.id.gallery_in_starting_menu)
    ImageView gallery;

    @BindView(R.id.store_in_starting_menu)
    ImageView store;


    @BindView(R.id.lost_pets_menu)
    ImageButton lostPets;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public StartingMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartingMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static StartingMenu newInstance(String param1, String param2) {
        StartingMenu fragment = new StartingMenu();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_starting_menu, container, false);
        ButterKnife.bind(this, view);

        getContacts();
        allListeners();
        getFriendsWhoAreNotInCache();
        getFriends();


        return view;
    }

    private void getFriendsWhoAreNotInCache(){
        FriendDownloader.getFriendIDsWhichDeletedUserFromFriendList();
    }

    private void allListeners(){
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(UserFriends.class);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Account.class);
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Messages.class);
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Store.class);
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Map.class);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write(MainActivity.GALLERY_MODE, GalleryMode.USERS_MODE);
                FragmentDispatcher.launchFragment(UsersGallery.class);
            }
        });
        lostPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(LostPets.class);
            }
        });


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void getContacts(){
        List<Contact> contacts = Paper.book().read(MainActivity.CONTACT_LIST);
        if(contacts == null){
            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            java.util.Map<String, String> authorisationCode = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());
            MainActivity.getServerRequests().getAllContactsOfCurrentUser(authorisationCode ,MainActivity.getCurrentUser().getId()).enqueue(new Callback<List<Contact>>() {
                @Override
                public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                    List<Contact> contacts = response.body();
                    Paper.book().write(MainActivity.CONTACT_LIST, contacts);
                }

                @Override
                public void onFailure(Call<List<Contact>> call, Throwable t) {

                }
            });
        }
    }

    private void getFriends(){
        List<FriendInfo> userFriends = Paper.book().read(MainActivity.FRIEND_LIST);
        if(userFriends == null){
            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            java.util.Map<String, String> authorisationCode = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());
            MainActivity.getServerRequests().getAllUserFriends(authorisationCode, currentUser.getId()).enqueue(new Callback<Set<FriendInfo>>() {
                @Override
                public void onResponse(Call<Set<FriendInfo>> call, Response<Set<FriendInfo>> response) {
                    List<FriendInfo> userFriends = new ArrayList<>();
                    userFriends.addAll(response.body());
                    Paper.book().write(MainActivity.FRIEND_LIST, userFriends);
                }

                @Override
                public void onFailure(Call<Set<FriendInfo>> call, Throwable t) {

                }
            });
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
