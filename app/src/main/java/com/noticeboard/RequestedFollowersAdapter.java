package com.noticeboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestedFollowersAdapter extends FirestoreRecyclerAdapter<UserDetails, RequestedFollowersAdapter.RequestingUser> {

   CircleImageView userImage;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RequestedFollowersAdapter(@NonNull FirestoreRecyclerOptions<UserDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestedFollowersAdapter.RequestingUser holder, int position, @NonNull UserDetails model) {

        holder.username.setText(model.getFullname());
        holder.level.setText(model.getLevel());
        Picasso.get().load(model.getUserimage()).placeholder(R.drawable.user_image_avatar).into(userImage);

    }

    @NonNull
    @Override
    public RequestedFollowersAdapter.RequestingUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_followers_tile, parent, false);
        return new RequestingUser(v);
    }

    public class RequestingUser extends RecyclerView.ViewHolder {

        TextView username, level;
        ImageButton accept, cancel;

        public RequestingUser(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            level = itemView.findViewById(R.id.level);
            accept = itemView.findViewById(R.id.acceptRequest);
            cancel = itemView.findViewById(R.id.cancelRequest);
            userImage = itemView.findViewById(R.id.userprofileimg);

        }
    }
}
