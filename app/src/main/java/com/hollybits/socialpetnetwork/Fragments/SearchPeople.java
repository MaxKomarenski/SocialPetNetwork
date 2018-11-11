package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.adapters.SpinnerAnimalAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

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
}
