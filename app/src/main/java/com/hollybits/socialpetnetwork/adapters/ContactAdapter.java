package com.hollybits.socialpetnetwork.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.Fragments.Chat;
import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.FragmentDispatcher;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.Contact;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<Contact> contacts;

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout contactConstraintLayout;
        public CircleImageView userPhoto;
        public TextView userName, lastMessage, time;

        public MyViewHolder(View itemView) {
            super(itemView);

            contactConstraintLayout = itemView.findViewById(R.id.contact_constraint_layout_in_contact_recycler_view);
            userPhoto = itemView.findViewById(R.id.user_photo_in_contact_recycler_view);
            userName = itemView.findViewById(R.id.user_name_in_contact_recycler_view);
            lastMessage = itemView.findViewById(R.id.last_message_in_contact_recycler_view);
            time = itemView.findViewById(R.id.time_in_contact_recycler_view);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_in_contact_recycler_view,
                parent, false);
        return new ContactAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.userName.setText(contact.getName() + " " + contact.getSurname());
        holder.lastMessage.setText(contact.getLastMessage());
        if(contact.getTimestamp() == null){
            holder.time.setText("");
        }else {
            holder.time.setText(contact.getTimestamp().toString());
        }

        holder.contactConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write(MainActivity.ID_OF_FRIEND, contact.getFriendId());
                Paper.book().write(MainActivity.NAME_OF_FRIEND, contact.getName());
                FragmentDispatcher.launchFragment(Chat.class);
            }
        });


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
