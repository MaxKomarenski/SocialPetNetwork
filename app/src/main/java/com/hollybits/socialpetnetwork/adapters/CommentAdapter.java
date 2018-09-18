package com.hollybits.socialpetnetwork.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hollybits.socialpetnetwork.R;
import com.hollybits.socialpetnetwork.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameAndSurname;
        TextView textComment;

        public MyViewHolder(View itemView) {
            super(itemView);

            nameAndSurname = itemView.findViewById(R.id.name_and_surname_in_comment_raw);
            textComment = itemView.findViewById(R.id.text_comment_in_comment_raw);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_comment,
                parent, false);
        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.nameAndSurname.setText(comment.getName() + " " + comment.getSurname());
        holder.textComment.setText(comment.getText());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


}
