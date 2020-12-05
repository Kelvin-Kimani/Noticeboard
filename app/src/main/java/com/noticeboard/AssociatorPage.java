package com.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssociatorPage extends AppCompatActivity {

    String pageID, adminUID, pname, pinfo, privacy;
    TextView pagename, pageinfo, posts_no, followers_no, requests_no;
    Dialog dialog;
    RadioGroup group;
    RadioButton radioButton;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associator_page);
        dialog = new Dialog(this);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        adminUID = intent.getStringExtra("adminUID");

        pagename = findViewById(R.id.pagename);
        pageinfo = findViewById(R.id.bio);
        circleImageView = findViewById(R.id.pageprofileimg);
        followers_no = findViewById(R.id.nooffollowers);
        posts_no = findViewById(R.id.noofposts);
        requests_no = findViewById(R.id.noofrequests);


        DocumentReference pageDetails = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID);
        pageDetails.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                pname = documentSnapshot.getString("pagename");
                pinfo = documentSnapshot.getString("pageinfo");
                pagename.setText(pname);
                pageinfo.setText(pinfo);


            }
        });

        CollectionReference page_posts = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Posts");
        page_posts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                   posts_no.setText(Integer.toString(task.getResult().size()));
                }
            }
        });

        CollectionReference page_followers = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers");
        page_followers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    followers_no.setText(Integer.toString(task.getResult().size()));
                }
            }
        });

        CollectionReference page_requests = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Requested");
        page_requests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    requests_no.setText(Integer.toString(task.getResult().size()));
                }
            }
        });

        retrievePageImage();

    }

    private void retrievePageImage() {

        FirebaseDatabase.getInstance().getReference().child("Users Profiles").child(adminUID).child("Pages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild(pageID))) {

                            String imageurl = dataSnapshot.child(pageID).child("PageProfile").getValue().toString();
                            Picasso.get().load(imageurl).into(circleImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void openFollowers(View view) {

        Intent intent = new Intent(AssociatorPage.this, PageFollowers.class);
        intent.putExtra("pageID", pageID);
        intent.putExtra("pageAdminUID", adminUID);

        startActivity(intent);

    }

    public void openFollowRequests(View view) {

    }

    public void openPagePosts(View view) {

        Intent intent = new Intent(AssociatorPage.this, AdminPagePosts.class);
        intent.putExtra("pagename", pname);
        intent.putExtra("pageID", pageID);
        intent.putExtra("pageAdminID", adminUID);
        startActivity(intent);

    }

    public void popupprivacy(View view) {

        final RadioButton privateButton, publicButton;
        RelativeLayout relativeLayout;

        dialog.setContentView(R.layout.page_privacy_pop_up);

        privateButton = dialog.findViewById(R.id.privateradiobutton);
        publicButton = dialog.findViewById(R.id.publicradiobutton);
        relativeLayout = dialog.findViewById(R.id.updateRL);
        group = dialog.findViewById(R.id.radiogroup);

        DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID);
        page.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                privacy = documentSnapshot.getString("privacy");

                if (privacy.equals("Private")) {

                    privateButton.setChecked(true);

                } else {
                    publicButton.setChecked(true);
                }

            }
        });


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int radioID = group.getCheckedRadioButtonId();
                radioButton = dialog.findViewById(radioID);

                final CharSequence chosenPrivacy = radioButton.getText();

                if (chosenPrivacy.equals(privacy)) {

                    dialog.dismiss();

                } else {

                    DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID);
                    page.update("privacy", chosenPrivacy).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                final CollectionReference globalPageRef = FirebaseFirestore.getInstance().collection("Global Pages");
                                Query query = globalPageRef.whereEqualTo("pageID", pageID);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot document : task.getResult()) {

                                                globalPageRef.document(document.getId()).update("privacy", chosenPrivacy);

                                                if (chosenPrivacy.equals("Public")){

                                                    //check for pending request


                                                }

                                            }
                                        }
                                    }
                                });

                                Toast.makeText(AssociatorPage.this, "Page is now " + chosenPrivacy, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        }
                    });

                    DocumentReference associatorPage = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Associated Pages").document(pageID);
                    associatorPage.update("privacy", chosenPrivacy);

                    //for following pages (Remember to edit documentID to the original pageID)
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

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
        getMenuInflater().inflate(R.menu.normal_edit, menu);

        return true;
    }

    public void openEditPage(MenuItem item) {

        Intent intent = new Intent(AssociatorPage.this, EditPage.class);
        intent.putExtra("pageID", pageID);
        intent.putExtra("pageinfo", pinfo);
        intent.putExtra("pagename", pname);
        intent.putExtra("adminID", adminUID);

        startActivity(intent);
    }

    public void leaveAssociation(View view) {

        TextView title, text;
        Button yes, no;
        dialog.setContentView(R.layout.custom_pop_up);

        title = dialog.findViewById(R.id.dialogtitle);
        title.setText("Leave Association");
        text = dialog.findViewById(R.id.dialogtext);
        text.setText("You're giving up your priviledge of being an Associator of this page. Do you wish to continue?");

        yes = dialog.findViewById(R.id.positivebutton);
        no = dialog.findViewById(R.id.negativebutton);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference getAssociator = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Associators").document(userID);
                getAssociator.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AssociatorPage.this, "Removed as Associator", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();

                            DocumentReference removeAssociator = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Associated Pages").document(pageID);
                            removeAssociator.delete();

                            //Add Followed Page
                            DocumentReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").document(pageID);
                            pagefollowed.set(new PageDetails(pname, pinfo, privacy, pageID, adminUID));
                        }
                    }
                });
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}
