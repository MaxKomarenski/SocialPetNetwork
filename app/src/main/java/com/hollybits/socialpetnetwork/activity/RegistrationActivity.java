package com.hollybits.socialpetnetwork.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.adapters.BreedAdapter;
import com.hollybits.socialpetnetwork.enums.PetType;
import com.hollybits.socialpetnetwork.models.Breed;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    @BindView(R.id.email_and_password_linear_layout)
    LinearLayout emailAndPasswordLinearLayout;

    @BindView(R.id.information_scroll_view)
    ScrollView informationScrollView;

    @BindView(R.id.access_button_in_registration)
    Button accessButtonInRegistration;

    @BindView(R.id.registration_button)
    Button registrationButton;

    @BindView(R.id.choose_photo_in_registration)
    CircleImageView chosenPhoto;

    @BindView(R.id.type_edit_text_in_registration)
    AutoCompleteTextView petTypeInput;

    @BindView(R.id.breed_edit_text_in_registration)
    AutoCompleteTextView breedInput;

    private BreedAdapter breedAdapter;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;



    private List<Breed> allBreadsForSelectedType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        attachListeners();
        loadTypesInfo();
        chosenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        accessButtonInRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationScrollView.setVisibility(View.GONE);
                emailAndPasswordLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You are registrated", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

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


    private void attachListeners(){
        petTypeInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                loadBreedsForSelectedType(PetType.valueOf(petTypeInput.getText().toString()));
            }
        });

        breedInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SELECTED BREED", breedInput.getText().toString());
            }
        });
    }

    private void loadTypesInfo(){

        ArrayAdapter<PetType> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,PetType.values());
        petTypeInput.setAdapter(adapter);
    }

    private void loadBreedsForSelectedType(PetType petType){
        Log.d("loadBreed for: ", petType.name());
        MainActivity.getServerRequests().getBreedsForType(petType).enqueue(new Callback<List<Breed>>() {
            @Override
            public void onResponse(Call<List<Breed>> call, Response<List<Breed>> response) {
                if(response.body() != null){
                    allBreadsForSelectedType = response.body();
                    breedAdapter = new BreedAdapter(RegistrationActivity.this, allBreadsForSelectedType);
                    breedInput.setAdapter(breedAdapter);

                }else {
                    Log.d("getBreedsForType: ", "body null");
                }
            }

            @Override
            public void onFailure(Call<List<Breed>> call, Throwable t) {
                Log.d("getBreedsForType: ", "Failure");
            }
        });
    }



}
