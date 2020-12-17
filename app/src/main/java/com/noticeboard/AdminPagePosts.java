package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminPagePosts extends AppCompatActivity {

    String page_name, pageID, pageAdminID;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String postID, postTitle, postContent, postTime, postersID;
    TextView noposts;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference postref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_posts_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Posts");

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        pageID = intent.getStringExtra("pageID");
        pageAdminID = intent.getStringExtra("pageAdminID");
        noposts = findViewById(R.id.noposts);

        postref = firestore.collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts");

        setUpRecyclerView();
        onPostClick();
    }

    private void onPostClick() {

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

        Task<QuerySnapshot> queryforemptiness = postref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        noposts.setVisibility(View.GONE);
                    } else {
                        noposts.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        Query query = postref.orderBy("timestamp", Query.Direction.DESCENDING);

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
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
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
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchData(query);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

                return false;
            }
        });
        return true;
    }

    private void searchData(String query) {

        FirestoreRecyclerOptions<PostDetails> options = new FirestoreRecyclerOptions.Builder<PostDetails>()
                .setQuery(FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").orderBy("title", Query.Direction.ASCENDING).startAt(query).endAt(query + "\uf8ff"), PostDetails.class)
                .build();

        postAdapter = new PostAdapter(options);
        postAdapter.startListening();
        recyclerView.setAdapter(postAdapter);
        onPostClick();
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
