package com.hollybits.socialpetnetwork.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.activity.SettingsActivity;
import com.hollybits.socialpetnetwork.adapters.FriendshipRequestAdapter;
import com.hollybits.socialpetnetwork.adapters.PeopleSearchAdapter;
import com.hollybits.socialpetnetwork.adapters.UserFriendsAdapter;
import com.hollybits.socialpetnetwork.data_queues.FriendShipRequestQueue;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.helper.FriendShipRequestObserver;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.SearchForm;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;
import com.hollybits.socialpetnetwork.widgets.ExpandableSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserFriends extends Fragment implements FriendShipRequestObserver {

//    @BindView(R.id.tableCardView)
//    CardView tableCardView;

//    @BindView(R.id.edit_texts_for_filter_search)
//    CardView filterCardView;
//
//    @BindView(R.id.friendship_request_recycler_view)
//    RecyclerView friendshipRequestRecyclerView;

    @BindView(R.id.move_to_search_fragment_button)
    Button searchButton;

    @BindView(R.id.move_to_request_fragment_button)
    Button requestButton;

    @BindView(R.id.user_friends_recycler_view)
    RecyclerView userFriendsRecyclerView;

    @BindView(R.id.friends_text_in_user_friends)
    TextView friendsTextView;

    @BindView(R.id.down_bar_in_user_friend)
    LinearLayout downBar;

    @BindView(R.id.open_friend_page)
    ImageButton openFriendPage;

    @BindView(R.id.go_to_chat_with_friend)
    ImageButton openChat;

    @BindView(R.id.open_navigation_drawer_image_button)
    ImageButton openDrawerButton;

    @BindView(R.id.settings_account_img_button)
    ImageButton settings;

    @BindView(R.id.search_friend)
    EditText searchView;

    @BindView(R.id.go_to_map)
    ImageButton goToMap;

    private DrawerLayout drawer;


    private List<FriendInfo> friends;
    private List<InfoAboutUserFriendShipRequest> friendShipRequests;

    private UserFriendsAdapter userFriendsAdapter;
    private FriendshipRequestAdapter friendshipRequestAdapter;

    private PeopleSearchAdapter peopleSearchAdapter;

    private Typeface nameFont, breedFont, mainFont;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.no_res_layout)
    LinearLayout noResultsLayout;
    @BindView(R.id.no_friends_text)
    TextView noFriendsText;
    @BindView(R.id.start_communicating_text)
    TextView startComunicatingText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserFriends() {
        // Required empty public constructor
    }

    public static UserFriends newInstance(String param1, String param2) {
        UserFriends fragment = new UserFriends();
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
        View view = inflater.inflate(R.layout.fragment_user_friends, container, false);
        ButterKnife.bind(this, view);

        nameFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/GOTHIC.TTF");
        breedFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/HelveticaNeueCyr.ttf");
        mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");

        noFriendsText.setTypeface(mainFont);
        startComunicatingText.setTypeface(mainFont);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(com.hollybits.socialpetnetwork.Fragments.Map.class);
            }
        });
        //changeTypeface();
        //listeners();

        //searchView.setColor(getResources().getColor(R.color.online));
        friendsTextView.setTypeface(mainFont);
        getAllUserFriends();
        FriendShipRequestQueue.getInstance().addObserver(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        userFriendsRecyclerView.setLayoutManager(layoutManager);
        attachListeners();
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        userFriendsRecyclerView.setItemAnimator(animator);
        peopleSearchAdapter = new PeopleSearchAdapter(this, nameFont, breedFont);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this.getContext());
        //searchPeopleRecyclerView.setLayoutManager(layoutManager2);
        SlideInUpAnimator animator2 = new SlideInUpAnimator(new OvershootInterpolator(1f));
        //searchPeopleRecyclerView.setItemAnimator(animator2);
        //searchPeopleRecyclerView.setAdapter(peopleSearchAdapter);


        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Requests.class);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(SearchPeople.class);
            }
        });

        return view;
    }

    private void filter(String text) {
        ArrayList<FriendInfo> filteredList = new ArrayList<>();

        System.err.println("text --->  " + text);
        for (FriendInfo friend : friends) {
            if (friend.getPetName().toLowerCase().contains(text.toLowerCase())) {
                System.err.println("pet name ---->  " + friend.getPetName());
                filteredList.add(friend);
            }
        }
        System.err.println("size --->  " + filteredList.size());
        userFriendsAdapter.filterList(filteredList);
    }

    private void getAllUserFriends() {
        userFriendsAdapter = new UserFriendsAdapter(mainFont, breedFont, UserFriends.this, UserFriends.this);
        friends = Paper.book().read(MainActivity.FRIEND_LIST);
        if (friends == null) {
            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            Map<String, String> authorisationCode = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());
            MainActivity.getServerRequests().getAllUserFriends(authorisationCode, currentUser.getId()).enqueue(new Callback<Set<FriendInfo>>() {
                @Override
                public void onResponse(Call<Set<FriendInfo>> call, Response<Set<FriendInfo>> response) {
                    friends = new ArrayList<>();
                    friends.addAll(response.body());
                    if (response.body().size() == 0) {
                        noResultsLayout.setVisibility(View.VISIBLE);
                    }
                    userFriendsAdapter.setFriends(friends);
                    userFriendsRecyclerView.setAdapter(userFriendsAdapter);
                    userFriendsAdapter.notifyDataSetChanged();
                    Paper.book().write(MainActivity.FRIEND_LIST, friends);
                }

                @Override
                public void onFailure(Call<Set<FriendInfo>> call, Throwable t) {

                }
            });
        } else {
            userFriendsAdapter = new UserFriendsAdapter(mainFont, breedFont, UserFriends.this, UserFriends.this);
            userFriendsAdapter.setFriends(friends);
            if (friends.size() == 0) {
                noResultsLayout.setVisibility(View.VISIBLE);
            }
            userFriendsRecyclerView.setAdapter(userFriendsAdapter);
            userFriendsAdapter.notifyDataSetChanged();
        }


    }

    public void showDownBar(FriendInfo friendInfo) {
        downBar.setVisibility(View.VISIBLE);

        openFriendPage.setOnClickListener(v -> getAllInformationOfChosenUser(friendInfo.getId()));

        openChat.setOnClickListener(v -> {
            Paper.book().write(MainActivity.ID_OF_FRIEND, friendInfo.getId());
            Paper.book().write(MainActivity.NAME_OF_FRIEND, friendInfo.getName());
            FragmentDispatcher.launchFragment(Chat.class);
        });
    }


    private void attachListeners() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        FriendShipRequestQueue.getInstance().removeObserver(this);
    }

    @Override
    public void update() {
        Log.d("USER FRIENDS", "UPDATE CALL");
        friendshipRequestAdapter.addItem(FriendShipRequestQueue.getInstance().poll());
        try {
            getActivity().runOnUiThread(() -> friendshipRequestAdapter.notifyDataSetChanged());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

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

    private void getAllInformationOfChosenUser(Long id) {
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
