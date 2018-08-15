package com.hollybits.socialpetnetwork.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendshipRequestAdapter extends RecyclerView.Adapter<FriendshipRequestAdapter.MyViewHolder> {

    List<InfoAboutUserFriendShipRequest> friendShipRequests;

    public FriendshipRequestAdapter(List<InfoAboutUserFriendShipRequest> friendShipRequests){
        this.friendShipRequests = friendShipRequests;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView userPhoto;
        public TextView userName, place, petName, breed;
        public Button accept, reject;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.user_photo_in_friendship_recycler_view);
            userName = itemView.findViewById(R.id.name_of_user_in_friendship_recycler_view);
            petName = itemView.findViewById(R.id.pet_name_in_friendship_recycler_view);
            place = itemView.findViewById(R.id.place_of_user_in_friendship_recycler_view);
            accept = itemView.findViewById(R.id.accept_button_in_friendship_recycler_view);
            reject = itemView.findViewById(R.id.reject_button_in_friendship_recycler_view);
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
        InfoAboutUserFriendShipRequest request = friendShipRequests.get(position);
        holder.userName.setText(request.getName() + " " + request.getSurname());

    }

    @Override
    public int getItemCount() {
        return friendShipRequests.size();
    }
}
