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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class PageFollowers extends AppCompatActivity {

    String pageID, followerUserID, associatorID, adminUID;
    FollowersAdapter followersAdapter;
    RecyclerView recyclerview;
    TextView nofollowers;

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
        nofollowers = findViewById(R.id.nofollowers);

        setUpFollowersRecyclerView();
        recyclerItemsOnClick();

    }

    private void recyclerItemsOnClick() {

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

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {

                        nofollowers.setVisibility(View.GONE);
                    } else {

                        nofollowers.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

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

        FirestoreRecyclerOptions<UserDetails> options = new FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").orderBy("fullname", Query.Direction.ASCENDING).startAt(query).endAt(query + "\uf8ff"), UserDetails.class)
                .build();

        followersAdapter = new FollowersAdapter(options);
        followersAdapter.startListening();
        recyclerview.setAdapter(followersAdapter);
        recyclerItemsOnClick();

    }

}
