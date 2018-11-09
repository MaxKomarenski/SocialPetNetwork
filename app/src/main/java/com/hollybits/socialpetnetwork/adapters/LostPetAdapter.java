package com.hollybits.socialpetnetwork.adapters;

import android.app.Fragment;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.Fragments.LostPets;
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
    private LostPets lostPetsFragment;

    public LostPetAdapter(List<LostPet> lostPets,
                          Typeface nameTypeface,
                          Typeface another,
                          android.support.v4.app.Fragment fragment,
                          LostPets lostPetsFragment) {
        this.lostPets = lostPets;
        this.nameTypeface = nameTypeface;
        this.another = another;
        photoManager = new PhotoManager(fragment);
        this.lostPetsFragment = lostPetsFragment;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout background;
        CircleImageView petImg;
        TextView petName, petBreed, ownerName;

        public MyViewHolder(View itemView) {
            super(itemView);

            background = itemView.findViewById(R.id.raw_in_lost_pet_constraintLayout);
            petImg = itemView.findViewById(R.id.user_photo_in_lost_pets_recycler_view);
            petName = itemView.findViewById(R.id.name_of_pet_in_lost_pets_recycler_view);
            petBreed = itemView.findViewById(R.id.breed_of_pet_in_lost_pets_recycler_view);
            ownerName = itemView.findViewById(R.id.name_of_user_in_lost_pets_recycler_view);
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

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lostPetsFragment.showDownBar(lostPet);
            }
        });

        holder.petName.setTypeface(nameTypeface);
        holder.petBreed.setText(lostPet.getBreed());
        holder.petBreed.setTypeface(another);
        holder.ownerName.setText(lostPet.getUserName() + " " + lostPet.getUserSurname());
        holder.ownerName.setTypeface(another);
        photoManager.loadFriendsMainPhoto(holder.petImg, lostPet.getUserId());





    }

    @Override
    public int getItemCount() {
        return lostPets.size();
    }

    public void deleteItem(LostPet lostPet){
        lostPets.remove(lostPet);
    }




}
