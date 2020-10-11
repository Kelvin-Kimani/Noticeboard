package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PageFollowers extends AppCompatActivity {

    String pageID, followerUserID, associatorID, adminUID;
    FollowersAdapter followersAdapter;
    RecyclerView recyclerview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_followers);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        adminUID = intent.getStringExtra("pageAdminUID");

        getSupportActionBar().setTitle(" Followers");

        associatorID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setUpFollowersRecyclerView();

        followersAdapter.setOnItemClickListener(new FollowersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                UserDetails user = documentSnapshot.toObject(UserDetails.class);
                followerUserID = user.getUserID();


                Intent intent = new Intent(PageFollowers.this, UserPage.class);
                intent.putExtra("userID", followerUserID);

                startActivity(intent);


            }
        });

    }

    private void setUpFollowersRecyclerView() {

        Query query = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").orderBy("fullname", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<UserDetails> options = new FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(query, UserDetails.class)
                .build();

        followersAdapter = new FollowersAdapter(options);

        recyclerview = findViewById(R.id.followersrecyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setAdapter(followersAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        followersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        followersAdapter.stopListening();
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

    @Override
    public void onBackPressed() {

        finish();

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
