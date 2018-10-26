package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.Pet;
import com.hollybits.socialpetnetwork.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class EditInfo extends Fragment {

    @BindViews({R.id.name_text_in_edit_page, R.id.sex_text_in_edit_page, R.id.breed_text_in_edit_page,
                R.id.city_text_in_edit_page, R.id.country_text_in_edit_page, R.id.owner_name_text_in_edit_page,
                R.id.email_text_in_edit_page, R.id.phone_text_in_edit_page, R.id.weight_text_in_edit_page,
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

    private PhotoManager photoManager;

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

        Typeface avenirNextCyr_regular = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Regular.ttf");
        Typeface infoFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/AvenirNextCyr-Demi.ttf");

        attitudeTextView.setText(currentPet.getAttitude().getName().toLowerCase());

        changeFontForAllWordInList(topics, infoFont);
        editProfileText.setTypeface(infoFont);
        changeProfilePhotoText.setTypeface(infoFont);
        cancelButton.setTypeface(avenirNextCyr_regular);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Account.class);
            }
        });


        String[] attitudes = {"friendly to everyone", "friendly to female", "friendly to male", "unfriendly"};
        int[] colorFilters = {Color.argb(255, 255, 255, 255)};

        changeAttitudeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attitudeTextView.setText(attitudes[attitudePosition]);
                editPen.setColorFilter(colorFilters[attitudePosition]);
                attitudePosition++;

            }
        });

        photoManager.loadUsersMainPhoto(userPhoto);

        return view;
    }

    void changeFontForAllWordInList(List<TextView> words, Typeface font) {
        for (TextView textView :
                words) {
            textView.setTypeface(font);
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
