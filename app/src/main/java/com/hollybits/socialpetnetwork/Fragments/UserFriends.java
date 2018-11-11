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


    private DrawerLayout drawer;

//    @BindView(R.id.search_people_recycler_view)
//    RecyclerView searchPeopleRecyclerView;
//
//    @BindView(R.id.search_in_friends)
//    ExpandableSearchView searchView;
//
//    @BindView(R.id.back_move_button)
//    ImageButton backButton;

//    @BindView(R.id.open_friend_list_in_user_friends_page)
//    TextView friendsTextView;
//
//    @BindView(R.id.open_requests_list_in_user_friends_page)
//    TextView requestsTextView;
//
//    @BindView(R.id.open_people_list_in_user_friends_page)
//    TextView peopleTextView;

//    @BindView(R.id.owner_name_edit_text_in_people_search)
//    EditText ownerNameEditText;
//
//    @BindView(R.id.animal_edit_text_in_people_search)
//    EditText animalEditText;
//
//    @BindView(R.id.breed_edit_text_in_people_search)
//    EditText breedEditText;
//
//    @BindView(R.id.pet_name_edit_text_in_people_search)
//    EditText petNameEditText;
//
//    @BindView(R.id.search_people_button)
//    Button searchPeopleButton;

    private List<FriendInfo> friends;
    private List<InfoAboutUserFriendShipRequest> friendShipRequests;

    private UserFriendsAdapter userFriendsAdapter;
    private FriendshipRequestAdapter friendshipRequestAdapter;

    private PeopleSearchAdapter peopleSearchAdapter;

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

        //changeTypeface();
        listeners();

        //searchView.setColor(getResources().getColor(R.color.online));
        friendsTextView.setTypeface(mainFont);
        getAllUserFriends();
        getAllFriendshipRequests();
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


//    private void changeTypeface(){
//        friendsTextView.setTypeface(mainFont);
//        requestsTextView.setTypeface(mainFont);
//        peopleTextView.setTypeface(mainFont);
//    }

    private void listeners(){

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentDispatcher.launchFragment(Account.class);
//            }
//        });

//        friendsTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeColorsAndVisibility(View.VISIBLE, View.GONE,
//                        R.drawable.background_for_table_friends_pressed,
//                        getResources().getDrawable(R.color.blue_for_table),
//                        R.drawable.background_for_table_people, View.GONE , View.GONE  );
//
//            }
//        });

//        requestsTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeColorsAndVisibility(View.GONE, View.VISIBLE,
//                        R.drawable.background_for_table_friends,
//                        getResources().getDrawable(R.color.blue_for_table_pressed),
//                        R.drawable.background_for_table_people, View.GONE , View.GONE);
//
//            }
//        });

//        peopleTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeColorsAndVisibility(View.GONE, View.GONE,
//                        R.drawable.background_for_table_friends,
//                        getResources().getDrawable(R.color.blue_for_table),
//                        R.drawable.background_for_table_people_pressed, View.VISIBLE, View.VISIBLE);
//            }
//        });

//        searchPeopleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
//                Map<String, String> authorisationCode = new HashMap<>();
//                authorisationCode.put("authorization", currentUser.getAuthorizationCode());
//                SearchForm searchForm = new SearchForm();
//                searchForm.setUserCountry(currentUser.getCity().getCountry().getName());
//                searchForm.setUserCity(currentUser.getCity().getName());
//                if(ownerNameEditText.getText().toString().equals(""))
//                    searchForm.setOwnersName(null);
//                else {
//                    searchForm.setOwnersName(ownerNameEditText.getText().toString());
//                }
//                if(breedEditText.getText().toString().equals(""))
//                    searchForm.setBreed(null);
//                else {
//                    searchForm.setBreed(breedEditText.getText().toString());
//                }
//                if(petNameEditText.getText().toString().equals(""))
//                    searchForm.setName(null);
//                else {
//                    searchForm.setName(petNameEditText.getText().toString());
//                }
//                if(animalEditText.getText().toString().toUpperCase().equals(""))
//                    searchForm.setPetType(null);
//                else {
//                    searchForm.setPetType(PetType.valueOf(animalEditText.getText().toString().toUpperCase()));
//                }
//                MainActivity.getServerRequests().search(authorisationCode, searchForm).enqueue(new Callback<List<FriendInfo>>() {
//                    @Override
//                    public void onResponse(Call<List<FriendInfo>> call, Response<List<FriendInfo>> response) {
//                        if(response.body()!=null){
//                            peopleSearchAdapter.setFriends(response.body());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<List<FriendInfo>> call, Throwable t) {
//
//                    }
//                });
//
//            }
//        });
    }

//    private void changeColorsAndVisibility(int friendVisibility,
//                                           int requestVisibility,
////                                           int friendTextBackground,
////                                           Drawable requestTextBackground,
////                                           int peopleTextBackground,
//                                           int peopleVisibility,
//                                           int filterVisibility){
//        userFriendsRecyclerView.setVisibility(friendVisibility);
//        friendshipRequestRecyclerView.setVisibility(requestVisibility);
//        searchPeopleRecyclerView.setVisibility(peopleVisibility);
////        peopleTextView.setBackgroundResource(peopleTextBackground);
////        requestsTextView.setBackground(requestTextBackground);
////        friendsTextView.setBackgroundResource(friendTextBackground);
//        filterCardView.setVisibility(filterVisibility);
   // }

    private void getAllFriendshipRequests(){
        friendShipRequests = Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST);
        //friendshipRequestRecyclerView.setAdapter(friendshipRequestAdapter);
        friendshipRequestAdapter = new FriendshipRequestAdapter(friendShipRequests, mainFont, breedFont);


        //friendshipRequestRecyclerView.setAdapter(friendshipRequestAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        //friendshipRequestRecyclerView.setLayoutManager(layoutManager);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));


        //friendshipRequestRecyclerView.setItemAnimator(animator);
        friendshipRequestAdapter.notifyDataSetChanged();
    }

    private void getAllUserFriends(){
        userFriendsAdapter = new UserFriendsAdapter(mainFont, breedFont, UserFriends.this, UserFriends.this);
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
                    userFriendsAdapter.setFriends(friends);
                    userFriendsRecyclerView.setAdapter(userFriendsAdapter);
                    userFriendsAdapter.notifyDataSetChanged();
                    Paper.book().write(MainActivity.FRIEND_LIST, friends);
                }

                @Override
                public void onFailure(Call<Set<FriendInfo>> call, Throwable t) {

                }
            });
        }else {
            userFriendsAdapter = new UserFriendsAdapter(mainFont, breedFont,UserFriends.this, UserFriends.this);
            userFriendsAdapter.setFriends(friends);
            userFriendsRecyclerView.setAdapter(userFriendsAdapter);
            userFriendsAdapter.notifyDataSetChanged();
        }


    }

    public void showDownBar(FriendInfo friendInfo){
        downBar.setVisibility(View.VISIBLE);

        openFriendPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllInformationOfChosenUser(friendInfo.getId());
            }
        });

        openChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write(MainActivity.ID_OF_FRIEND, friendInfo.getId());
                Paper.book().write(MainActivity.NAME_OF_FRIEND, friendInfo.getName());
                FragmentDispatcher.launchFragment(Chat.class);
            }
        });
    }


    private void attachListeners(){
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
