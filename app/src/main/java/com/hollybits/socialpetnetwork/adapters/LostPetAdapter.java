package com.hollybits.socialpetnetwork.adapters;

import android.app.Fragment;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.LostPet;
import com.hollybits.socialpetnetwork.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LostPetAdapter extends RecyclerView.Adapter<LostPetAdapter.MyViewHolder> {

    private List<LostPet> lostPets;
    private Typeface nameTypeface;
    private Typeface another;
    private PhotoManager photoManager;

    public LostPetAdapter(List<LostPet> lostPets, Typeface nameTypeface, Typeface another, android.support.v4.app.Fragment fragment) {
        this.lostPets = lostPets;
        this.nameTypeface = nameTypeface;
        this.another = another;
        photoManager = new PhotoManager(fragment);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView petImg;
        ImageButton chatButton, foundMyPet;
        TextView petName, petBreed, ownerName;

        public MyViewHolder(View itemView) {
            super(itemView);

            petImg = itemView.findViewById(R.id.user_photo_in_lost_pets_recycler_view);
            chatButton = itemView.findViewById(R.id.send_message_button_in_lost_pets);
            petName = itemView.findViewById(R.id.name_of_pet_in_lost_pets_recycler_view);
            petBreed = itemView.findViewById(R.id.breed_of_pet_in_lost_pets_recycler_view);
            ownerName = itemView.findViewById(R.id.name_of_user_in_lost_pets_recycler_view);
            foundMyPet = itemView.findViewById(R.id.i_found_my_pet_button_in_lost_pets);
        }
    }

    @NonNull
    @Override
    public LostPetAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_in_lost_pets_recycler_view,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostPetAdapter.MyViewHolder holder, int position) {
        LostPet lostPet = lostPets.get(position);

        holder.petName.setText(lostPet.getPetName());
        holder.petName.setTypeface(nameTypeface);
        holder.petBreed.setText(lostPet.getBreed());
        holder.petBreed.setTypeface(another);
        holder.ownerName.setText(lostPet.getUserName() + " " + lostPet.getUserSurname());
        holder.ownerName.setTypeface(another);
        photoManager.loadFriendsMainPhoto(holder.petImg, lostPet.getUserId());

        if (!lostPet.getUserId().equals(MainActivity.getCurrentUser().getId())){
            holder.foundMyPet.setVisibility(View.GONE);
            holder.chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //TODO if man who lost pet is your friend just open chat
//                    if(lostPet.getUserId()){
//
//                    }

                    User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
                    Map<String, String> authorisationCode = new HashMap<>();
                    authorisationCode.put("authorization", currentUser.getAuthorizationCode());
                    MainActivity.getServerRequests().addToFriendsWhenOneUserFoundPetOfAnotherUser(authorisationCode,
                            currentUser.getId(), lostPet.getUserId()).enqueue(new Callback<FriendInfo>() {
                        @Override
                        public void onResponse(Call<FriendInfo> call, Response<FriendInfo> response) {
                            if(response.body() != null){
                                addNewFriendToPaperBook(response.body());
                                Paper.book().delete(MainActivity.CONTACT_LIST);
                            }
                        }

                        @Override
                        public void onFailure(Call<FriendInfo> call, Throwable t) {

                        }
                    });
                }
            });
        }else {
            holder.chatButton.setVisibility(View.GONE);
            holder.foundMyPet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
                    Map<String, String> authorisationCode = new HashMap<>();
                    authorisationCode.put("authorization", currentUser.getAuthorizationCode());
                    MainActivity.getServerRequests().userFoundHisPet(authorisationCode, lostPet.getPetId()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            deleteItem(lostPet);
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {

                        }
                    });
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return lostPets.size();
    }

    public void deleteItem(LostPet lostPet){
        lostPets.remove(lostPet);
    }

    private void addNewFriendToPaperBook(FriendInfo newFriend){
        List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);
        friends.add(newFriend);
        Paper.book().write(MainActivity.FRIEND_LIST, friends);
    }


}
