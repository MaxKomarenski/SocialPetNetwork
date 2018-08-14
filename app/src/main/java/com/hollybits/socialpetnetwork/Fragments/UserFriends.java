package com.hollybits.socialpetnetwork.Fragments;

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
import com.hollybits.socialpetnetwork.adapters.UserFriendsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class UserFriends extends Fragment {

    @BindView(R.id.user_friends_recycler_view)
    RecyclerView userFriendsRecyclerView;

    private List<Map<String, Object>> friends = new ArrayList<>();

    private UserFriendsAdapter userFriendsAdapter;

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

        Map<String,Object> friend1 = new HashMap<>();
        friend1.put("name", "Dibil");
        friend1.put("breed", "Fuck you");

        Map<String,Object> friend2 = new HashMap<>();
        friend2.put("name", "Dibil2");
        friend2.put("breed", "Fuck you2");

        Map<String,Object> friend4 = new HashMap<>();
        friend4.put("name", "Dibil2");
        friend4.put("breed", "Fuck you2");

        Map<String,Object> friend5 = new HashMap<>();
        friend5.put("name", "Dibil2");
        friend5.put("breed", "Fuck you2");

        Map<String,Object> friend6 = new HashMap<>();
        friend6.put("name", "Dibil2");
        friend6.put("breed", "Fuck you2");

        Map<String,Object> friend7 = new HashMap<>();
        friend7.put("name", "Dibil2");
        friend7.put("breed", "Fuck you2");

        Map<String,Object> friend8 = new HashMap<>();
        friend8.put("name", "Dibil2");
        friend8.put("breed", "Fuck you2");

        friends.add(friend1);
        friends.add(friend2);
        friends.add(friend4);
        friends.add(friend5);
        friends.add(friend6);
        friends.add(friend7);
        friends.add(friend8);


        userFriendsAdapter = new UserFriendsAdapter(friends);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        userFriendsRecyclerView.setLayoutManager(layoutManager);

        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));

        userFriendsRecyclerView.setItemAnimator(animator);
        userFriendsRecyclerView.setAdapter(userFriendsAdapter);
        userFriendsAdapter.notifyDataSetChanged();

        return view;
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
