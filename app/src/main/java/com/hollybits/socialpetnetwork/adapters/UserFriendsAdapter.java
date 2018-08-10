package com.hollybits.socialpetnetwork.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.MyViewHolder> {

    private List<Map<String, Object>> friends;

    public UserFriendsAdapter(List<Map<String, Object>> friends){
        this.friends = friends;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, breed;
        public CircleImageView img;
        public ImageView indicator;

        public MyViewHolder(View view){
            super(view);

            name = view.findViewById(R.id.name_of_pet_in_user_friend_recycler_view);
            breed = view.findViewById(R.id.breed_of_pet_in_user_friend_recycler_view);
            img = view.findViewById(R.id.pet_photo_in_user_friend_recycler_view);
            indicator = view.findViewById(R.id.indicator_in_user_friend_recycler_view);

        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_friend_in_user_friends_recycler_view
        ,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Map<String, Object> friend = friends.get(position);
        Log.d("position", String.valueOf(position));
        holder.name.setText(friend.get("name").toString());
        holder.breed.setText(friend.get("breed").toString());

    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

}
