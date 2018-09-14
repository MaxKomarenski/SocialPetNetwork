package com.hollybits.socialpetnetwork.Fragments;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.Pet;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;
import com.kennyc.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendAccount extends Fragment {

    @BindViews({
            R.id.breed_word_text_view,
            R.id.address_word_text_view,
            R.id.age_word_text_view,
            R.id.weight_word_text_view,
            R.id.attitude_word_text_view
    })
    List<TextView> words;

    @BindViews({
            R.id.name_of_pet_text_view,
            R.id.sex_of_pet_text_view,
            R.id.name_of_breed_text_view,
            R.id.place_of_user_text_view,
            R.id.number_of_age_word_text_view,
            R.id.amount_of_weight_text_view,
            R.id.attitude_state_text_view,
            R.id.owner_name_and_surname_in_expansion_panel,
            R.id.owner_phone_in_expansion_panel,
            R.id.owner_email_in_expansion_panel
    })
    List<TextView> allChangedInformation;

    @BindView(R.id.open_navigation_drawer_image_button)
    ImageView openNavigationDrawer;

    @BindView(R.id.user_photo_in_friend_account_fragment)
    CircleImageView circleImageView;

    @BindView(R.id.become_friend_button)
    ImageButton becomeFriendButton;

    @BindView(R.id.delete_friend_button)
    ImageButton deleteFriendButton;

    @BindView(R.id.become_friend_text)
    TextView becomeFriendText;

    DrawerLayout drawer;
    UserInfo userInfo;
    List<FriendInfo> friends;
    boolean friendOrNot;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private PhotoManager photoManager;

    private User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
    private Map<String, String> authorisationCode = new HashMap<>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FriendAccount() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_friend_account, container, false);
        ButterKnife.bind(this, view);

        userInfo = Paper.book().read(MainActivity.CURRENT_CHOICE);
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        photoManager = new PhotoManager(this);
        photoManager.loadFriendsMainPhoto(circleImageView, userInfo.getId());

        fillAllInformation();
        isThisUserAFriend();


        Typeface mainFont = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/911Fonts.com_CenturyGothicBold__-_911fonts.com_fonts_pMgo.ttf");
        for (TextView textView: words) {
            textView.setTypeface(mainFont);
        }
        for (TextView textView: allChangedInformation) {
            textView.setTypeface(mainFont);
        }

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        openNavigationDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        });

        View acceptDeletingFriend = FriendAccount.this.getLayoutInflater().inflate(R.layout.accept_deleting_friend, null);

        ImageButton accept = acceptDeletingFriend.findViewById(R.id.acceptDeletingFriend);
        ImageButton reject = acceptDeletingFriend.findViewById(R.id.rejectDeletingFriends);
        BottomSheet bottomSheet = new BottomSheet.Builder(FriendAccount.this.getActivity())
                .setView(acceptDeletingFriend).create();

        deleteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriendButton.startAnimation(buttonClick);
                bottomSheet.show();


            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriendFromFriendListInPaperBook();
                bottomSheet.dismiss();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.dismiss();
            }
        });



        return view;
    }

    private void deleteFriendFromFriendListInPaperBook(){

        deleteFriendFromDB();

        List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);
        for (int i = 0; i < friends.size(); i++){
            if(userInfo.getId().equals(friends.get(i).getId())){
                friends.remove(i);
            }
        }

        Paper.book().write(MainActivity.FRIEND_LIST, friends);
    }

    private void deleteFriendFromDB(){
        MainActivity.getServerRequests().deleteUserFromFriendList(authorisationCode, currentUser.getId(), userInfo.getId()).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void isThisUserAFriend(){
        friends = Paper.book().read(MainActivity.FRIEND_LIST);

        if(friends == null){
            MainActivity.getServerRequests().getAllUserFriends(authorisationCode, currentUser.getId()).enqueue(new Callback<Set<FriendInfo>>() {
                @Override
                    public void onResponse(Call<Set<FriendInfo>> call, Response<Set<FriendInfo>> response) {
                        friends = new ArrayList<>();
                        friends.addAll(response.body());
                        Paper.book().write(MainActivity.FRIEND_LIST, friends);

                        for (FriendInfo friend:
                                friends) {
                            if(friend.getId().equals(userInfo.getId())){
                                friendOrNot = true;
                                helperInIsThisAFriend(friendOrNot);
                                return;
                            }
                        }

                        friendOrNot = false;
                        helperInIsThisAFriend(friendOrNot);
                    }

                    @Override
                    public void onFailure(Call<Set<FriendInfo>> call, Throwable t) {

                    }
                });
            }else{
                for (FriendInfo friend:
                        friends) {
                    if(friend.getId().equals(userInfo.getId())){
                        friendOrNot = true;
                        helperInIsThisAFriend(friendOrNot);
                        return;
                    }
            }

            friendOrNot = false;
            helperInIsThisAFriend(friendOrNot);
        }
    }

    private void helperInIsThisAFriend(boolean isFriend){
        if(isFriend){
            deleteFriendButton.setVisibility(View.VISIBLE);
            becomeFriendText.setText("Delete friend");
            becomeFriendButton.setVisibility(View.GONE);
        }
    }

    private void fillAllInformation(){
        Pet pet = new Pet();
        for (Pet p:
             userInfo.getPets()) {
            if(p.getId().equals(userInfo.getPet_id())){
                pet = p;
            }
        }

        String[] info = {
                pet.getName(),
                pet.getSex().name().toLowerCase(),
                pet.getBreed().getName(),
                userInfo.getCity().getName() + ", " + userInfo.getCity().getCountry().getName(),
                pet.getAge().toString(),
                pet.getWeight().getMass().toString() + " " + pet.getWeight().getMassUnit().name().toLowerCase(),
                pet.getAttitude().name(),
                userInfo.getName() + " " + userInfo.getSurname(),
                userInfo.getPhone(),
                userInfo.getEmail()
        };

        for(int i = 0; i < info.length; i++){
            allChangedInformation.get(i).setText(info[i]);
        }

    }
    private void addToFriendRequest(Long target) {

        MainActivity.getServerRequests().newFriendshipRequest(authorisationCode, currentUser.getId(), target).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 202) {

                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FriendAccount.
         */
    // TODO: Rename and change types and number of parameters
    public static FriendAccount newInstance(String param1, String param2) {
        FriendAccount fragment = new FriendAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
