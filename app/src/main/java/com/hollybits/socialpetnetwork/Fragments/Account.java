package com.hollybits.socialpetnetwork.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.Pet;
import com.hollybits.socialpetnetwork.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Account.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Account#newInstance} factory method to
 * create an instance of this fragment.
 */

public class Account extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    @BindView(R.id.photo_grid_view)
//    GridView photoGridView;
//    Integer[] images = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e};
//
//    private PhotoGridAdapter photoGridAdapter;

    @BindView(R.id.user_photo_in_account_fragment)
    CircleImageView userMainPhoto;

    @BindView(R.id.open_navigation_drawer_image_button)
    ImageButton openDrawerButton;

    @BindView(R.id.open_map_from_account)
    ImageButton openMap;

    @BindViews({R.id.name_of_pet_text_view, R.id.sex_of_pet_text_view,
                R.id.breed_word_text_view, R.id.name_of_breed_text_view, R.id.address_word_text_view,
                R.id.place_of_user_text_view, R.id.age_word_text_view, R.id.number_of_age_word_text_view,
                R.id.weight_word_text_view, R.id.amount_of_weight_text_view, R.id.attitude_word_text_view,
                R.id.attitude_state_text_view
    })
    List<TextView> informationAboutPet;

    @BindViews({R.id.owner_name_and_surname_in_expansion_panel,
            R.id.owner_phone_in_expansion_panel,
            R.id.owner_email_in_expansion_panel})
    List<TextView> informationAboutUser;

    private static final int PICK_IMAGE = 100;
    DrawerLayout drawer;
    private Uri imageUri;

    private OnFragmentInteractionListener mListener;

    public Account() {
        // Required empty public constructor
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

//        photoGridAdapter = new PhotoGridAdapter(this.getContext(), images);
//
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/3;
//
//        photoGridView.setColumnWidth(imageWidth);
//
//        photoGridView.setAdapter(photoGridAdapter);

        showAllInformationOnTheScreen();

        Typeface mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        for (TextView textView:
             informationAboutPet) {
            textView.setTypeface(mainFont);
        }

        listeners();


        return view;
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            userMainPhoto.setImageURI(imageUri);
        }
    }

    private void listeners(){
        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        });

        userMainPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDispatcher.launchFragment(Map.class);
            }
        });
    }

    private void showAllInformationOnTheScreen(){
        //TODO make if user has more than one pet
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Pet currentPet = currentUser.getPets().get(0);


        informationAboutPet.get(0).setText(currentPet.getName()); //name
        informationAboutPet.get(1).setText(currentPet.getSex().name().toLowerCase()); //sex
        informationAboutPet.get(3).setText(currentPet.getBreed().getName()); //breed
        informationAboutPet.get(5).setText(currentUser.getCity().getName() + ", " + currentUser.getCity().getCountry().getName());// city and country
        informationAboutPet.get(7).setText(currentPet.getAge().toString()); // age
        informationAboutPet.get(9).setText(currentPet.getWeight().getMass().toString() + " " + currentPet.getWeight().getMassUnit().getName().toLowerCase());
        informationAboutPet.get(11).setText(currentPet.getAttitude().toString());

        informationAboutUser.get(0).setText(currentUser.getName() + " " + currentUser.getSurname());
        informationAboutUser.get(1).setText(currentUser.getPhone());
        informationAboutUser.get(2).setText(currentUser.getCredentials().email);
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
