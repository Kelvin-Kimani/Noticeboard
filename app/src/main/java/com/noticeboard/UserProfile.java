package com.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    CircleImageView circleImageView;
    TextView username, level, pages_created, pages_followed, associated_pages;
    FirebaseFirestore firestore;
    String userID =  FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference imageref;
    Uri imageUri;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        circleImageView = findViewById(R.id.userprofileimg);
        username = findViewById(R.id.username);
        level = findViewById(R.id.level);
        floatingActionButton = findViewById(R.id.fab);
        pages_created = findViewById(R.id.noofpagescreated);
        pages_followed = findViewById(R.id.noofpagesfollowed);
        associated_pages = findViewById(R.id.noofassociatedpages);

        //edit profile pic
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        imageref = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("User Profiles");


        // retrieve user details
        DocumentReference documentReference = firestore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                username.setText(documentSnapshot.getString("fullname"));
                level.setText(documentSnapshot.getString("level"));

            }
        });


        final CollectionReference pagescreated = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages");
        pagescreated.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        pages_created.setText(Integer.toString(task.getResult().size()));
                    }
                }
            }
        });

        CollectionReference pagesfollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed");
        pagesfollowed.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        pages_followed.setText(Integer.toString(task.getResult().size()));
                    }
                }
            }
        });

        CollectionReference associatedpages = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Associated Pages");
        associatedpages.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    associated_pages.setText(Integer.toString(task.getResult().size()));
                }
            }
        });


        retrieveUserImage();
    }

    private void updateProfile() {

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

                StorageReference filereference = storageReference.child(userID + ".jpg");

                filereference.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(UserProfile.this, "Profile Updated", Toast.LENGTH_LONG).show();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();
                                final String imageurl = String.valueOf(downloadUrl);

                                imageref.child("Users Profiles").child(userID).child("image").child("userimage")
                                        .setValue(imageurl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    DocumentReference userImageRef = FirebaseFirestore.getInstance().collection("Users").document(userID);
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("userimage", imageurl);
                                                    userImageRef.update(map);

                                                    Toast.makeText(UserProfile.this, "Image Url Saved too!", Toast.LENGTH_LONG).show();
                                                } else {

                                                    String message = task.getException().toString();
                                                    Toast.makeText(UserProfile.this, "Error:" + message, Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(UserProfile.this, "Updating Profile", Toast.LENGTH_LONG).show();
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Task task = null;
                                String message = task.getException().toString();
                                Toast.makeText(UserProfile.this, "Error:" + message, Toast.LENGTH_LONG).show();

                            }
                        });
            }
        }
    }

    private void retrieveUserImage() {

        imageref.child("Users Profiles").child(userID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))) {

                            String imageurl = dataSnapshot.child("image").child("userimage").getValue().toString();
                            Picasso.get().load(imageurl).into(circleImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void openPagesFollowing(View view) {

        int page = 1;
        Intent intent = new Intent(UserProfile.this, Pages.class);
        intent.putExtra("IntendedPage", page);
        startActivity(intent);

    }

    public void openAssociationPages(View view) {

        int page = 2;
        Intent intent = new Intent(UserProfile.this, Pages.class);
        intent.putExtra("IntendedPage", page);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }


    public void openEditUser(MenuItem item) {

        startActivity(new Intent(UserProfile.this, EditUser.class));

    }

    public void openCreatedPages(View view) {

        startActivity(new Intent(UserProfile.this, Pages.class));

    }
}
