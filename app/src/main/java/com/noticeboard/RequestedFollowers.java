package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
import com.google.firebase.firestore.QuerySnapshot;

public class RequestedFollowers extends AppCompatActivity {

    private static final String KEY_RECYCLER_STATE = "recycler_state";
    String pageID, userID, requestedID, pageAdminID, pagename, pageinfo, privacy;
    RecyclerView requestingFollowers;
    RequestedFollowersAdapter requestedFollowersAdapter;
    TextView textView;
    private Bundle bundleRecyclerViewState;
    private Parcelable recyclerstate = null;


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
        pagename = intent.getStringExtra("pagename");
        pageinfo = intent.getStringExtra("pageinfo");
        privacy = intent.getStringExtra("privacy");

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        textView = findViewById(R.id.nofollowrequest);

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

                        DocumentReference followers = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Followers").document(requestedID);

                        UserDetails follower = new UserDetails();

                        follower.setFullname(username);
                        follower.setLevel(level);
                        follower.setUserID(requestedID);
                        follower.setEmail(userimage);
                        follower.setSearch_user(username.toLowerCase());

                        followers.set(follower).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RequestedFollowers.this, "Accepted", Toast.LENGTH_SHORT).show();

                                    //Remove from Requested
                                    DocumentReference remove = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Requested").document(requestedID);
                                    remove.delete();

                                    //Add page to followed pages
                                    DocumentReference addpage = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").document(pageID);
                                    addpage.set(new PageDetails(pagename, pageinfo, privacy, pageID, pageAdminID));

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

        requestedFollowersAdapter.setOnItemClickListener(new RequestedFollowersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                UserDetails user = documentSnapshot.toObject(UserDetails.class);
                requestedID = user.getUserID();

                //can do as a fragment on free time
                Intent intent1 = new Intent(RequestedFollowers.this, UserPage.class);
                intent1.putExtra("userID", requestedID);
                startActivity(intent1);
            }
        });


    }

    private void setUpFollowersRequest() {

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Requested").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {

                        textView.setVisibility(View.GONE);
                    } else {

                        textView.setVisibility(View.VISIBLE);

                    }

                }
            }
        });

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
    protected void onPause() {
        super.onPause();
        bundleRecyclerViewState = new Bundle();

        recyclerstate = requestingFollowers.getLayoutManager().onSaveInstanceState();

        bundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, recyclerstate);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    recyclerstate = bundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    requestingFollowers.getLayoutManager().onRestoreInstanceState(recyclerstate);

                }
            }, 50);
        }


        requestingFollowers.setLayoutManager(new LinearLayoutManager(this));
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