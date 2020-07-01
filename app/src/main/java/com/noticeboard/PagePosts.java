package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PagePosts extends AppCompatActivity {

    String page_name, pageID;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    String userID;
    String postID, postTitle, postContent;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference postref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_posts_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        pageID = intent.getStringExtra("pageID");
        getSupportActionBar().setTitle(page_name + " Posts");


        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postref = firestore.collection("Users").document(userID).collection("Pages").document(pageID).collection("Posts");

        setUpRecyclerView();

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                postID = documentSnapshot.getId();
                postTitle = post.getTitle();
                postContent = post.getContent();

                Toast.makeText(PagePosts.this,
                        "Position: " + position + " ID: " + postID, Toast.LENGTH_LONG).show();

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

        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);


    }

    public void openPostPage(View view) {

        Intent intent = new Intent(PagePosts.this, PostPage.class);

        intent.putExtra("pageID", pageID);
        intent.putExtra("pagename", page_name);
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

      /*  SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));*/

        return true;
    }
}
