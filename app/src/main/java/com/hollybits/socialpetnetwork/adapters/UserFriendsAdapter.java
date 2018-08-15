package com.hollybits.socialpetnetwork.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

import com.hollybits.socialpetnetwork.Fragments.FriendAccount;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
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

public class UserFriendsAdapter extends RecyclerView.Adapter<UserFriendsAdapter.MyViewHolder> implements Filterable{

    private List<FriendInfo> friends;
    private List<FriendInfo> filtredFriends;
    private MyViewHolder previousHolder;

    public UserFriendsAdapter(List<FriendInfo> friends){

        this.friends = friends;
        filtredFriends = friends;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout primeConstraintLayout;

        //-------------small---------------
        public ConstraintLayout smallConstraintLayout;
        public TextView user_name_sm, pet_name_sm, breed_sm;
        public CircleImageView img_sm;
        public ImageView indicator_sm;
        //-------------big----------------
        public ConstraintLayout bigConstraintLayout;
        public TextView user_name_bg, pet_name_bg, breed_bg;
        public CircleImageView img_bg;
        public ImageView indicator_bg;
        public ImageButton infoButton, messageButton, mapButton;


        public MyViewHolder(View view){
            super(view);
            primeConstraintLayout = view.findViewById(R.id.prime_constrainLayout_in_item);

            //---------------small-----------------------------------------------------------------
            smallConstraintLayout = view.findViewById(R.id.small_constraintLayout_in_item);

            user_name_sm = view.findViewById(R.id.name_of_user_in_user_friend_recycler_view);
            pet_name_sm = view.findViewById(R.id.name_of_pet_in_user_friend_recycler_view);
            breed_sm = view.findViewById(R.id.breed_of_pet_in_user_friend_recycler_view);
            img_sm = view.findViewById(R.id.user_photo_in_user_friend_recycler_view);
            indicator_sm = view.findViewById(R.id.indicator_in_user_friend_recycler_view);

            //----------------big------------------------------------------------------------------
            bigConstraintLayout = view.findViewById(R.id.big_constraintLayout_in_item);
            user_name_bg = view.findViewById(R.id.pressed_name_of_user_in_user_friend_recycler_view);
            pet_name_bg = view.findViewById(R.id.pressed_name_of_pet_in_user_friend_recycler_view);
            breed_bg = view.findViewById(R.id.pressed_breed_of_pet_in_user_friend_recycler_view);
            img_bg = view.findViewById(R.id.pressed_user_photo_in_user_friend_recycler_view);
            indicator_bg = view.findViewById(R.id.pressed_indicator_in_user_friend_recycler_view);
            infoButton = view.findViewById(R.id.info_button_in_pressed_item);
            //messageButton
            //mapButton
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

        holder.user_name_sm.setText(friend.getName() + " " + friend.getSurname());
        holder.pet_name_sm.setText(friend.getPetName());
        holder.breed_sm.setText(friend.getPetBreedName());
        //----------------------------------
        holder.user_name_bg.setText(friend.getName() + " " + friend.getSurname());
        holder.pet_name_bg.setText(friend.getPetName());
        holder.breed_bg.setText(friend.getPetBreedName());

        if(isUserOnline(friend.getLastActiveTime())){
            holder.indicator_sm.setImageResource(R.drawable.green_dot);
            holder.indicator_bg.setImageResource(R.drawable.green_dot);
        }else {
            holder.indicator_sm.setImageResource(R.drawable.red_lock);
            holder.indicator_bg.setImageResource(R.drawable.red_lock);
        }

        holder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllInformationOfChosenUser(friend.getId());
            }
        });

        holder.smallConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.smallConstraintLayout.setVisibility(View.GONE);
                holder.bigConstraintLayout.setVisibility(View.VISIBLE);
                holder.primeConstraintLayout.setBackgroundResource(R.drawable.background_for_raw_of_recycler_view_big);

                if(previousHolder == null){
                    previousHolder = holder;
                }else {
                    previousHolder.bigConstraintLayout.setVisibility(View.GONE);
                    previousHolder.smallConstraintLayout.setVisibility(View.VISIBLE);
                    previousHolder.primeConstraintLayout.setBackgroundResource(R.drawable.background_for_raw_of_recycler_view);
                    previousHolder = holder;
                }
            }
        });

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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filtredFriends = friends;
                } else {
                    List<FriendInfo> filteredList = new ArrayList<>();
                    for (FriendInfo row : friends) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if((row.getName()+row.getSurname()).contains(charSequence)){
                            filteredList.add(row);
                        }
                    }

                    filtredFriends = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtredFriends;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtredFriends = (ArrayList<FriendInfo>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    private void getAllInformationOfChosenUser(Long id){
        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().getInformationOfAnotherUser(authorisationCode, id).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                Paper.book().write(MainActivity.CURRENT_CHOICE, response.body());
                FragmentDispatcher.launchFragment(FriendAccount.class);

            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }

}