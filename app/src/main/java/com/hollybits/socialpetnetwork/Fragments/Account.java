package com.hollybits.socialpetnetwork.Fragments;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.activity.SettingsActivity;
import com.hollybits.socialpetnetwork.enums.GalleryMode;
import com.hollybits.socialpetnetwork.enums.Sex;
import com.hollybits.socialpetnetwork.helper.GlideApp;
import com.hollybits.socialpetnetwork.helper.PermissionManeger;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.Pet;
import com.hollybits.socialpetnetwork.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class Account extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";
    private PhotoManager photoManager;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.user_photo_in_account_fragment)
    CircleImageView userMainPhoto;

    @BindView(R.id.open_navigation_drawer_image_button)
    ImageButton openDrawerButton;

    @BindView(R.id.open_map_from_account)
    ImageButton openMap;

    @BindView(R.id.open_gallery_from_account)
    ImageButton openGallery;

    @BindView(R.id.sexImageView)
    ImageView sexImageView;

    @BindViews({R.id.name_of_pet_text_view,
            R.id.breed_word_text_view, R.id.name_of_breed_text_view, R.id.address_word_text_view,
            R.id.place_of_user_text_view, R.id.age_word_text_view, R.id.number_of_age_word_text_view,
            R.id.weight_word_text_view, R.id.amount_of_weight_text_view, R.id.owner_text_in_account,
            R.id.phone_text_in_account, R.id.email_text_in_account})
    List<TextView> informationAboutPet;

    @BindViews({R.id.owner_name_and_surname_in_expansion_panel,
            R.id.owner_phone_in_expansion_panel,
            R.id.owner_email_in_expansion_panel, R.id.my_profile_text_in_account})
    List<TextView> informationAboutUser;

    @BindViews({R.id.map_text_in_account, R.id.gallery_text_in_account, R.id.store_text_in_account})
    List<TextView> wordUnderButtins;

    @BindView(R.id.settings_account_img_button)
    ImageButton settings;

    private static final int PICK_IMAGE = 200;
    DrawerLayout drawer;
    private Uri imageUri;
    private Typeface infoFont;

    private OnFragmentInteractionListener mListener;

    public Account() {
        // Required empty public constructor
        photoManager = new PhotoManager(this);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Account.
     */
    // TODO: Rename and change types and number of parameters
    public static Account newInstance(String param1, String param2) {
        Account fragment = new Account();
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, view);
        PermissionManeger.checkForPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        PermissionManeger.checkForPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        java.util.Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        photoManager.loadUsersMainPhoto(userMainPhoto);

        Typeface mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        infoFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Demi.ttf");
        changeFontForAllWordInList(informationAboutPet, infoFont);
        changeFontForAllWordInList(wordUnderButtins, mainFont);
        showAllInformationOnTheScreen();
        listeners();

        return view;
    }

    void changeFontForAllWordInList(List<TextView> words, Typeface font) {
        for (TextView textView :
                words) {
            textView.setTypeface(font);
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String mediaPath = cursor.getString(columnIndex);
            File file = new File(mediaPath);

//            int compressionRatio = 4; //1 == originalImage, 2 = 50% compression, 4=25% compress
//            try {
//                Bitmap bitmap = BitmapFactory.decodeFile (file.getPath ());
//                bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(file));
//            }
//            catch (Throwable t) {
//                Log.e("ERROR", "Error compressing file." + t.toString ());
//                t.printStackTrace ();
//            }
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("img", file.getName(), requestBody);


            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            java.util.Map<String, String> authorisationCode = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());

            MainActivity.getServerRequests().updateMainPhoto(authorisationCode, fileToUpload,
                    MainActivity.getCurrentUser().getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    Paper.book(PhotoManager.PAPER_BOOK_NAME).delete(PhotoManager.MAIN_PHOTO);
                    Log.d("UPDATE PHOTO RESPONSE:", String.valueOf(response.code()));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("FAIL:", t.getMessage());
                }
            });



            GlideApp.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.test_photo)
                    .into(userMainPhoto);
            userMainPhoto.setImageURI(imageUri);
        }
    }

    private void listeners() {

        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        });

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write(MainActivity.GALLERY_MODE, GalleryMode.USERS_MODE);
                FragmentDispatcher.launchFragment(UsersGallery.class);
            }
        });

        userMainPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openGallery();
                } catch (Exception e) {
                    Toast.makeText(Account.this.getActivity(), "SORRY IMAGE IS TOO LARGE", Toast.LENGTH_LONG).show();
                }

            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Map.class);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showAllInformationOnTheScreen() {
        Typeface avenirNextCyr_regular = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Regular.ttf");

        //TODO make if user has more than one pet
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Pet currentPet = currentUser.getPets().get(0);


        informationAboutPet.get(0).setText(currentPet.getName()); //name
        informationAboutPet.get(0).setTypeface(infoFont);
        //informationAboutPet.get(1).setText(currentPet.getSex().name().toLowerCase()); //sex
        informationAboutPet.get(2).setText(currentPet.getBreed().getName()); //breed
        informationAboutPet.get(2).setTypeface(avenirNextCyr_regular);
        informationAboutPet.get(4).setText(currentUser.getCity().getName() + ", " + currentUser.getCity().getCountry().getName());// city and country
        informationAboutPet.get(4).setTypeface(avenirNextCyr_regular);
        informationAboutPet.get(6).setText(currentPet.getAge().toString()); // age
        informationAboutPet.get(6).setTypeface(avenirNextCyr_regular);
        informationAboutPet.get(8).setText(currentPet.getWeight().getMass().toString() + " " + currentPet.getWeight().getMassUnit().getName().toLowerCase());
        informationAboutPet.get(8).setTypeface(avenirNextCyr_regular);

        informationAboutUser.get(0).setText(currentUser.getName() + " " + currentUser.getSurname());
        informationAboutUser.get(1).setText(currentUser.getPhone());
        informationAboutUser.get(2).setText(currentUser.getCredentials().email);

        for (TextView text:
             informationAboutUser) {
            text.setTypeface(avenirNextCyr_regular);
        }

        if (currentPet.getSex() == Sex.FEMALE) {
            sexImageView.setImageResource(R.mipmap.female);
        } else {
            sexImageView.setImageResource(R.mipmap.male);
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
