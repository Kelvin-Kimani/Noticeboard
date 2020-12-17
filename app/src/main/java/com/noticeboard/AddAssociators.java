package com.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AddAssociators extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    FollowersAdapter followersAdapter;
    String userID, pageID, followerUserID, followerUsername, pagename, pageinfo, privacy;
    Dialog dialog;
    RelativeLayout relativeLayout;
    Bundle bundleRecyclerViewState;
    private Parcelable recyclerstate = null;
    private final String KEY_RECYCLER_STATE = "recycler_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_associators);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Add Associator");

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        userID = intent.getStringExtra("userID");
        relativeLayout = findViewById(R.id.nofollowersRL);

        floatingActionButton = findViewById(R.id.addassociatorfab);

        setUpFollowersRecyclerView();

        followersAdapter.setOnItemClickListener(new FollowersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                UserDetails user = documentSnapshot.toObject(UserDetails.class);
                followerUserID = user.getUserID();
                followerUsername = user.getFullname();

                associatorPopUp();

            }
        });

        DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
        page.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                pagename = documentSnapshot.getString("pagename");
                pageinfo = documentSnapshot.getString("pageinfo");
                privacy = documentSnapshot.getString("privacy");
            }
        });

    }

    private void associatorPopUp() {

        final RelativeLayout viewUser, makeAssociator, dismissAssociator;
        TextView username;

        dialog.setContentView(R.layout.addassociator_pop_up);
        username = dialog.findViewById(R.id.viewText);
        username.setText("View " + followerUsername);

        viewUser = dialog.findViewById(R.id.viewprofile);
        makeAssociator = dialog.findViewById(R.id.makeAssociator);
        dismissAssociator = dialog.findViewById(R.id.dismissAssociator);

        DocumentReference checkIfAssociator = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Associators").document(followerUserID);
        checkIfAssociator.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dismissAssociator.setVisibility(View.VISIBLE);
                        makeAssociator.setVisibility(View.GONE);
                    }
                }
            }
        });

        viewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AddAssociators.this, UserPage.class);
                intent.putExtra("userID", followerUserID);

                startActivity(intent);
                dialog.dismiss();
            }
        });

        makeAssociator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
                page.addSnapshotListener(AddAssociators.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        String pagename = documentSnapshot.getString("pagename");
                        String pageinfo = documentSnapshot.getString("pageinfo");
                        String pageimage = documentSnapshot.getString("pageimage");
                        String AdminUID = documentSnapshot.getString("userID");
                        String privacy = documentSnapshot.getString("privacy");

                        DocumentReference associatedPage = FirebaseFirestore.getInstance().collection("Users").document(followerUserID).collection("Associated Pages").document(pageID);
                        associatedPage.set(new PageDetails(pagename, pageinfo, privacy, pageID, AdminUID, pageimage));

                        DocumentReference followedPage = FirebaseFirestore.getInstance().collection("Users").document(followerUserID).collection("PagesFollowed").document(pageID);
                        followedPage.delete();

                        DocumentReference getUserDetails = FirebaseFirestore.getInstance().collection("Users").document(followerUserID);
                        getUserDetails.addSnapshotListener(AddAssociators.this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                final String username = documentSnapshot.getString("fullname");
                                String level = documentSnapshot.getString("level");
                                String userImage = documentSnapshot.getString("userimage");

                                DocumentReference associators = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Associators").document(followerUserID);

                                UserDetails associator = new UserDetails();

                                associator.setFullname(username);
                                associator.setLevel(level);
                                associator.setUserID(followerUserID);
                                associator.setUserimage(userImage);
                                associator.setSearch_user(username.toLowerCase());

                                associators.set(associator).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(AddAssociators.this, username + " is now an Associator", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                    }
                });

            }
        });

        dismissAssociator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference getAssociator = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Associators").document(followerUserID);
                getAssociator.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddAssociators.this, "Removed as Associator", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            DocumentReference removeAssociationPage = FirebaseFirestore.getInstance().collection("Users").document(followerUserID).collection("Associated Pages").document(pageID);
                            removeAssociationPage.delete();

                            //Add Followed Page
                            DocumentReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(followerUserID).collection("PagesFollowed").document(pageID);
                            pagefollowed.set(new PageDetails(pagename, pageinfo, privacy, pageID, userID));
                        }
                    }
                });

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void setUpFollowersRecyclerView() {

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Followers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().size() > 0) {

                        relativeLayout.setVisibility(View.GONE);

                    } else {

                        relativeLayout.setVisibility(View.VISIBLE);

                    }
                }

            }
        });

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Followers").orderBy("fullname", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<UserDetails> options = new FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(query, UserDetails.class)
                .build();

        followersAdapter = new FollowersAdapter(options);
        recyclerView = findViewById(R.id.addAssociatorRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(followersAdapter);
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
    public void onPause() {
        super.onPause();
        bundleRecyclerViewState = new Bundle();

        recyclerstate = recyclerView.getLayoutManager().onSaveInstanceState();

        bundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, recyclerstate);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    recyclerstate = bundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerstate);

                }
            }, 50);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void searchData(String query) {

        FirestoreRecyclerOptions<UserDetails> options = new FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Followers").orderBy("fullname", Query.Direction.ASCENDING).startAt(query).endAt(query + "\uf8ff"), UserDetails.class)
                .build();

        followersAdapter = new FollowersAdapter(options);
        followersAdapter.startListening();
        recyclerView.setAdapter(followersAdapter);

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
