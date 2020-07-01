package com.noticeboard;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPage extends AppCompatActivity {

    String followeruserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        followeruserID = intent.getStringExtra("userID");


        final CircleImageView userImage = findViewById(R.id.userprofileimg);
        final TextView username = findViewById(R.id.username);
        final TextView userLevel = findViewById(R.id.level);
        final TextView email = findViewById(R.id.email);
        final TextView number = findViewById(R.id.phonenumber);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(followeruserID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                email.setText(documentSnapshot.getString("email"));
                username.setText(documentSnapshot.getString("fullname"));
                number.setText(documentSnapshot.getString("phonenumber"));
                userLevel.setText(documentSnapshot.getString("level"));

            }
        });

        DatabaseReference userProfile = FirebaseDatabase.getInstance().getReference();
        userProfile.child("Users Profiles").child(followeruserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {

                            String imageurl = dataSnapshot.child("image").child("userimage").getValue().toString();
                            Picasso.get().load(imageurl).into(userImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
