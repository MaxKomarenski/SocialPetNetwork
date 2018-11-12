package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.PeopleSearchAdapter;
import com.hollybits.socialpetnetwork.adapters.SpinnerAnimalAdapter;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.SearchForm;
import com.hollybits.socialpetnetwork.models.User;

import java.util.*;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchPeople extends Fragment {

    @BindViews({R.id.pet_name_text_view,
            R.id.owner_name_text_view,
            R.id.animal_text_view,
            R.id.breed_text_view})
    List<TextView> texts;

    @BindView(R.id.search_text)
    TextView searchTextView;

    @BindView(R.id.pet_name_edit_text_in_search_people)
    EditText petNameEditText;

    @BindView(R.id.owner_name_edit_text_in_search_people)
    EditText ownerNameEditText;

    @BindView(R.id.breed_autocomplete_in_search_people)
    AutoCompleteTextView breedAutoComplete;

    @BindView(R.id.animal_spinner_in_search_people)
    Spinner animalSpinner;

    @BindView(R.id.search_button_in_search_people)
    Button searchButton;

    @BindView(R.id.back_to_friends_img_button)
    ImageButton backToFriends;


    @BindView(R.id.user_search_recycler_view)
    RecyclerView searchResults;
    Typeface gothic;
    Typeface avenirNextCyr_regular;
    Typeface gothicRegular;

    List<String> animals;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private PeopleSearchAdapter peopleSearchAdapter;

    public SearchPeople() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchPeople.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPeople newInstance(String param1, String param2) {
        SearchPeople fragment = new SearchPeople();
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
        View view = inflater.inflate(R.layout.fragment_search_people, container, false);
        ButterKnife.bind(this, view);

        animals = new ArrayList<>();
        animals.add("Animals");
        animals.add("Dog");
        animals.add("Cat");

        gothic = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        avenirNextCyr_regular = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Regular.ttf");
        gothicRegular = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/CenturyGothicRegular.ttf");

        for (TextView word : texts) {
            word.setTypeface(gothic);
        }
        peopleSearchAdapter = new PeopleSearchAdapter(this, avenirNextCyr_regular, avenirNextCyr_regular);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this.getContext());
        searchResults.setLayoutManager(layoutManager2);
        SlideInUpAnimator animator2 = new SlideInUpAnimator(new OvershootInterpolator(1f));
        searchResults.setItemAnimator(animator2);
        searchResults.setAdapter(peopleSearchAdapter);




        petNameEditText.setTypeface(gothic);
        ownerNameEditText.setTypeface(gothic);
        breedAutoComplete.setTypeface(gothic);

        searchTextView.setTypeface(avenirNextCyr_regular);

        SpinnerAnimalAdapter adapter = new SpinnerAnimalAdapter(this.getContext(), android.R.layout.simple_spinner_item, animals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalSpinner.setAdapter(adapter);

        searchButton.setTypeface(gothicRegular);

        backToFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(UserFriends.class);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        return view;
    }

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
        void onFragmentInteraction(Uri uri);
    }


    public void search() {
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        java.util.Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        SearchForm searchForm = new SearchForm();
        searchForm.setUserCountry(currentUser.getCity().getCountry().getName());
        searchForm.setUserCity(currentUser.getCity().getName());
        if (ownerNameEditText.getText().toString().equals(""))
            searchForm.setOwnersName(null);
        else {
            searchForm.setOwnersName(ownerNameEditText.getText().toString());
        }
        if (breedAutoComplete.getText().toString().equals(""))
            searchForm.setBreed(null);
        else {
            searchForm.setBreed(breedAutoComplete.getText().toString());
        }
        if (petNameEditText.getText().toString().equals(""))
            searchForm.setName(null);
        else {
            searchForm.setName(petNameEditText.getText().toString());
        }
        if (animalSpinner.getSelectedItem().toString().toUpperCase().equals("ANIMALS"))
            searchForm.setPetType(null);
        else {
            searchForm.setPetType(PetType.valueOf(animalSpinner.getSelectedItem().toString().toUpperCase()));
        }
        MainActivity.getServerRequests().search(authorisationCode, searchForm).enqueue(new Callback<List<FriendInfo>>() {
            @Override
            public void onResponse(Call<List<FriendInfo>> call, Response<List<FriendInfo>> response) {
                if (response.body() != null) {
                    Log.d("SEARCH RES", String.valueOf(response.body().size()));
                    peopleSearchAdapter.setFriends(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<FriendInfo>> call, Throwable t) {

            }
        });

    }

}
