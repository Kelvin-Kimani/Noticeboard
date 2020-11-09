package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class AdminPagePosts extends AppCompatActivity {

    String page_name, pageID, pageAdminID;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String postID, postTitle, postContent, postTime, postersID;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference postref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_posts_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        pageID = intent.getStringExtra("pageID");
        pageAdminID = intent.getStringExtra("pageAdminID");

        getSupportActionBar().setTitle(page_name + " Posts");

        postref = firestore.collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts");

        setUpRecyclerView();

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                postID = documentSnapshot.getId();
                postTitle = post.getTitle();
                postContent = post.getContent();
                postTime = post.getTime();

                DocumentReference getPostersID = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("All Posts").document(postID);
                getPostersID.addSnapshotListener(AdminPagePosts.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        postersID = documentSnapshot.getString("postersID");

                        Intent intent1 = new Intent(AdminPagePosts.this, PostWithComments.class);
                        intent1.putExtra("pagename", page_name);
                        intent1.putExtra("postTitle", postTitle);
                        intent1.putExtra("postContent", postContent);
                        intent1.putExtra("postTime", postTime);
                        intent1.putExtra("postID", postID);
                        intent1.putExtra("pageAdminID", pageAdminID);
                        intent1.putExtra("pageID", pageID);
                        intent1.putExtra("postersID", postersID);

                        startActivity(intent1);

                    }
                });


            }
        });

    }

    private void setUpRecyclerView() {

        Query query = postref.orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PostDetails> options = new FirestoreRecyclerOptions.Builder<PostDetails>()
                .setQuery(query, PostDetails.class)
                .build();

        postAdapter = new PostAdapter(options);

        recyclerView = findViewById(R.id.pagepostsrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);
    }

    public void openPostPage(View view) {

        Intent intent = new Intent(AdminPagePosts.this, PostPage.class);

        intent.putExtra("pageID", pageID);
        intent.putExtra("pagename", page_name);
        intent.putExtra("pageAdminID", pageAdminID);
        startActivity(intent);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
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
