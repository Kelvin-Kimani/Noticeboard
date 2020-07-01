package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AddAssociators extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    AddAssociatorAdapter addAssociatorAdapter;
    String userID, pageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_associators);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose from the followers");

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        userID = intent.getStringExtra("userID");

        floatingActionButton = findViewById(R.id.addassociatorfab);

        setUpFollowersRecyclerView();

    }

    private void setUpFollowersRecyclerView() {

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Followers").orderBy("fullname", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<UserDetails> options = new FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(query, UserDetails.class)
                .build();

        addAssociatorAdapter = new AddAssociatorAdapter(options);
        recyclerView = findViewById(R.id.addAssociatorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(addAssociatorAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addAssociatorAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        addAssociatorAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public void onBackPressed() {

        finish();

    }
}
