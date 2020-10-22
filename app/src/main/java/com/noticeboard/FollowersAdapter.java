package com.noticeboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowersAdapter extends FirestoreRecyclerAdapter<UserDetails, FollowersAdapter.User> {
    CircleImageView userImageView;
    String level;
    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FollowersAdapter(@NonNull FirestoreRecyclerOptions<UserDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FollowersAdapter.User holder, int position, @NonNull UserDetails model) {

        level = model.getLevel();

        holder.textViewUsername.setText(model.getFullname());
        holder.textViewLevel.setText(level);
        Picasso.get().load(model.getUserimage()).placeholder(R.drawable.user_image_avatar).into(userImageView);

        if (level.equals("Staff")) {

            holder.verifiedStaffImage.setVisibility(View.VISIBLE);

        }


    }

    @NonNull
    @Override
    public FollowersAdapter.User onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_tile, parent, false);
        return new User(v);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public class User extends RecyclerView.ViewHolder {

        TextView textViewUsername;
        TextView textViewLevel;
        ImageView verifiedStaffImage;


        public User(@NonNull View itemView) {
            super(itemView);

            textViewUsername = (itemView).findViewById(R.id.username);
            textViewLevel = (itemView).findViewById(R.id.level);
            userImageView = (itemView).findViewById(R.id.userprofileimg);
            verifiedStaffImage = (itemView).findViewById(R.id.verifiedStaff);

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
