package com.hollybits.socialpetnetwork.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;


public class FriendAccount extends Fragment {

    @BindViews({
            R.id.breed_word_text_view,
            R.id.address_word_text_view,
            R.id.age_word_text_view,
            R.id.weight_word_text_view,
            R.id.attitude_word_text_view
    })
    List<TextView> words;

    @BindViews({
            R.id.name_of_pet_text_view,
            R.id.sex_of_pet_text_view,
            R.id.name_of_breed_text_view,
            R.id.place_of_user_text_view,
            R.id.number_of_age_word_text_view,
            R.id.amount_of_weight_text_view,
            R.id.attitude_state_text_view,
            R.id.owner_name_and_surname_in_expansion_panel,
            R.id.owner_phone_in_expansion_panel,
            R.id.owner_email_in_expansion_panel
    })
    List<TextView> allChangedInformation;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendAccount() {
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
        View view = inflater.inflate(R.layout.fragment_friend_account, container, false);
        ButterKnife.bind(this, view);



        return view;
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendAccount.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendAccount newInstance(String param1, String param2) {
        FriendAccount fragment = new FriendAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
