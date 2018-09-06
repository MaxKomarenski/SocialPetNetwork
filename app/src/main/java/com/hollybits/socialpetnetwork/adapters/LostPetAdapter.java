package com.hollybits.socialpetnetwork.adapters;

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
import com.hollybits.socialpetnetwork.models.LostPet;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LostPetAdapter extends RecyclerView.Adapter<LostPetAdapter.MyViewHolder> {

    private List<LostPet> lostPets;
    private Typeface nameTypeface;
    private Typeface another;

    public LostPetAdapter(List<LostPet> lostPets, Typeface nameTypeface, Typeface another) {
        this.lostPets = lostPets;
        this.nameTypeface = nameTypeface;
        this.another = another;
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

        if (!lostPet.getUserId().equals(MainActivity.getCurrentUser().getId())){
            holder.foundMyPet.setVisibility(View.GONE);
            holder.chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else {
            holder.chatButton.setVisibility(View.GONE);
            holder.foundMyPet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return lostPets.size();
    }


}
