package com.hollybits.socialpetnetwork.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.PhotoGridAdapter;
import com.hollybits.socialpetnetwork.enums.GalleryMode;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UsersGallery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UsersGallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersGallery extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.gallery_text_in_gallery_page)
    TextView galleryText;

    @BindView(R.id.number_of_photo_in_gallery)
    TextView numberOfPhoto;

    @BindView(R.id.number_of_friends_in_gallery)
    TextView numberOfFriends;

    @BindView(R.id.to_profile_in_gallery_page)
    Button toProfileButton;

    @BindView(R.id.photo_grid_view)
    GridView photoGridView;

    @BindView(R.id.pet_breed)
    TextView petBreedText;

    @BindView(R.id.pet_name)
    TextView petNameText;

    @BindView(R.id.uploadPhoto)
    TextView uploadPhoto;

    @BindView(R.id.gallery_avatar)
    CircleImageView avatar;

    private UserInfo userInfo;
    private Long target;
    private GalleryMode mode;


    private PhotoGridAdapter photoGridAdapter;

    private OnFragmentInteractionListener mListener;

    private static final int PICK_IMAGE = 200;

    private Uri imageUri;

    public UsersGallery() {
        // Required empty public constructor
    }

    public static UsersGallery newInstance(String param1, String param2) {
        UsersGallery fragment = new UsersGallery();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, view);

        Typeface mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");

        numberOfFriends.setTypeface(mainFont);
        numberOfPhoto.setTypeface(mainFont);

        Typeface petNameFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/HelveticaNeueCyr-Medium.ttf");
        Typeface petBreedFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Regular.ttf");

        petBreedText.setTypeface(petBreedFont);
        uploadPhoto.setTypeface(petBreedFont);
        petNameText.setTypeface(petNameFont);
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        java.util.Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        mode = Paper.book().read(MainActivity.GALLERY_MODE);
        PhotoManager photoManager = new PhotoManager(this);
        if(mode == GalleryMode.USERS_MODE) {

            List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);

            if(friends == null){
                MainActivity.getServerRequests().getNumberOfFriendsOfAnotherUser(authorisationCode, currentUser.getId()).enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        String countOfFriends = response.body() + " friends";
                        numberOfFriends.setText(countOfFriends);
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });

                photoManager.loadFriendsMainPhoto(avatar, currentUser.getId());
                uploadPhoto.setVisibility(View.GONE);

            } else {
                String countOfFriends = friends.size() + " friends";
                numberOfFriends.setText(countOfFriends);
            }


            target = currentUser.getId();
            petBreedText.setText(currentUser.getPets().get(0).getBreed().getName());
            petNameText.setText(currentUser.getPets().get(0).getName());
            photoManager.loadUsersMainPhoto(avatar);
        }
        else {
            userInfo = Paper.book().read(MainActivity.CURRENT_CHOICE);
            target = userInfo.getId();
            petBreedText.setText(userInfo.getPets().get(0).getBreed().getName());
            petNameText.setText(userInfo.getPets().get(0).getName());


            MainActivity.getServerRequests().getNumberOfFriendsOfAnotherUser(authorisationCode, target).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    String countOfFriends = response.body() + " friends";
                    numberOfFriends.setText(countOfFriends);
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });

            photoManager.loadFriendsMainPhoto(avatar, userInfo.getId());
            uploadPhoto.setVisibility(View.GONE);

        }


        galleryText.setTypeface(mainFont);
        getIdsOfUserPhoto();
        listeners();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String mediaPath = cursor.getString(columnIndex);
            File file = new File(mediaPath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("img", file.getName(), requestBody);

            Paper.book().write("PATH_TO_PHOTO", mediaPath);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());


            System.err.println("ATTENTION");
            System.err.println(mediaPath);


            User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
            java.util.Map<String, String> authorisationCode = new HashMap<>();
            authorisationCode.put("authorization", currentUser.getAuthorizationCode());

            MainActivity.getServerRequests().addNewPhoto(authorisationCode, fileToUpload, currentUser.getId()).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {

                    FragmentDispatcher.launchFragment(UsersGallery.class);
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {

                }
            });
        }
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void getIdsOfUserPhoto(){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        MainActivity.getServerRequests().getIdsOfUserPhoto(authorisationCode, currentUser.getId(), target).enqueue(new Callback<List<Long>>() {
            @Override
            public void onResponse(Call<List<Long>> call, Response<List<Long>> response) {

                photoGridAdapter = new PhotoGridAdapter(UsersGallery.this.getContext(), response.body(), UsersGallery.this, mode);

                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/3;

                photoGridView.setColumnWidth(imageWidth);

                photoGridView.setAdapter(photoGridAdapter);

                String amountOfPhotos = photoGridAdapter.getCount() + " photos";
                numberOfPhoto.setText(amountOfPhotos);
            }

            @Override
            public void onFailure(Call<List<Long>> call, Throwable t) {

            }
        });
    }

    private void listeners(){
        toProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Account.class);
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    openGallery();
                }catch (Exception e){
                    Toast.makeText(UsersGallery.this.getActivity(), "SORRY IMAGE IS TOO LARGE", Toast.LENGTH_LONG).show();
                }
            }
        });
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
