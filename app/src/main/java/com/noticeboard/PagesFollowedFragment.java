package com.noticeboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagesFollowedFragment extends Fragment {

    //PageUserAdapter userAdapterFollowing;
    View v;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), pageID, pname, pinfo, administratorUID, privacy, username, level, phonenumber;
    RelativeLayout relativeLayout;
    OnItemClickListener listener;
    OnFollowButtonClickListener followButtonClickListener;
    private FirestoreRecyclerAdapter adapter;

    public PagesFollowedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pages_followed, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        relativeLayout = v.findViewById(R.id.followpageRL);
        setUpPagesFollowedRecyclerView();
        //recyclerViewOnClick();
    }

    private void setUpPagesFollowedRecyclerView() {

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().size() > 0) {

                        relativeLayout.setVisibility(View.GONE);

                    } else {

                        relativeLayout.setVisibility(View.VISIBLE);

                    }
                }

            }
        });

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").orderBy("pagename", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(query, PageDetails.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<PageDetails, FollowingPageHolder>(options) {
            @NonNull
            @Override
            public FollowingPageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_tile_user, parent, false);
                return new FollowingPageHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FollowingPageHolder holder, int position, @NonNull PageDetails model) {

                String pagename = model.getPagename();
                String pageinfo = model.getPageinfo();
                String pageID = model.getPageID();
                String adminUID = model.getUserID();

                holder.follow.setVisibility(View.VISIBLE);

                DocumentReference followedpage = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
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

                DocumentReference requestedpage = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Requested").document(userID);
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


                holder.textViewPageName.setText(pagename);
                holder.textViewPageInfo.setText(pageinfo);

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

                // Picasso.get().load(model.getImage(().placeholder(R.drawable.some_drawable).into(ourimageview)


            }
        };

        recyclerView = v.findViewById(R.id.PagesFollowedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;
    }

    public void setOnFollowButtonClickListener(OnFollowButtonClickListener followButtonClickListener) {

        this.followButtonClickListener = followButtonClickListener;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public interface OnItemClickListener {

        void onItemClick(View documentSnapshot, int position);
    }


    public interface OnFollowButtonClickListener {

        void onFollowButtonClick(DocumentSnapshot documentSnapshot, int position);

    }

    private class FollowingPageHolder extends RecyclerView.ViewHolder {

        TextView textViewPageName;
        TextView textViewPageInfo;
        CircleImageView pageImageView;
        Button follow, following, requested;

        public FollowingPageHolder(@NonNull View itemView) {
            super(itemView);

            textViewPageName = (itemView).findViewById(R.id.pagename);
            textViewPageInfo = (itemView).findViewById(R.id.bio);
            pageImageView = (itemView).findViewById(R.id.pageprofileimg);
            follow = (itemView).findViewById(R.id.followbutton);
            following = (itemView).findViewById(R.id.followingbutton);
            requested = (itemView).findViewById(R.id.requestbutton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null) {

                        listener.onItemClick(v, getAdapterPosition());

                    }

                }
            });
        }
    }
}
