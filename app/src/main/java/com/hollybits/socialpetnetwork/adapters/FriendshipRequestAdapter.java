package com.hollybits.socialpetnetwork.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.FriendInfo;
import com.hollybits.socialpetnetwork.models.InfoAboutUserFriendShipRequest;
import com.hollybits.socialpetnetwork.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendshipRequestAdapter extends RecyclerView.Adapter<FriendshipRequestAdapter.MyViewHolder> {

    private List<InfoAboutUserFriendShipRequest> friendShipRequests;

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
            breed = itemView.findViewById(R.id.pet_breed_in_friendship_recycler_view);
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
        holder.place.setText(request.getCity() + ", " + request.getCountry());
        holder.petName.setText(request.getPetName());
        holder.breed.setText(request.getPetBreed());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendshipRequest(request);
                Paper.book().delete(MainActivity.CONTACT_LIST);

            }
        });
    }

    @Override
    public int getItemCount() {
        return friendShipRequests.size();
    }

    public void addItem(InfoAboutUserFriendShipRequest request){
        friendShipRequests.add(request);
    }

    private void acceptFriendshipRequest(InfoAboutUserFriendShipRequest request){

        User currentUser = Paper.book().read(MainActivity.CURRENTUSER);
        Map<String, String> authorisationCode = new HashMap<>();
        authorisationCode.put("authorization", currentUser.getAuthorizationCode());
        MainActivity.getServerRequests().acceptFriendshipInvitation(authorisationCode,
                                                                    currentUser.getId(),
                                                                    request.getId(),
                                                                    true).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        deleteRequestFromPaperBook(request);
        deleteItem(request);

    }

    private void getFromServerAllFriendsWhichAreNotOnClientSide(){
        List<FriendInfo> friends = Paper.book().read(MainActivity.FRIEND_LIST);
        List<Long> friend_ids = new ArrayList<>();
        if (friends != null){

        }
    }

    private void deleteRequestFromPaperBook(InfoAboutUserFriendShipRequest info){
        List<InfoAboutUserFriendShipRequest> list = Paper.book().read(MainActivity.FRIENDSHIP_REQUEST_LIST);
        for (int i = 0; i < list.size(); i++){
            if(info.equals(list.get(i))){
                list.remove(i);
            }
        }

        Paper.book().write(MainActivity.FRIENDSHIP_REQUEST_LIST, list);
        notifyDataSetChanged();

    }

    private void deleteItem(InfoAboutUserFriendShipRequest infoAboutUserFriendShipRequest){
        friendShipRequests.remove(infoAboutUserFriendShipRequest);
    }
}
