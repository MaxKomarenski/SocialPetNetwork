package com.hollybits.socialpetnetwork.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.adapters.CommentAdapter;
import com.hollybits.socialpetnetwork.adapters.FriendshipRequestAdapter;
import com.hollybits.socialpetnetwork.enums.GalleryMode;
import com.hollybits.socialpetnetwork.helper.BitmapRotator;
import com.hollybits.socialpetnetwork.helper.GlideApp;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.Comment;
import com.hollybits.socialpetnetwork.models.User;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
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

    @BindView(R.id.user_photo_in_photo_page)
    ImageView userPhoto;

    @BindView(R.id.name_in_photo_page)
    TextView nameAndSurnameTextView;

    private int like = 1;
    private  int dislike = 0;
    private int likesCount;
    private int commentsCount;

    private int res_id;

    @BindView(R.id.to_gallery_in_photo_page)
    ImageButton toGalleryButton;

    @BindView(R.id.photo_text_in_photo_page)
    TextView photoTextView;

    @BindView(R.id.image_in_photo_page)
    ImageView photoPageImage;

    @BindView(R.id.commentRecyclerViewInPhotoPage)
    RecyclerView commentRecyclerView;

    @BindView(R.id.writeNewCommentEditText)
    EditText writeNewCommentEditText;

    @BindView(R.id.sendNewCommentButton)
    ImageButton sendNewCommentButton;


    @BindView(R.id.photo_caption)
    TextView caption;

    @BindView(R.id.like_button)
    LinearLayout likeButton;



    @BindView(R.id.likes)
    TextView likes;

    @BindView(R.id.comments)
    TextView commentsText;

    private CommentAdapter commentAdapter;
    private OnFragmentInteractionListener mListener;
    private List<Comment> comments;
    private Long id;
    private String name;

    User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
    Map<String, String> authorisationCode = new HashMap<>();


    public PhotoPage() {
        // Required empty public constructor
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
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



        comments = new ArrayList<>();

        listeners();

        GalleryMode galleryMode = Paper.book().read(MainActivity.GALLERY_MODE);
        PhotoManager photoManager = new PhotoManager(PhotoPage.this);
        id = Paper.book().read("Current choice");
        getComments(id);
        getLikes(id);

        if (galleryMode == GalleryMode.FRIENDS_MODE){
            Long friendID = Paper.book().read(MainActivity.ID_OF_FRIEND);
            byte[] photoBytes = Paper.book(PhotoManager.PAPER_BOOK_NAME_FRIENDS).read(PhotoManager.REGULAR_PHOTO+id);
            Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0,photoBytes.length);
            loadBitmapToImageView(photoPageImage, bitmap);
            name = Paper.book().read("last_chosen_friend_pet");
        }else {
            photoManager.loadUsersMainPhoto(userPhoto);
            byte[] photoBytes = Paper.book(PhotoManager.PAPER_BOOK_NAME).read(PhotoManager.REGULAR_PHOTO+id);
            Bitmap bitmap = BitmapFactory.decodeByteArray(photoBytes, 0,photoBytes.length);
            loadBitmapToImageView(photoPageImage, bitmap);
        }


        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.getServerRequests().like(authorisationCode, currentUser.getId(), id).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(res_id == like) {
                            likeButton.getBackground().setTint(getResources().getColor(R.color.like_pressed));
                            res_id = dislike;
                            likes.setText(++likesCount+" likes");
                        }
                        else {
                            likeButton.getBackground().setTint(getResources().getColor((R.color.like_not_pressed)));
                            res_id = like;
                            likes.setText(--likesCount+" likes");
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
            }
        });



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Typeface mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        photoTextView.setTypeface(mainFont);
        likes.setTypeface(mainFont);
        commentsText.setTypeface(mainFont);
        nameAndSurnameTextView.setTypeface(mainFont);
        if(name!=null)
            nameAndSurnameTextView.setText(name);
        else
            nameAndSurnameTextView.setText(currentUser.getPets().get(0).getName());

    }

    private void getComments(Long id){
        MainActivity.getServerRequests().getAllCommentOfCurrentPhoto(authorisationCode, id).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.body() != null){
                    comments.addAll(response.body());
                    commentsCount = response.body().size();
                    commentsText.setText(commentsCount+" comments");
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
        MainActivity.getServerRequests().getPhotoCaption(authorisationCode, String.valueOf(id)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.body()!=null){
                    caption.setText(response.body().replaceAll("%"," "));
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getLikes(Long id){


        MainActivity.getServerRequests().getLikes(authorisationCode, id).enqueue(new Callback<List<Long>>() {
            @Override
            public void onResponse(Call<List<Long>> call, Response<List<Long>> response) {
                if(response.body() != null){
                    if(response.body().contains(currentUser.getId())){
                        Log.d("likes", "LIKED!!!");
                        likeButton.getBackground().setTint(getResources().getColor(R.color.like_pressed));
                        likesCount = response.body().size();
                        likes.setText(likesCount+" likes");
                        res_id = dislike;
                    }else {
                        Log.d("likes", "NOT LIKED!!!");
                        likesCount = response.body().size();
                        likes.setText(likesCount+" likes");
                        likeButton.getBackground().setTint(getResources().getColor((R.color.like_not_pressed)));
                        res_id = like;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Long>> call, Throwable t) {

            }
        });


    }




    private void listeners(){
        toGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(UsersGallery.class);
            }
        });

        sendNewCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = writeNewCommentEditText.getText().toString();
                if(!commentText.equals("")){
                    sendNewComment(commentText, id);
                    hideKeyboard(getActivity());
                    commentsText.setText(++commentsCount+" comments");


                }
            }
        });
    }

    private void sendNewComment(String text, Long photoID){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        Timestamp time = new Timestamp(System.currentTimeMillis());
        MainActivity.getServerRequests().sendNewComment(authorisationCode, time.toString(), currentUser.getId(), text, photoID).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                commentAdapter.addItem(new Comment(text ,currentUser.getName(), currentUser.getSurname()));
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        writeNewCommentEditText.setText("");
    }

    private void loadBitmapToImageView(ImageView  imageView, Bitmap bitmap){
        GlideApp.with(this)
                .load(bitmap)
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
