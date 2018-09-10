package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.FriendshipRequestAdapter;
import com.hollybits.socialpetnetwork.adapters.UserFriendsAdapter;
import com.hollybits.socialpetnetwork.data_queues.FriendShipRequestQueue;
import com.hollybits.socialpetnetwork.helper.FriendShipRequestObserver;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.User;
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

    @BindView(R.id.tableCardView)
    CardView tableCardView;

    @BindView(R.id.friendship_request_recycler_view)
    RecyclerView friendshipRequestRecyclerView;

    @BindView(R.id.user_friends_recycler_view)
    RecyclerView userFriendsRecyclerView;

    @BindView(R.id.search_in_friends)
    ExpandableSearchView searchView;

    @BindView(R.id.back_move_button)
    ImageButton backButton;

    @BindView(R.id.open_friend_list_in_user_friends_page)
    TextView friendsTextView;

    @BindView(R.id.open_requests_list_in_user_friends_page)
    TextView requestsTextView;

    @BindView(R.id.open_people_list_in_user_friends_page)
    TextView peopleTextView;

    private List<FriendInfo> friends;
    private List<InfoAboutUserFriendShipRequest> friendShipRequests;

    private UserFriendsAdapter userFriendsAdapter;
    private FriendshipRequestAdapter friendshipRequestAdapter;

    private Typeface nameFont, breedFont, mainFont;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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

        changeTypeface();
        listeners();

        searchView.setColor(getResources().getColor(R.color.online));

        getAllFriendshipRequests();
        getAllUserFriends();
        FriendShipRequestQueue.getInstance().addObserver(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        userFriendsRecyclerView.setLayoutManager(layoutManager);
        attachListeners();
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        userFriendsRecyclerView.setItemAnimator(animator);


        return view;
    }

    private void changeTypeface(){
        friendsTextView.setTypeface(mainFont);
        requestsTextView.setTypeface(mainFont);
        peopleTextView.setTypeface(mainFont);
    }

    private void listeners(){

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Account.class);
            }
        });

        friendsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColorsAndVisibility(View.VISIBLE, View.GONE,
                        R.drawable.background_for_table_friends_pressed,
                        getResources().getDrawable(R.color.blue_for_table),
                        R.drawable.background_for_table_people);

            }
        });

        requestsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColorsAndVisibility(View.GONE, View.VISIBLE,
                        R.drawable.background_for_table_friends,
                        getResources().getDrawable(R.color.blue_for_table_pressed),
                        R.drawable.background_for_table_people);

            }
        });

        peopleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColorsAndVisibility(View.GONE, View.GONE,
                        R.drawable.background_for_table_friends,
                        getResources().getDrawable(R.color.blue_for_table),
                        R.drawable.background_for_table_people_pressed);
            }
        });
    }

    private void changeColorsAndVisibility(int friendVisibility,
                                           int requestVisibility,
                                           int friendTextBackground,
                                           Drawable requestTextBackground,
                                           int peopleTextBackground){
        userFriendsRecyclerView.setVisibility(friendVisibility);
        friendshipRequestRecyclerView.setVisibility(requestVisibility);
        peopleTextView.setBackgroundResource(peopleTextBackground);
        requestsTextView.setBackground(requestTextBackground);
        friendsTextView.setBackgroundResource(friendTextBackground);
    }

    private void getAllFriendshipRequests(){
        friendShipRequests = Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST);
        friendshipRequestAdapter = new FriendshipRequestAdapter(friendShipRequests, userFriendsAdapter);
        friendshipRequestRecyclerView.setAdapter(friendshipRequestAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        friendshipRequestRecyclerView.setLayoutManager(layoutManager);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        friendshipRequestRecyclerView.setItemAnimator(animator);
        friendshipRequestAdapter.notifyDataSetChanged();
    }

    private void getAllUserFriends(){
        friends = Paper.book().read(MainActivity.FRIEND_LIST);
        if(friends == null){
            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            Map<String, String> authorisationCode = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());
            MainActivity.getServerRequests().getAllUserFriends(authorisationCode, currentUser.getId()).enqueue(new Callback<Set<FriendInfo>>() {
                @Override
                public void onResponse(Call<Set<FriendInfo>> call, Response<Set<FriendInfo>> response) {
                    friends = new ArrayList<>();
                    friends.addAll(response.body());
                    userFriendsAdapter = new UserFriendsAdapter(friends, nameFont, breedFont, UserFriends.this);
                    userFriendsRecyclerView.setAdapter(userFriendsAdapter);
                    userFriendsAdapter.notifyDataSetChanged();
                    Paper.book().write(MainActivity.FRIEND_LIST, friends);
                }

                @Override
                public void onFailure(Call<Set<FriendInfo>> call, Throwable t) {

                }
            });
        }else {
            userFriendsAdapter = new UserFriendsAdapter(friends, nameFont, breedFont,UserFriends.this);
            userFriendsRecyclerView.setAdapter(userFriendsAdapter);
            userFriendsAdapter.notifyDataSetChanged();
        }


    }


    private void attachListeners(){
        searchView.setOnSearchActionListener(new ExpandableSearchView.OnSearchActionListener() {
            @Override
            public void onSearchAction(String text) {
                Log.d("SEARCH TEXT", text);
                userFriendsAdapter.getFilter().filter(text);
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update() {
        Log.d("USER FRIENDS", "UPDATE CALL");
        friendshipRequestAdapter.addItem(FriendShipRequestQueue.getInstance().poll());
        try {
            getActivity().runOnUiThread(() -> friendshipRequestAdapter.notifyDataSetChanged());
        }catch (NullPointerException e){
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
}
