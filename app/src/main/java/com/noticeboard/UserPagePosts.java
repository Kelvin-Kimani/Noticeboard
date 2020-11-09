package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class UserPagePosts extends AppCompatActivity {

    RelativeLayout forrequested, fornonfollowers, fornoposts;
    String userID, pageID, adminUID, postID, postTitle, postContent, postTime, postersID, pagename;
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
        fornoposts = findViewById(R.id.noposts);


        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        pageID = intent.getStringExtra("pageID");
        adminUID = intent.getStringExtra("AdminUserID");
        pagename = intent.getStringExtra("pagename");

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
                    fornonfollowers.setVisibility(View.GONE);

                    Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {

                                if (task.getResult().size() == 0) {
                                    fornoposts.setVisibility(View.VISIBLE);
                                }
                                else {
                                    fornoposts.setVisibility(View.GONE);
                                }
                            }

                        }
                    });
                }
            }
        });

        displayPagePosts();

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                postID = documentSnapshot.getId();
                postTitle = post.getTitle();
                postContent = post.getContent();
                postTime = post.getTime();

                DocumentReference getPostersID = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("All Posts").document(postID);
                getPostersID.addSnapshotListener(UserPagePosts.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        postersID = documentSnapshot.getString("postersID");

                        Intent intent1 = new Intent(UserPagePosts.this, PostWithComments.class);
                        intent1.putExtra("pagename", pagename);
                        intent1.putExtra("postTitle", postTitle);
                        intent1.putExtra("postContent", postContent);
                        intent1.putExtra("postTime", postTime);
                        intent1.putExtra("postID", postID);
                        intent1.putExtra("pageAdminID", adminUID);
                        intent1.putExtra("pageID", pageID);
                        intent1.putExtra("postersID", postersID);

                        startActivity(intent1);
                    }
                });
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
