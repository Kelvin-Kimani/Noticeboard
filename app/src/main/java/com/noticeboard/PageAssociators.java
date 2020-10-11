package com.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class PageAssociators extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    String page_name, pageID, userID, associatorID, associatorName, pageinfo, privacy;
    FollowersAdapter adapter;
    RecyclerView associators;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_associators);
        dialog = new Dialog(this);

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        pageID = intent.getStringExtra("pageID");
        userID = intent.getStringExtra("userID");

        getSupportActionBar().setTitle(page_name + " Associators");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
        page.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                pageinfo = documentSnapshot.getString("pageinfo");
                privacy = documentSnapshot.getString("privacy");
            }
        });
        floatingActionButton = findViewById(R.id.addassociatorfab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(PageAssociators.this, AddAssociators.class);
                intent1.putExtra("pageID", pageID);
                intent1.putExtra("userID", userID);

                startActivity(intent1);

            }
        });

        setUpAssociatorsRecyclerview();

        adapter.setOnItemClickListener(new FollowersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                UserDetails user = documentSnapshot.toObject(UserDetails.class);
                associatorID = user.getUserID();
                associatorName = user.getFullname();

                associatorPopUp();
            }
        });
    }

    private void associatorPopUp() {

        final RelativeLayout viewUser, makeAssociator, dismissAssociator;
        TextView username;

        dialog.setContentView(R.layout.addassociator_pop_up);
        username = dialog.findViewById(R.id.viewText);
        username.setText("View " + associatorName);

        viewUser = dialog.findViewById(R.id.viewprofile);
        makeAssociator = dialog.findViewById(R.id.makeAssociator);
        dismissAssociator = dialog.findViewById(R.id.dismissAssociator);

        DocumentReference checkIfAssociator = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Associators").document(associatorID);
        checkIfAssociator.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        makeAssociator.setVisibility(View.GONE);
                        dismissAssociator.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        viewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PageAssociators.this, UserPage.class);
                intent.putExtra("userID", associatorID);

                startActivity(intent);
                dialog.dismiss();
            }
        });

        dismissAssociator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference getAssociator = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Associators").document(associatorID);
                getAssociator.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PageAssociators.this, "Removed as Associator", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            DocumentReference removeAssociator = FirebaseFirestore.getInstance().collection("Users").document(associatorID).collection("Associated Pages").document(pageID);
                            removeAssociator.delete();

                            //Add Followed Page
                            DocumentReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(associatorID).collection("PagesFollowed").document(pageID);
                            pagefollowed.set(new PageDetails(page_name, pageinfo, privacy, pageID, userID));
                        }
                    }
                });

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void setUpAssociatorsRecyclerview() {

        /*Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("All Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        });*/

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Associators").orderBy("fullname", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<UserDetails> options = new FirestoreRecyclerOptions.Builder<UserDetails>()
                .setQuery(query, UserDetails.class)
                .build();

        adapter = new FollowersAdapter(options);
        associators = findViewById(R.id.associatorsrecyclerview);
        associators.setLayoutManager(new LinearLayoutManager(this));
        associators.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
