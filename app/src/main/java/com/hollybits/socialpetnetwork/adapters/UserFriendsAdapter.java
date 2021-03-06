package com.hollybits.socialpetnetwork.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.Fragments.Chat;
import com.hollybits.socialpetnetwork.Fragments.FriendAccount;
import com.hollybits.socialpetnetwork.Fragments.UserFriends;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.User;
import com.hollybits.socialpetnetwork.models.UserInfo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.MyViewHolder>{

    private List<FriendInfo> friends;
    private List<FriendInfo> filtredFriends;
    private MyViewHolder previousHolder;
    private Typeface nameFont, breedFont;
    private PhotoManager photoManager;
    private UserFriends userFriendsFragment;

    public UserFriendsAdapter(Typeface nameFont, Typeface breedFont, Fragment fragment, UserFriends userFriends){

        this.friends = new ArrayList<>();
        photoManager = new PhotoManager(fragment);
        filtredFriends = friends;
        notifyDataSetChanged();
        this.nameFont = nameFont;
        this.breedFont = breedFont;
        this.userFriendsFragment = userFriends;

    }

    public void setFriends(List<FriendInfo> friends) {
        this.friends.clear();
        this.friends.addAll(friends);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout primeConstraintLayout;

        ConstraintLayout smallConstraintLayout;
        TextView pet_name_sm, breed_sm, active_or_not;
        CircleImageView img_sm;
        ImageView indicator_sm;


        public MyViewHolder(View view){
            super(view);
            primeConstraintLayout = view.findViewById(R.id.prime_constrainLayout_in_item);

            smallConstraintLayout = view.findViewById(R.id.small_constraintLayout_in_item);
            pet_name_sm = view.findViewById(R.id.name_of_pet_in_user_friend_recycler_view);
            breed_sm = view.findViewById(R.id.breed_of_pet_in_user_friend_recycler_view);
            img_sm = view.findViewById(R.id.user_photo_in_friendship_recycler_view);
            indicator_sm = view.findViewById(R.id.indicator_in_user_friend_recycler_view);
            active_or_not = view.findViewById(R.id.active_or_not_text_in_friend_raw);

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
        FriendInfo friend = filtredFriends.get(position);

        if (previousHolder != null){
            previousHolder.smallConstraintLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.pet_name_sm.setText(friend.getPetName());
        holder.pet_name_sm.setTypeface(nameFont);
        holder.breed_sm.setText(friend.getPetBreedName());
        holder.breed_sm.setTypeface(breedFont);
        photoManager.loadFriendsMainPhoto(holder.img_sm, friend.getId());

        if(isUserOnline(friend.getLastActiveTime())){
            holder.indicator_sm.setImageResource(R.drawable.green_dot);
            holder.active_or_not.setText("Active");
        }else {
            holder.active_or_not.setText("Not active");
            holder.indicator_sm.setImageResource(R.drawable.red_lock);
        }

        holder.smallConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.smallConstraintLayout.setBackgroundColor(Color.parseColor("#f3fbfe"));
                userFriendsFragment.showDownBar(friend);
            }
        });
        previousHolder = holder;

    }

    @Override
    public int getItemCount() {
        return filtredFriends.size();
    }

    private boolean isUserOnline(Timestamp timestamp){
        long five_minutes = 5 * 60 * 1000;
        long currentTime = System.currentTimeMillis();
        Log.d("last active", timestamp.toString());
        Log.d("time", String.valueOf(currentTime - timestamp.getTime()));
        return currentTime - timestamp.getTime() < five_minutes;

    }

    public void addItem(FriendInfo friendInfo){
        friends.add(friendInfo);
    }

    public void filterList(ArrayList<FriendInfo> filteredList) {
        friends.clear();
        friends.addAll(filteredList);
        notifyDataSetChanged();
    }

}
