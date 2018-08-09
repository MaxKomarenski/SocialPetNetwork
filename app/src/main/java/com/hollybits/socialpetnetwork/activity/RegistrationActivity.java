package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.adapters.AutoCompleteCountryAdapter;
import com.hollybits.socialpetnetwork.enums.Attitude;
import com.hollybits.socialpetnetwork.enums.MassUnit;
import com.hollybits.socialpetnetwork.enums.Sex;
import com.hollybits.socialpetnetwork.forms.RegistrationForm;
import com.hollybits.socialpetnetwork.models.City;
import com.hollybits.socialpetnetwork.models.Country;

import java.util.ArrayList;
import java.util.List;
import com.hollybits.socialpetnetwork.adapters.BreedAdapter;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.models.Breed;
import com.hollybits.socialpetnetwork.models.Pet;
import com.hollybits.socialpetnetwork.models.Weight;
import com.hollybits.socialpetnetwork.validation.RegistrationValidator;
import com.hollybits.socialpetnetwork.validation.Validator;


import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import lib.kingja.switchbutton.SwitchMultiButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.email_and_password_linear_layout)
    LinearLayout emailAndPasswordLinearLayout;

    @BindView(R.id.information_scroll_view)
    ScrollView informationScrollView;

    @BindView(R.id.choose_icon_text_view)
    TextView chooseIconText;

    @BindView(R.id.now_some_info_text_view)
    TextView textView1;

    @BindView(R.id.about_you_text_view)
    TextView textView2;

    @BindView(R.id.access_button_in_registration)
    Button accessButtonInRegistration;

    @BindView(R.id.registration_button)
    ImageButton registrationButton;

    @BindView(R.id.choose_photo_in_registration)
    CircleImageView chosenPhoto;

    @BindView(R.id.breed_edit_text_in_registration)
    AutoCompleteTextView breedInput;

    @BindView(R.id.country_auto_complete_text_in_registration)
    AutoCompleteTextView chosenCountry;

    @BindView(R.id.name_edit_text_in_registration)
    EditText nameOfPet;

    @BindView(R.id.age_edit_text_in_registration)
    EditText ageOfPet;

    @BindView(R.id.tag_number_edit_text_in_registration)
    EditText tagNumberOfPet;

    @BindView(R.id.weight_edit_text_in_registration)
    EditText weightOfPet;

    @BindView(R.id.owner_name_edit_text_in_registration)
    EditText ownerName;

    @BindView(R.id.owner_surname_edit_text_in_registration)
    EditText ownerSurname;

    @BindView(R.id.phone_number_edit_text_in_registration)
    EditText phoneNumber;

    @BindView(R.id.city_edit_text_in_registration)
    EditText cityEdit;

    @BindView(R.id.email_edit_text_in_registration)
    EditText email;

    @BindView(R.id.password_edit_text_in_registration)
    EditText password;

    @BindViews({R.id.rat_pet_type_image_button, R.id.cat_pet_type_image_button,
            R.id.bird_pet_type_image_button, R.id.dog_pet_type_image_button,
            R.id.rabbit_rat_pet_type_image_button, R.id.raccoon_pet_type_image_button})
    List<ImageButton> listOfPetTypes;

    @BindView(R.id.sex_switch_compat_in_registration_activity)
    SwitchCompat sexOfPet;

    @BindView(R.id.weight_switch_compat_in_registration_activity)
    SwitchCompat massUnitSwitchCompat;

    @BindView(R.id.attitude_switch_multi_button)
    SwitchMultiButton attitudeSwitchMultiButton;

    private BreedAdapter breedAdapter;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;


    private AutoCompleteCountryAdapter autoCompleteCountryAdapter;
    private List<Country> countries = new ArrayList<>();
    private List<Breed> allBreadsForSelectedType;
    private Pet newPet;
    private PetType petType;
    private PetType[] allPetTypes;
    private RegistrationActivity instance;

    private Validator validator;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        instance = this;
        validator  = new RegistrationValidator();
        attachListeners();
        loadCountriesList();

        Typeface mainFont = Typeface.createFromAsset(this.getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        chooseIconText.setTypeface(mainFont);
        textView1.setTypeface(mainFont);
        textView2.setTypeface(mainFont);
        accessButtonInRegistration.setTypeface(mainFont);
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

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            chosenPhoto.setImageURI(imageUri);
        }
    }

    private void changeColorOfPetTypeButtons(final PetType[] allPetTypes){
        for (int i = 0; i < allPetTypes.length; i++){
            final int j = i;
            listOfPetTypes.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    petType = allPetTypes[j];
                    listOfPetTypes.get(j).setBackgroundResource(R.drawable.circle_background_active);

                    for (int m = 0; m < allPetTypes.length; m++){
                        if(m != j){
                            listOfPetTypes.get(m).setBackgroundResource(R.drawable.circle_background);
                        }
                    }

                    loadBreedsForSelectedType(petType);

                }
            });
        }
    }


    private void attachListeners(){
        allPetTypes = new PetType[]{PetType.RAT, PetType.CAT, PetType.BIRD, PetType.DOG, PetType.RABBIT, PetType.RACCOON};

        changeColorOfPetTypeButtons(allPetTypes);

        breedInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SELECTED BREED", breedInput.getText().toString());
            }
        });
        chosenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        accessButtonInRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validator.validate(instance, 0)){
                    return;
                }

                Breed breedOfPet = getChosenBreed(breedInput.getText().toString());

                Attitude attitude;
                if (attitudeSwitchMultiButton.getSelectedTab() == 0)
                    attitude = Attitude.GOODWITHALL;
                else if(attitudeSwitchMultiButton.getSelectedTab() == 1)
                    attitude = Attitude.GOODWITHMALE;
                else if(attitudeSwitchMultiButton.getSelectedTab() == 2)
                    attitude = Attitude.GOODWITHFEMALE;
                else
                    attitude = Attitude.BAD;

                Sex s;
                if (sexOfPet.isChecked()) {
                    s = Sex.FEMALE;
                } else {
                    s = Sex.MALE;
                }

                MassUnit massUnit;

                if (massUnitSwitchCompat.isChecked()){
                    massUnit = MassUnit.POUNDS;
                }else {
                    massUnit = MassUnit.KG;
                }

                Weight w = new Weight(Double.parseDouble(weightOfPet.getText().toString()), massUnit);

                newPet = new Pet(nameOfPet.getText().toString(),
                        breedOfPet,
                        Long.parseLong(ageOfPet.getText().toString()),
                        s,
                        tagNumberOfPet.getText().toString(),
                        w,
                        attitude);

                informationScrollView.setVisibility(View.GONE);
                emailAndPasswordLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validator.validate(instance, 1)){
                    return;
                }

                Toast toast = Toast.makeText(getApplicationContext(),
                        "You are registrated", Toast.LENGTH_SHORT);
                toast.show();

                Country country = getChosenCountry(chosenCountry.getText().toString());
                City city = new City(cityEdit.getText().toString() , country);

                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();


                String encodedPassword = Base64.encodeToString(passwordStr.getBytes(), Base64.NO_WRAP);
                String encodedEmail = Base64.encodeToString(emailStr.getBytes(), Base64.NO_WRAP);


                final RegistrationForm registrationForm = new RegistrationForm(ownerName.getText().toString(),
                                                    ownerSurname.getText().toString(),
                                                    phoneNumber.getText().toString(),
                                                    city,
                                                    encodedEmail,
                                                    encodedPassword,
                                                    newPet);

                MainActivity.getServerRequests().sendRegistrationFormToTheServer(registrationForm).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        System.err.println("Response from registration -->   " + response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            }
        });
    }

    private void loadCountriesList(){
        MainActivity.getServerRequests().getAllCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(@NonNull Call<List<Country>> call, @NonNull Response<List<Country>> response) {
                if(response.body() != null){
                    countries.clear();
                    countries.addAll(response.body());
                    ((RegistrationValidator) validator).setCountries(countries);

                    autoCompleteCountryAdapter = new AutoCompleteCountryAdapter(RegistrationActivity.this,countries);
                    chosenCountry.setAdapter(autoCompleteCountryAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {

            }
        });
    }

    private void loadBreedsForSelectedType(PetType petType){
        Log.d("loadBreed for: ", petType.name());
        MainActivity.getServerRequests().getBreedsForType(petType).enqueue(new Callback<List<Breed>>() {
            @Override
            public void onResponse(Call<List<Breed>> call, Response<List<Breed>> response) {
                if(response.body() != null){
                    allBreadsForSelectedType = response.body();
                    breedAdapter = new BreedAdapter(RegistrationActivity.this, allBreadsForSelectedType);
                    ((RegistrationValidator) validator).setBreeds(allBreadsForSelectedType);
                    breedInput.setAdapter(breedAdapter);

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



}
