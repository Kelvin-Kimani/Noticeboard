package com.noticeboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class PostAdapter extends FirestoreRecyclerAdapter<PostDetails, PostAdapter.PostHolder> {

    private PostAdapter.OnItemClickListener listener;

    public PostAdapter(@NonNull FirestoreRecyclerOptions<PostDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull PostDetails model) {

        holder.title.setText(model.getTitle());
        holder.content.setText(model.getContent());
        holder.time.setText(model.getTime());

    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_post_tile, parent, false);

        return new PostHolder(v);
    }

    public void setOnItemClickListener(PostAdapter.OnItemClickListener listener) {

        this.listener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    class PostHolder extends RecyclerView.ViewHolder {

        TextView title, content, time;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.posttitle);
            content = itemView.findViewById(R.id.post);
            time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });


        }
    }
}
