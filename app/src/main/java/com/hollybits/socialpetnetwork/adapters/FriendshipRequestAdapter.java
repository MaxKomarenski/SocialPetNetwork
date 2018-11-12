package com.hollybits.socialpetnetwork.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.Fragments.Chat;
import com.hollybits.socialpetnetwork.Fragments.Requests;
import com.hollybits.socialpetnetwork.Fragments.UserFriends;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.helper.PhotoManager;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.User;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendshipRequestAdapter extends RecyclerView.Adapter<FriendshipRequestAdapter.MyViewHolder> {

    private List<InfoAboutUserFriendShipRequest> friendShipRequests;
    private Typeface first;
    private Requests root;
    private Typeface second;
    public boolean cancel;
    public boolean selectAll;
    List<Long> checkedIds;
    private User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
    private PhotoManager photoManager;
    private Map<String, String> authorisationCode = new HashMap<>();


    public FriendshipRequestAdapter(List<InfoAboutUserFriendShipRequest> friendShipRequests,
                                    Typeface first,
                                    Typeface second,
                                    Requests root){
        this.friendShipRequests = friendShipRequests;
        this.first = first;
        this.second = second;
        this.root = root;
        photoManager = new PhotoManager(root);
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        checkedIds = new ArrayList<>();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userPhoto;
        TextView petName, breed;
        CheckBox checkBox;
        TextView onlineText;
        ImageView indicator;


        public MyViewHolder(View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.user_photo_in_friendship_recycler_view);
            petName = itemView.findViewById(R.id.pet_name_in_friendship_recycler_view);
            breed = itemView.findViewById(R.id.pet_breed_in_friendship_recycler_view);
            indicator = itemView.findViewById(R.id.indicator_in_user_friend_recycler_view);
            onlineText = itemView.findViewById(R.id.active_or_not_text_in_friend_raw);
            checkBox = itemView.findViewById(R.id.accept);
        }
    }

    @NonNull
    @Override
    public FriendshipRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_in_friendship_request_recycler_view,
                parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendshipRequestAdapter.MyViewHolder holder, int position) {
        Log.d("POS", String.valueOf(position));
        try {
            InfoAboutUserFriendShipRequest request = friendShipRequests.get(position);
            holder.petName.setTypeface(first);
            photoManager.loadFriendsMainPhoto(holder.userPhoto, request.getId());
            holder.petName.setText(request.getPetName());
            holder.breed.setText(request.getPetBreed());
            holder.breed.setTypeface(second);
            if(selectAll){
                holder.checkBox.setChecked(true);
            }
            if(cancel){
                holder.checkBox.setChecked(false);
            }
            checkIfUserIsOnline(request.getId(), holder);
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    if(!checkedIds.contains(friendShipRequests.get(position).getId()))
                        checkedIds.add(friendShipRequests.get(position).getId());
                    Log.d("FriendShip Adapter", "Added id "+ friendShipRequests.get(position).getId());
                    root.accept.setVisibility(View.VISIBLE);
                    root.decline.setVisibility(View.VISIBLE);
                }else {
                    checkedIds.remove(friendShipRequests.get(position).getId());
                }
            });
            if(position == friendShipRequests.size()-1){
                selectAll = false;
                cancel = false;
            }

        }catch (Exception e){
            FragmentDispatcher.launchFragment(UserFriends.class);
        }
    }

    @Override
    public int getItemCount() {
        return friendShipRequests.size();
    }

    public void addItem(InfoAboutUserFriendShipRequest request){
        friendShipRequests.add(request);
    }



    public List<Long> getCheckedIds() {

        if(checkedIds == null){
            return new ArrayList<>();
        }
        return checkedIds;
    }

    public void  selectAll(){
        for(InfoAboutUserFriendShipRequest inf: friendShipRequests){
            checkedIds.add(inf.getId());
        }
    }
    public void cancelSelection(){
        checkedIds.clear();
    }

    public void deleteItem(InfoAboutUserFriendShipRequest infoAboutUserFriendShipRequest){
        if(infoAboutUserFriendShipRequest == null)
            return;
        friendShipRequests.remove(infoAboutUserFriendShipRequest);
    }


    public InfoAboutUserFriendShipRequest getRequestById(Long id){
        for(InfoAboutUserFriendShipRequest r: friendShipRequests){
            if(r.getId().equals(id)){
                return r;
            }
        }
        return null;
    }


    private void checkIfUserIsOnline(Long friendId, MyViewHolder holder){


        MainActivity.getServerRequests().getUserLastActiveTime(authorisationCode, friendId).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String time = response.body().replaceAll("%", " ");
                Timestamp timestamp = Timestamp.valueOf(time.replaceAll("\\^", ":"));

                long five_minutes = 5 * 60 * 1000;
                long currentTime = System.currentTimeMillis();

                try{
                    if(currentTime - timestamp.getTime() < five_minutes){


                        holder.indicator.setVisibility(View.VISIBLE);
                        holder.onlineText.setText("online");


                    }else {
                        holder.indicator.setVisibility(View.INVISIBLE);
                        holder.onlineText.setText("not online");
                        holder.onlineText.setTextColor(Color.parseColor("#ff0000"));
                    }
                }catch (NullPointerException e){

                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }








}
