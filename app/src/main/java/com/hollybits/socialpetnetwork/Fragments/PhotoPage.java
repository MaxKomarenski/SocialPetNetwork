package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.CommentAdapter;
import com.hollybits.socialpetnetwork.adapters.FriendshipRequestAdapter;
import com.hollybits.socialpetnetwork.helper.GlideApp;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.Comment;
import com.hollybits.socialpetnetwork.models.User;

import java.util.ArrayList;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoPage extends Fragment{

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

    @BindView(R.id.commentRecyclerViewInPhotoPage)
    RecyclerView commentRecyclerView;

    private CommentAdapter commentAdapter;
    private OnFragmentInteractionListener mListener;
    private List<Comment> comments;

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

        comments = new ArrayList<>();

        toGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(UsersGallery.class);
            }
        });

        Long id = Paper.book().read("Current choice");

        byte[] photoBytes = Paper.book(PhotoManager.PAPER_BOOK_NAME).read(PhotoManager.REGULAR_PHOTO+id);
        Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0,photoBytes.length);
        loadBitmapToImageView(photoPageImage, bitmap);

        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());

        MainActivity.getServerRequests().getAllCommentOfCurrentPhoto(authorisationCode, id).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {

                if (response.body() != null){
                    comments.addAll(response.body());
                    commentAdapter = new CommentAdapter(comments);
                    commentRecyclerView.setAdapter(commentAdapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PhotoPage.this.getContext());
                    commentRecyclerView.setLayoutManager(layoutManager);
                    SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
                    commentRecyclerView.setItemAnimator(animator);
                    commentAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });

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
