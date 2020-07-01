package com.noticeboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserPagePosts extends AppCompatActivity {

    RelativeLayout forfollowers, fornonfollowers;
    String userID, pageID, adminUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page_posts);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        forfollowers = findViewById(R.id.followersview);
        fornonfollowers = findViewById(R.id.nonfollowersview);


        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        pageID  = intent.getStringExtra("pageID");
        adminUID = intent.getStringExtra("AdminUserID");

        DocumentReference if_follower = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
        if_follower.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    forfollowers.setVisibility(View.VISIBLE);
                }
                else {
                    fornonfollowers.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
