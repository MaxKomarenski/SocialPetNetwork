package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.helper.GlideApp;
import com.hollybits.socialpetnetwork.helper.PhotoManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoPage extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.to_gallery_in_photo_page)
    Button toGalleryButton;

    @BindView(R.id.photo_text_in_photo_page)
    TextView photoTextView;

    @BindView(R.id.image_in_photo_page)
    ImageView photoPageImage;

    private OnFragmentInteractionListener mListener;

    public PhotoPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoPage.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoPage newInstance(String param1, String param2) {
        PhotoPage fragment = new PhotoPage();
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
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);
        ButterKnife.bind(this, view);

        Typeface mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        photoTextView.setTypeface(mainFont);

        toGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(UsersGallery.class);
            }
        });

        Long id = Paper.book().read("Current choice");

        System.err.println("id------------------>    " + id);

        byte[] photoBytes = Paper.book(PhotoManager.PAPER_BOOK_NAME).read(PhotoManager.REGULAR_PHOTO+id);
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0,photoBytes.length);
        loadBitmapToImageView(photoPageImage, bitmap);

        return view;
    }

    private void loadBitmapToImageView(ImageView  imageView, Bitmap bitmap){
        GlideApp.with(this)
                .load(bitmap)
                .placeholder(R.drawable.test_photo)
                .into(imageView);
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
