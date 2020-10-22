package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UserPagePosts extends AppCompatActivity {

    RelativeLayout forrequested, fornonfollowers;
    String userID, pageID, adminUID;
    PostAdapter postAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page_posts);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        forrequested = findViewById(R.id.requestedfollowers);
        fornonfollowers = findViewById(R.id.nonfollowersview);


        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        pageID = intent.getStringExtra("pageID");
        adminUID = intent.getStringExtra("AdminUserID");

        displayPagePosts();
        fornonfollowers.setVisibility(View.VISIBLE);

        DocumentReference if_requested = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Requested").document(userID);
        if_requested.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    forrequested.setVisibility(View.VISIBLE);
                    fornonfollowers.setVisibility(View.GONE);
                }
            }
        });

        DocumentReference if_follower = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
        if_follower.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //display posts
                    //displayPagePosts();
                    fornonfollowers.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayPagePosts() {

        Query query = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Posts").orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PostDetails> options = new FirestoreRecyclerOptions.Builder<PostDetails>()
                .setQuery(query, PostDetails.class)
                .build();

        postAdapter = new PostAdapter(options);

        recyclerView = findViewById(R.id.userPagePostsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
