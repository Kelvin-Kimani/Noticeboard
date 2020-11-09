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
import com.noticeboard.Utils.AppUtils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.sql.Types.TIMESTAMP;

public class CommentAdapter extends FirestoreRecyclerAdapter<CommentDetails, CommentAdapter.CommentHolder> {

    CircleImageView userImage;
    private OnItemClickListener itemClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentAdapter(@NonNull FirestoreRecyclerOptions<CommentDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentAdapter.CommentHolder holder, int position, @NonNull CommentDetails model) {
        //need username and imageurl.
        holder.username.setText(model.getUsername());
        holder.comment.setText(model.getComment());


        //Time Ordering
        String date_time = model.getTime();
        String timeAgo = AppUtils.getTime(TIMESTAMP);
        
        holder.time.setText(model.getTime());
        Picasso.get().load(model.getImageURL()).placeholder(R.drawable.user_image_avatar).into(userImage);

    }

    @NonNull
    @Override
    public CommentAdapter.CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_comment_tile, parent, false);

        return new CommentHolder(v);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        TextView username, comment, time;


        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            time = itemView.findViewById(R.id.commentpostTime);
            userImage = itemView.findViewById(R.id.userprofileimg);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && itemClickListener != null) {

                        itemClickListener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });
        }
    }
}
