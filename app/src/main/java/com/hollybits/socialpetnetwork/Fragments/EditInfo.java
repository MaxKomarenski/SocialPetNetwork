package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.LoginActivity;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.activity.RegistrationActivity;
import com.hollybits.socialpetnetwork.adapters.AutoCompleteCountryAdapter;
import com.hollybits.socialpetnetwork.adapters.BreedAdapter;
import com.hollybits.socialpetnetwork.enums.Attitude;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.forms.EditForm;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.Breed;
import com.hollybits.socialpetnetwork.models.City;
import com.hollybits.socialpetnetwork.models.Country;
import com.hollybits.socialpetnetwork.models.Pet;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.Weight;
import com.hollybits.socialpetnetwork.validation.RegistrationValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditInfo extends Fragment {

    @BindViews({R.id.name_text_in_edit_page, R.id.breed_text_in_edit_page,
                R.id.city_text_in_edit_page, R.id.country_text_in_edit_page, R.id.owner_name_text_in_edit_page,
                R.id.phone_text_in_edit_page, R.id.weight_text_in_edit_page,
                R.id.age_text_in_edit_page})
    List<TextView> topics;

    @BindView(R.id.edit_profile_text_in_edit_page)
    TextView editProfileText;

    @BindView(R.id.change_profile_photo_text_in_edit_page)
    TextView changeProfilePhotoText;

    @BindView(R.id.cancel_button_in_edit)
    Button cancelButton;

    @BindView(R.id.user_photo_in_edit_fragment)
    CircleImageView userPhoto;

    @BindView(R.id.change_attitude_linear_layout)
    LinearLayout changeAttitudeLinearLayout;

    @BindView(R.id.attitude_text_view)
    TextView attitudeTextView;

    @BindView(R.id.edit_image_view)
    ImageView editPen;

    @BindView(R.id.done_button)
    Button doneButton;
//--------------------------------------------------------
    @BindView(R.id.pet_name_in_edit_page)
    EditText petNameEditText;

    @BindView(R.id.breed_edit_text_in_edit)
    AutoCompleteTextView breedAutocomplete;

    @BindView(R.id.city_edit_text_in_edit_page)
    EditText cityEditText;

    @BindView(R.id.country_auto_complete_in_edit)
    AutoCompleteTextView countryAutocomplete;

    @BindView(R.id.owner_name_edit_text_in_edit_page)
    EditText ownerName;

    @BindView(R.id.weight_edit_text_in_edit_page)
    EditText weightEditText;

    @BindView(R.id.age_edit_text_in_edit_page)
    EditText ageEditText;

    @BindView(R.id.phone_edit_text_in_edit_page)
    EditText phoneEditText;

    private PhotoManager photoManager;

    private BreedAdapter breedAdapter;
    private AutoCompleteCountryAdapter autoCompleteCountryAdapter;
    private List<Country> countries = new ArrayList<>();
    private List<Breed> allBreadsForSelectedType;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int attitudePosition = 0;

    private OnFragmentInteractionListener mListener;

    public EditInfo() {
        photoManager = new PhotoManager(this);
    }


    public static EditInfo newInstance(String param1, String param2) {
        EditInfo fragment = new EditInfo();
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
        View view = inflater.inflate(R.layout.fragment_edit_info, container, false);
        ButterKnife.bind(this, view);

        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Pet currentPet = currentUser.getPets().get(0);

        loadBreedsForSelectedType(currentPet.getBreed().getType());
        loadCountriesList();

        changeTextInEditText(currentUser, currentPet);

        Typeface avenirNextCyr_regular = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Regular.ttf");
        Typeface infoFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Demi.ttf");

        attitudeTextView.setTypeface(infoFont);

        changeFontForAllWordInList(topics, infoFont, "#8edaff");
        editProfileText.setTypeface(infoFont);
        changeProfilePhotoText.setTypeface(infoFont);
        cancelButton.setTypeface(avenirNextCyr_regular);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Account.class);
            }
        });

        doneButton.setTypeface(avenirNextCyr_regular);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditForm editForm = new EditForm();

                String pName = petNameEditText.getText().toString();
                editForm.setPetName(pName);

                Breed b = getChosenBreed(breedAutocomplete.getText().toString());
                editForm.setBreed(b);

                Country country = getChosenCountry(countryAutocomplete.getText().toString());
                City city = new City(cityEditText.getText().toString(), country);
                editForm.setCity(city);

                String owName = ownerName.getText().toString();
                editForm.setOwnerName(owName); //

                String phone = phoneEditText.getText().toString();
                editForm.setPhone(phone); //

                Long age = Long.parseLong(ageEditText.getText().toString());
                editForm.setAge(age);

                Double weight = Double.parseDouble(weightEditText.getText().toString());
                editForm.setWeight(weight);

                String att = attitudeTextView.getText().toString();
                Attitude at;

                if (att.equals("friendly to everyone")){
                    at = Attitude.GOODWITHALL;
                } else if (att.equals("friendly to female")){
                    at = Attitude.GOODWITHFEMALE;
                } else if (att.equals("friendly to male")){
                    at = Attitude.GOODWITHMALE;
                }else {
                    at = Attitude.BAD;
                }

                editForm.setAttitude(at);

                User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
                java.util.Map<String, String> authorisationCode = new HashMap<>();
                authorisationCode.put("authorization", currentUser.getAuthorizationCode());


                MainActivity.getServerRequests().sendEditableInfo(authorisationCode,
                        editForm, currentUser.getId(), currentPet.getId()).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        currentUser.setName(owName);
                        currentUser.setCity(city);
                        currentUser.setPhone(phone);

                        currentPet.setAge(age);
                        currentPet.setBreed(b);

                        currentPet.setName(pName);

                        Weight w = currentPet.getWeight();
                        w.setMass(weight);
                        currentPet.setWeight(w);

                        currentPet.setAttitude(at);

                        List<Pet> pets = new ArrayList<>();
                        pets.add(currentPet);
                        currentUser.setPets(pets);

                        Paper.book().write(MainActivity.CURRENTUSER, currentUser);

                        FragmentDispatcher.launchFragment(Account.class);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
            }
        });

        changeAttitude(currentPet);

        photoManager.loadUsersMainPhoto(userPhoto);

        return view;
    }

    void changeAttitude(Pet currentPet){
        String[] attitudes = {"friendly to everyone", "friendly to female", "friendly to male", "unfriendly"};
        int[] strokes = {R.drawable.attitude_edit_background_green,
                R.drawable.attitude_edit_background_yellow,
                R.drawable.attitude_edit_background_yellow,
                R.drawable.attitude_edit_background_red};
        int[] colorFilters = {Color.argb(255,0,135,68),
                Color.argb(255, 255,167,0),
                Color.argb(255, 255,167,0),
                Color.argb(255, 214,45,32)};

        attitudePosition = getAttitudePosition(currentPet);
        attitudeTextView.setText(attitudes[attitudePosition]);
        editPen.setColorFilter(colorFilters[attitudePosition]);
        changeAttitudeLinearLayout.setBackgroundResource(strokes[attitudePosition]);

        changeAttitudeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(attitudePosition == 3){
                    attitudePosition = -1;
                }

                attitudePosition++;
                changeAttitudeLinearLayout.setBackgroundResource(strokes[attitudePosition]);
                attitudeTextView.setText(attitudes[attitudePosition]);
                editPen.setColorFilter(colorFilters[attitudePosition]);

            }
        });
    }

    void changeTextInEditText(User currentUser, Pet currentPet){
        breedAutocomplete.setText(currentPet.getBreed().getName());
        countryAutocomplete.setText(currentUser.getCity().getCountry().getName());

        EditText[] allEditTexts = {petNameEditText, cityEditText, ownerName, weightEditText, ageEditText, phoneEditText};
        String[] info = {currentPet.getName(),
                currentUser.getCity().getName(),
                currentUser.getName(),
                currentPet.getWeight().getMass().toString(),
                currentPet.getAge().toString(),
                currentUser.getPhone()};

        for (int i = 0; i < allEditTexts.length; i++) {
            allEditTexts[i].setText(info[i]);
        }
    }

    private void loadBreedsForSelectedType(PetType petType){
        Log.d("loadBreed for: ", petType.name());
        MainActivity.getServerRequests().getBreedsForType(petType).enqueue(new Callback<List<Breed>>() {
            @Override
            public void onResponse(Call<List<Breed>> call, Response<List<Breed>> response) {
                if(response.body() != null){
                    allBreadsForSelectedType = response.body();
                    breedAdapter = new BreedAdapter(getContext(), allBreadsForSelectedType);
                    breedAutocomplete.setAdapter(breedAdapter);

                }else {
                    Log.d("getBreedsForType: ", "body null");
                }
            }

            @Override
            public void onFailure(Call<List<Breed>> call, Throwable t) {
                Log.d("getBreedsForType: ", t.getMessage());
            }
        });
    }

    private Breed getChosenBreed(String nameOfChosenBreed){
        if(allBreadsForSelectedType != null)
            for (Breed b: allBreadsForSelectedType){
                if(b.getName().equalsIgnoreCase(nameOfChosenBreed)){
                    return b;
                }
            }

        return new Breed(nameOfChosenBreed);
    }

    private Country getChosenCountry(String nameOfChosenCountry){
        if(countries != null)
            for (Country c: countries) {
                if(c.getName().equalsIgnoreCase(nameOfChosenCountry)){
                    return c;
                }
            }

        return new Country(nameOfChosenCountry);
    }

    private void loadCountriesList(){
        MainActivity.getServerRequests().getAllCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(@NonNull Call<List<Country>> call, @NonNull Response<List<Country>> response) {
                if(response.body() != null){
                    countries.clear();
                    countries.addAll(response.body());
                    autoCompleteCountryAdapter = new AutoCompleteCountryAdapter(getContext(), countries);
                    countryAutocomplete.setAdapter(autoCompleteCountryAdapter);
                    autoCompleteCountryAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {

            }
        });
    }

    void changeFontForAllWordInList(List<TextView> words, Typeface font, String color) {
        for (TextView textView :
                words) {
            textView.setTypeface(font);
            textView.setTextColor(Color.parseColor(color));
        }
    }

    int getAttitudePosition(Pet currentPet){
        Attitude attitude = currentPet.getAttitude();

        if (attitude == Attitude.GOODWITHALL){
            return 0;
        }else if(attitude == Attitude.GOODWITHFEMALE){
            return 1;
        }else if(attitude == Attitude.GOODWITHMALE) {
            return 2;
        }else {
            return 3;
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
