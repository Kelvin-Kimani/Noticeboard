package com.noticeboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageProfileAdmin extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView pagename, pageinfo, posts_no, followers_no, requests_no, associators_no;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String userID;
    String page_name, page_info, pageID, privacyreceived, pageIDOnline;
    Switch notificationswitch;
    Dialog dialog;
    Character firstLetter;
    TextDrawable drawable;
    RadioGroup group;
    RadioButton radioButton;
    Uri imageUri;
    Context context;
    RelativeLayout requested, deletePage;
    private String privacy, adminUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_profile_admin);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        notificationswitch = findViewById(R.id.notificationswitch);
        notificationswitch.setChecked(true);

        circleImageView = findViewById(R.id.pageprofileimg);
        pagename = findViewById(R.id.pagename);
        pageinfo = findViewById(R.id.bio);
        requested = findViewById(R.id.requestedRL);
        deletePage = findViewById(R.id.deletepagerelativelayout);
        posts_no = findViewById(R.id.noofposts);
        followers_no = findViewById(R.id.nooffollowers);
        requests_no = findViewById(R.id.noofrequests);
        associators_no = findViewById(R.id.noofassociators);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        privacy = intent.getStringExtra("privacy");
        adminUserID = intent.getStringExtra("adminUserID");

        DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
        page.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                privacy = documentSnapshot.getString("privacy");

                if (privacy.equals("Private")) {

                    requested.setVisibility(View.VISIBLE);

                }
            }
        });


        DocumentReference documentReference = firestore.collection("Users").document(userID).collection("Pages").document(pageID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                page_name = documentSnapshot.getString("pagename");
                page_info = documentSnapshot.getString("pageinfo");
                privacyreceived = documentSnapshot.getString("privacy");
                pagename.setText(page_name);
                pageinfo.setText(documentSnapshot.getString("pageinfo"));
                pageIDOnline = documentSnapshot.getString("pageID");

            }
        });

        CollectionReference page_posts = FirebaseFirestore.getInstance().collection("Users").document(adminUserID).collection("Pages").document(pageID).collection("Posts");
        page_posts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        posts_no.setText(Integer.toString(task.getResult().size()));
                    }
                }
            }
        });

        CollectionReference page_followers = FirebaseFirestore.getInstance().collection("Users").document(adminUserID).collection("Pages").document(pageID).collection("Followers");
        page_followers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        followers_no.setText(Integer.toString(task.getResult().size()));
                    }
                }
            }
        });

        CollectionReference page_requests = FirebaseFirestore.getInstance().collection("Users").document(adminUserID).collection("Pages").document(pageID).collection("Requested");
        page_requests.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    requests_no.setText(Integer.toString(task.getResult().size()));
                }
            }
        });

        CollectionReference page_associators = FirebaseFirestore.getInstance().collection("Users").document(adminUserID).collection("Pages").document(pageID).collection("Associators");
        page_associators.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    associators_no.setText(Integer.toString(task.getResult().size()));
                }
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updatePageProfile();

            }
        });

        retrievePageImage();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    public void popupprivacy(View view) {

        final RadioButton privateButton, publicButton;
        RelativeLayout relativeLayout;

        dialog.setContentView(R.layout.page_privacy_pop_up);

        privateButton = dialog.findViewById(R.id.privateradiobutton);
        publicButton = dialog.findViewById(R.id.publicradiobutton);
        relativeLayout = dialog.findViewById(R.id.updateRL);
        group = dialog.findViewById(R.id.radiogroup);

        DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
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

                    //if page is changing from private to public, delete all follow requests.
                    DocumentReference page = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
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

                                            }
                                        }
                                    }
                                });

                                Toast.makeText(PageProfileAdmin.this, "Page is now " + chosenPrivacy, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        }
                    });

                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void retrievePageImage() {

        FirebaseDatabase.getInstance().getReference().child("Users Profiles").child(userID).child("Pages")
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

    private void updatePageProfile() {

        CropImage.startPickImageActivity(this);
    }

    public void cropImageRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            imageUri = CropImage.getPickImageResultUri(this, data);
            cropImageRequest(imageUri);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                StorageReference filereference = FirebaseStorage.getInstance().getReference().child("Page Profiles").child(pageID + ".jpg");

                filereference.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(PageProfileAdmin.this, "Profile Updated", Toast.LENGTH_LONG).show();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();
                                final String imageurl = String.valueOf(downloadUrl);

                                FirebaseDatabase.getInstance().getReference().child("Users Profiles").child(userID).child("Pages").child(pageID).child("PageProfile")
                                        .setValue(imageurl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    DocumentReference pageImageRef = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
                                                    PageDetails pageimageupdate = new PageDetails(page_name, page_info, privacyreceived, pageID, userID, imageurl);
                                                    pageImageRef.set(pageimageupdate);

                                                } else {

                                                    String message = task.getException().toString();
                                                    Toast.makeText(PageProfileAdmin.this, "Error:" + message, Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(PageProfileAdmin.this, "Updating Profile", Toast.LENGTH_LONG).show();
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Task task = null;
                                String message = task.getException().toString();
                                Toast.makeText(PageProfileAdmin.this, "Error:" + message, Toast.LENGTH_LONG).show();

                            }
                        });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.normal_edit, menu);

        return true;
    }

    public void openPagePosts(View view) {

        Intent intent = new Intent(PageProfileAdmin.this, AdminPagePosts.class);
        intent.putExtra("pagename", page_name);
        intent.putExtra("pageID", pageID);
        intent.putExtra("pageAdminID", userID);
        startActivity(intent);

    }


    public void openFollowers(View view) {

        Intent intent = new Intent(PageProfileAdmin.this, PageFollowers.class);
        intent.putExtra("pageID", pageID);
        intent.putExtra("pageAdminUID", adminUserID);

        startActivity(intent);


    }

    public void deletePage(View view) {

        showDeleteWarningPopUp(view);

    }

    private void showDeleteWarningPopUp(final View view) {

        TextView title, text;
        Button yes, no;
        dialog.setContentView(R.layout.custom_pop_up);

        title = dialog.findViewById(R.id.dialogtitle);
        title.setText("Delete Page");
        text = dialog.findViewById(R.id.dialogtext);
        text.setText(getString(R.string.deletePage));

        yes = dialog.findViewById(R.id.positivebutton);
        no = dialog.findViewById(R.id.negativebutton);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PageProfileAdmin.this, Pages.class);
                //makesure user cant go back
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                //delete a page from admin side
                DocumentReference pageRef = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
                pageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(view, "Product deleted!", Snackbar.LENGTH_LONG).show();
                    }
                });

                //for deleting all posts from the page
                final CollectionReference pagePostsRef = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("All Posts");
                Query query = pagePostsRef.whereEqualTo("pageID", pageID);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                pagePostsRef.document(document.getId()).delete();
                            }
                        }
                    }
                });

                //deleting a page globally
                final CollectionReference globalPageRef = FirebaseFirestore.getInstance().collection("Global Pages");
                Query query1 = globalPageRef.whereEqualTo("pageID", pageID);
                query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                globalPageRef.document(document.getId()).delete();
                            }
                        }
                    }
                });

                //Delete followers
                CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID).collection("Followers");

                // yet to add delete users, associators and followers

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void openAssociators(View view) {

        Intent intent = new Intent(PageProfileAdmin.this, PageAssociators.class);
        intent.putExtra("pagename", page_name);
        intent.putExtra("pageID", pageID);
        intent.putExtra("userID", userID);
        startActivity(intent);

    }

    public void openEditPage(MenuItem item) {

        Intent intent = new Intent(PageProfileAdmin.this, EditPage.class);
        intent.putExtra("pageID", pageID);
        intent.putExtra("pageinfo", page_info);
        intent.putExtra("pagename", page_name);
        intent.putExtra("adminID", adminUserID);

        startActivity(intent);

    }

    public void openFollowRequests(View view) {

        Intent intent = new Intent(PageProfileAdmin.this, RequestedFollowers.class);
        intent.putExtra("pageID", pageID);
        intent.putExtra("pageAdminID", adminUserID);
        intent.putExtra("pagename", page_name);
        intent.putExtra("pageinfo", page_info);
        intent.putExtra("privacy", privacy);

        startActivity(intent);
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
