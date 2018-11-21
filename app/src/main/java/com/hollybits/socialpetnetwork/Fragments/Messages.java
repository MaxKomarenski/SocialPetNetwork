package com.hollybits.socialpetnetwork.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.activity.SettingsActivity;
import com.hollybits.socialpetnetwork.adapters.ContactAdapter;
import com.hollybits.socialpetnetwork.models.Contact;
import com.hollybits.socialpetnetwork.models.User;

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


public class Messages extends Fragment {

    @BindView(R.id.settings_account_img_button_in_messages)
    ImageButton settings;

    @BindView(R.id.open_navigation_drawer_image_button_in_messages)
    ImageButton openDrawerButton;

    @BindView(R.id.contacts_recycler_view)
    RecyclerView contactsRecyclerView;

    @BindView(R.id.chat_text_in_message_page)
    TextView chatText;

    private List<Contact> contacts;
    private ContactAdapter contactAdapter;
    private Typeface nameFont;
    private Typeface anotherFont;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DrawerLayout drawer;

    @BindView(R.id.no_res_layout)
    LinearLayout noResultsLayout;
    @BindView(R.id.no_friends_text)
    TextView noFriendsText;
    @BindView(R.id.start_communicating_text)
    TextView startComunicatingText;

    @BindView(R.id.go_to_map)
    ImageButton goToMap;

    private OnFragmentInteractionListener mListener;

    public Messages() {

    }


    public static Messages newInstance(String param1, String param2) {
        Messages fragment = new Messages();
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

        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, view);
        noResultsLayout.setVisibility(View.GONE);

        nameFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        anotherFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/HelveticaNeueCyr.ttf");


        noFriendsText.setTypeface(nameFont);
        startComunicatingText.setTypeface(nameFont);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(com.hollybits.socialpetnetwork.Fragments.Map.class);
            }
        });


        chatText.setTypeface(nameFont);

        getContacts();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        contactsRecyclerView.setLayoutManager(layoutManager);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        contactsRecyclerView.setItemAnimator(animator);

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

        return view;
    }

    private void getContacts() {
        contacts = Paper.book().read(MainActivity.CONTACT_LIST);
        if (contacts == null) {
            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            Map<String, String> authorisationCode = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());
            MainActivity.getServerRequests().getAllContactsOfCurrentUser(authorisationCode, MainActivity.getCurrentUser().getId()).enqueue(new Callback<List<Contact>>() {
                @Override
                public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                    contacts = response.body();
                    Paper.book().write(MainActivity.CONTACT_LIST, contacts);
                    contactAdapter = new ContactAdapter(contacts, nameFont, anotherFont, Messages.this);
                    contactsRecyclerView.setAdapter(contactAdapter);
                    if(response.body().size()==0){
                        noResultsLayout.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onFailure(Call<List<Contact>> call, Throwable t) {

                }
            });
        } else {
            if(contacts.size()==0){
                noResultsLayout.setVisibility(View.VISIBLE);
            }
            contactAdapter = new ContactAdapter(contacts, nameFont, anotherFont, Messages.this);
            contactsRecyclerView.setAdapter(contactAdapter);
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
