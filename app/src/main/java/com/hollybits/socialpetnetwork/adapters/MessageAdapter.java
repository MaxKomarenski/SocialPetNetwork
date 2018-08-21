package com.hollybits.socialpetnetwork.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.activity.MainActivity;
import com.hollybits.socialpetnetwork.models.Message;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder>{

    private List<Message> messages;

    public MessageAdapter(List<Message> messages){
        this.messages = messages;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout messageLinearLayout;
        private TextView text, time;

        public MyViewHolder(View itemView) {
            super(itemView);
            messageLinearLayout = itemView.findViewById(R.id.one_message_linear_layout);
            text = itemView.findViewById(R.id.text_of_message);
            time = itemView.findViewById(R.id.time_of_message);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_in_message_recycler_view ,
                parent, false);
        return new MessageAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {

        Message message = messages.get(position);
        if(message.getUserTo().equals(MainActivity.getCurrentUser().getId())){
            holder.messageLinearLayout.setGravity(Gravity.START);
        }else {
            holder.messageLinearLayout.setGravity(Gravity.END);
        }
        holder.text.setText(message.getMessage());
        Date date = new Date(message.getTimestamp().getTime());
        holder.time.setText(date.toString());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void add(Message m){
        messages.add(m);
    }

    private void sortByTimestamp( List<Message> messageList){
        Collections.sort(messageList, new Comparator<Message>() {
            public int compare(Message o1, Message o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
        });
    }



}
