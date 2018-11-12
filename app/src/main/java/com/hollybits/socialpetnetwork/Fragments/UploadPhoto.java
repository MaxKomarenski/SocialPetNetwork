package com.hollybits.socialpetnetwork.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.LoginActivity;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.GlideApp;
import com.hollybits.socialpetnetwork.models.User;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
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
 * {@link UploadPhoto.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadPhoto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadPhoto extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE = 200;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @BindView(R.id.photo_place)
    ImageView image;

    @BindView(R.id.uploadPhoto_button)
    Button upload;

    @BindView(R.id.cancel_button_in_upload_photo)
    Button cancel;

    @BindView(R.id.photo_caption)
    EditText editText;

    private ProgressDialog progressDialog;
    private OnFragmentInteractionListener mListener;

    public UploadPhoto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UploadPhoto.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadPhoto newInstance(String param1, String param2) {
        UploadPhoto fragment = new UploadPhoto();
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
        View view = inflater.inflate(R.layout.fragment_upload_photo, container, false);
        ButterKnife.bind(this, view);

        progressDialog = new ProgressDialog(this.getContext(), R.style.AppTheme_Dark_Dialog);

        Uri imageUri;
        imageUri = Paper.book().read("ImageToLoad");
        if (imageUri == null) {
            FragmentDispatcher.launchFragment(UsersGallery.class);
            return null;
        }
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(imageUri, filePathColumn, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String mediaPath = cursor.getString(columnIndex);
        File file = new File(mediaPath);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        image.setMinimumWidth(width);


        GlideApp.with(this)
                .load(file)
                .placeholder(R.drawable.test_photo)
                .override(width, height)
                .into(image);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPhoto(imageUri);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(UsersGallery.class);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Typeface mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        cancel.setTypeface(mainFont);
        upload.setTypeface(mainFont);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        image.setImageResource(R.drawable.test_photo);
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

    public void loadPhoto(Uri imageUri) {
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

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

        System.err.println("ATTENTION");
        System.err.println(mediaPath);


        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        java.util.Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        String caption = editText.getText().toString();

        MainActivity.getServerRequests().addNewPhoto(authorisationCode, fileToUpload, caption, currentUser.getId()).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                progressDialog.dismiss();
                FragmentDispatcher.launchFragment(UsersGallery.class);
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {

            }
        });
    }

}
