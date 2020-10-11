package com.noticeboard;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageUserAdapter extends FirestoreRecyclerAdapter<PageDetails, PageUserAdapter.PageHolder> {

    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), pagename, pageinfo;
    CircleImageView pageImageView;
    private OnItemClickListener listener;
    private OnFollowButtonClickListener followButtonClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PageUserAdapter(@NonNull FirestoreRecyclerOptions<PageDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final PageHolder holder, int position, @NonNull PageDetails model) {

        pagename = model.getPagename();
        pageinfo = model.getPageinfo();
        String pgID = model.getPageID();
        String adUID = model.getUserID();

        holder.follow.setVisibility(View.VISIBLE);

        DocumentReference followedpage = FirebaseFirestore.getInstance().collection("Users").document(adUID).collection("Pages").document(pgID).collection("Followers").document(userID);
        followedpage.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.following.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        DocumentReference requestedpage = FirebaseFirestore.getInstance().collection("Users").document(adUID).collection("Pages").document(pgID).collection("Requested").document(userID);
        requestedpage.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        holder.requested.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        holder.textViewPageName.setText(model.getPagename());
        holder.textViewPageInfo.setText(model.getPageinfo());


        Character firstLetter = pagename.charAt(0);

        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.RED)
                .fontSize(30)
                .toUpperCase()
                .bold()
                .width(80)  // width in px
                .height(80) // height in px
                .endConfig()
                .buildRect(String.valueOf(firstLetter), Color.BLACK);

        holder.pageImageView.setImageDrawable(drawable);

        // Picasso.get().load(model.getPageimage()).placeholder(R.drawable.page_icon).into(pageImageView);


    }

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_tile_user, parent, false);
        return new PageHolder(v);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }

    public void setOnFollowButtonClickListener(OnFollowButtonClickListener followButtonClickListener) {

        this.followButtonClickListener = followButtonClickListener;

    }

    public interface OnItemClickListener {

        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public interface OnFollowButtonClickListener {

        void onFollowButtonClick(DocumentSnapshot documentSnapshot, int position);

    }

    public class PageHolder extends RecyclerView.ViewHolder {

        TextView textViewPageName;
        TextView textViewPageInfo;
        Button follow, following, requested;
        CircleImageView pageImageView;


        public PageHolder(@NonNull View itemView) {
            super(itemView);

            textViewPageName = (itemView).findViewById(R.id.pagename);
            textViewPageInfo = (itemView).findViewById(R.id.bio);
            pageImageView = (itemView).findViewById(R.id.userpageprofileimg);
            follow = (itemView).findViewById(R.id.followbutton);
            following = (itemView).findViewById(R.id.followingbutton);
            requested = (itemView).findViewById(R.id.requestbutton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remember to implement
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && followButtonClickListener != null) {

                        followButtonClickListener.onFollowButtonClick(getSnapshots().getSnapshot(position), position);

                    }

                }
            });


           /* following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remember to implement
                    int position = getAdapterPosition();

                   }
            });

            requested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remember to implement
                    int position = getAdapterPosition();

                   }
            });*/
        }
    }
}
