package com.noticeboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostAdapter extends FirestoreRecyclerAdapter<PostDetails, PostAdapter.PostHolder> {

    public PostAdapter(@NonNull FirestoreRecyclerOptions<PostDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull PostDetails model) {

        holder.title.setText(model.getTitle());
        holder.content.setText(model.getContent());
       //Remember to add time
        holder.time.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_tile, parent, false);

        return new PostHolder(v);
    }

    class PostHolder extends RecyclerView.ViewHolder {

        TextView title, content,time;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            title  = itemView.findViewById(R.id.posttitle);
            content = itemView.findViewById(R.id.post);
            time = itemView.findViewById(R.id.time);

        }
    }
}
