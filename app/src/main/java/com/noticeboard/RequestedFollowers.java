package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class RequestedFollowers extends AppCompatActivity {

    String pageID, userID, requestedID, pageAdminID;
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
        pageAdminID = intent.getStringExtra("pageAdminID");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setUpFollowersRequest();

        requestedFollowersAdapter.setOnAcceptClickListener(new RequestedFollowersAdapter.OnAcceptClickListener() {
            @Override
            public void onAcceptClick(DocumentSnapshot documentSnapshot, int position) {
                UserDetails user = documentSnapshot.toObject(UserDetails.class);
                requestedID = user.getUserID();

                //Add to follower
                DocumentReference userDetails = FirebaseFirestore.getInstance().collection("Users").document(requestedID);
                userDetails.addSnapshotListener(RequestedFollowers.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        String username = documentSnapshot.getString("fullname");
                        String level = documentSnapshot.getString("level");
                        String userimage = documentSnapshot.getString("userimage");

                        DocumentReference follower = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Followers").document(requestedID);
                        follower.set(new UserDetails(username, level, requestedID, userimage)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RequestedFollowers.this, "Accepted", Toast.LENGTH_SHORT).show();

                                    //Remove as Requested
                                    DocumentReference remove = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Requested").document(requestedID);
                                    remove.delete();
                                }
                            }
                        });

                    }
                });
            }
        });

        requestedFollowersAdapter.setOnCancelClickListener(new RequestedFollowersAdapter.OnCancelClickListener() {
            @Override
            public void onCancelClick(DocumentSnapshot documentSnapshot, int position) {

                UserDetails user = documentSnapshot.toObject(UserDetails.class);
                requestedID = user.getUserID();

                DocumentReference remove = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Requested").document(requestedID);
                remove.delete();
                Toast.makeText(RequestedFollowers.this, "Request Cancelled", Toast.LENGTH_SHORT).show();

            }
        });


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