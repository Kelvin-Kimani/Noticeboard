package com.noticeboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RequestedFollowers extends AppCompatActivity {

    String pageID, userID;
    RecyclerView requestingFollowers;
    RequestedFollowersAdapter requestedFollowersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_followers);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Follow Requests");

        requestingFollowers = findViewById(R.id.requestedfollowersRV);

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setUpFollowersRequest();


    }

    private void setUpFollowersRequest() {

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Requested").orderBy("fullname", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<UserDetails> options = new FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(query, UserDetails.class)
                .build();

        requestedFollowersAdapter = new RequestedFollowersAdapter(options);

        requestingFollowers.setLayoutManager(new LinearLayoutManager(this));
        requestingFollowers.setAdapter(requestedFollowersAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestedFollowersAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        requestedFollowersAdapter.stopListening();
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