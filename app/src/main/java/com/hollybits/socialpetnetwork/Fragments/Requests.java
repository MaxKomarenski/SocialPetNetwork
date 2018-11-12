package com.hollybits.socialpetnetwork.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.activity.SettingsActivity;
import com.hollybits.socialpetnetwork.adapters.FriendshipRequestAdapter;
import com.hollybits.socialpetnetwork.data_queues.FriendShipRequestQueue;
import com.hollybits.socialpetnetwork.helper.FriendShipRequestObserver;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.User;

import java.util.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Requests.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Requests#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Requests extends Fragment implements FriendShipRequestObserver {


    @BindView(R.id.open_navigation_drawer)
    ImageButton openNavigation;

    @BindView(R.id.settings_button)
    ImageButton settingsButton;

    @BindView(R.id.friendship_request_recycler_view)
    RecyclerView friendshipRequestRecyclerView;

    @BindView(R.id.requests_text)
    TextView requestsText;

    @BindView(R.id.accept_button)
    public Button accept;

    @BindView(R.id.decline_button)
    public Button decline;

    @BindView(R.id.cancel)
    TextView cancel;

    @BindView(R.id.select_all)
    TextView selectAll;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FriendshipRequestAdapter friendshipRequestAdapter;

    private Typeface breedFont;
    private Typeface mainFont;

    private DrawerLayout drawer;

    public Requests() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Requests.
     */
    // TODO: Rename and change types and number of parameters
    public static Requests newInstance(String param1, String param2) {
        Requests fragment = new Requests();
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

        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        ButterKnife.bind(this, view);
        FriendShipRequestQueue.getInstance().addObserver(this);
        initFonts();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllFriendshipRequests();
        accept.setOnClickListener(v -> {
            for (Long id : friendshipRequestAdapter.getCheckedIds()) {
                Log.d("REQUESTS:", "ACCEPTING REQUEST WITH ID " + id);
                acceptFriendshipRequest(id, true);
            }
            accept.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
        });

        decline.setOnClickListener(v -> {
            for (Long id : friendshipRequestAdapter.getCheckedIds()) {
                acceptFriendshipRequest(id, false);
            }
            accept.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
        });

        accept.setVisibility(View.GONE);
        decline.setVisibility(View.GONE);


        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendshipRequestAdapter.selectAll = true;
                friendshipRequestAdapter.notifyDataSetChanged();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendshipRequestAdapter.cancel = true;
                friendshipRequestAdapter.notifyDataSetChanged();
                friendshipRequestAdapter.cancelSelection();
            }
        });

        openNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

    }


    private void acceptFriendshipRequest(Long request, boolean state) {


        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        java.util.Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        MainActivity.getServerRequests().acceptFriendshipInvitation(authorisationCode,
                currentUser.getId(),
                request,
                state).enqueue(new Callback<FriendInfo>() {
            @Override
            public void onResponse(Call<FriendInfo> call, Response<FriendInfo> response) {
                if (state) {
                    FriendInfo newFriend = response.body();
                    addNewFriendToPaperBook(newFriend);
                    Log.d("ACCEPTOR", "SUCSESSFUL RESPONSE, ADDED FRIEND TO PAPER");
                }
            }

            @Override
            public void onFailure(Call<FriendInfo> call, Throwable t) {
            }
        });

        deleteRequestFromPaperBook(friendshipRequestAdapter.getRequestById(request));
        Log.d("ACCEPTOR", "DELETE REQUEST FROM PAPER");
        friendshipRequestAdapter.deleteItem(friendshipRequestAdapter.getRequestById(request));
        friendshipRequestAdapter.notifyDataSetChanged();
        Log.d("ACCEPTOR", "ADAPTER NOTIFIRED");
    }


    private void addNewFriendToPaperBook(FriendInfo newFriend) {
        List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);
        friends.add(newFriend);
        Paper.book().write(MainActivity.FRIEND_LIST, friends);
    }

    private void deleteRequestFromPaperBook(InfoAboutUserFriendShipRequest info) {
        if (info == null)
            return;
        List<InfoAboutUserFriendShipRequest> list = Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST);
        for (int i = 0; i < list.size(); i++) {
            if (info.equals(list.get(i))) {
                list.remove(i);
            }
        }

        Paper.book().write(MainActivity.FRIENDSHIP_REQUEST_LIST, list);
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


    private void getAllFriendshipRequests() {
        List<InfoAboutUserFriendShipRequest> friendShipRequests = Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST);
        friendshipRequestAdapter = new FriendshipRequestAdapter(friendShipRequests, mainFont, breedFont, this);
        friendshipRequestRecyclerView.setAdapter(friendshipRequestAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        friendshipRequestRecyclerView.setLayoutManager(layoutManager);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        friendshipRequestRecyclerView.setItemAnimator(animator);
        friendshipRequestAdapter.notifyDataSetChanged();
    }

    private void initFonts() {
        breedFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/HelveticaNeueCyr.ttf");
        mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        requestsText.setTypeface(mainFont);
        decline.setTypeface(mainFont);
        accept.setTypeface(mainFont);
        selectAll.setTypeface(mainFont);
        cancel.setTypeface(mainFont);
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
