package com.noticeboard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddUserProfile extends AppCompatActivity {

    Button add, cancel;
    FloatingActionButton fab;
    ImageView userprofileimg;
    ProgressBar progressBar;
    StorageReference storageReference;
    DatabaseReference imageref;
    Uri imageUri;
    StorageTask storageTask;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_profile);

        userprofileimg = findViewById(R.id.userprofileimg);
        fab = findViewById(R.id.floatingActionButton);
        add = findViewById(R.id.addbutton);
        cancel = findViewById(R.id.skipbutton);
        progressBar = findViewById(R.id.progressBar);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("User Profiles");
        imageref = FirebaseDatabase.getInstance().getReference();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickImage();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddUserProfile.this, "Okay, No problem", Toast.LENGTH_LONG).show();
                startActivity(new Intent(AddUserProfile.this, MainActivity.class));
                finish();
            }
        });
    }

    public void pickImage() {
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

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    userprofileimg.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                StorageReference filereference = storageReference.child(userID + ".jpg");

                filereference.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(AddUserProfile.this, "Image Uploaded successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(AddUserProfile.this, MainActivity.class));
                                //Avoid going back to the same page on back press
                                finish();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();
                                final String imageurl = String.valueOf(downloadUrl);

                                final UserDetails userImage = new UserDetails(imageurl);
                                imageref.child("Users Profiles").child(userID).child("image")
                                        .setValue(userImage)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    DocumentReference userImageRef = FirebaseFirestore.getInstance().collection("Users").document(userID);
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("userimage", imageurl);
                                                    userImageRef.update(map);

                                                    Toast.makeText(AddUserProfile.this, "Image Url Saved too!", Toast.LENGTH_LONG).show();
                                                } else {

                                                    String message = task.getException().toString();
                                                    Toast.makeText(AddUserProfile.this, "Error:" + message, Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(AddUserProfile.this, "Upload in progress", Toast.LENGTH_LONG).show();
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Task task = null;
                                String message = task.getException().toString();
                                Toast.makeText(AddUserProfile.this, "Error:" + message, Toast.LENGTH_LONG).show();

                            }
                        });
            }
        }
    }
}
