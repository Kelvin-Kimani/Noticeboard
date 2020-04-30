package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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

        userprofileimg =(ImageView) findViewById(R.id.userprofileimg);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        add = (Button) findViewById(R.id.addbutton);
        cancel = (Button) findViewById(R.id.skipbutton);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

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

    public void cropImageRequest(Uri imageUri){
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


                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful());
                                Uri downloadUrl = urlTask.getResult();
                                String imageurl = String.valueOf(downloadUrl);

                                imageref.child("Users Profiles").child(userID).child("image")
                                        .setValue(imageurl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){
                                                    Toast.makeText(AddUserProfile.this, "Image Url Saved too!", Toast.LENGTH_LONG).show();
                                                }
                                                else{

                                                    String message = task.getException().toString();
                                                    Toast.makeText(AddUserProfile.this, "Error:"+message, Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });

                        }
                    })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(AddUserProfile.this, "Upload in progress", Toast.LENGTH_LONG).show();
                    double progress = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    }
                })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Task task = null;
                    String message = task.getException().toString();
                    Toast.makeText(AddUserProfile.this, "Error:"+message, Toast.LENGTH_LONG).show();

                    }
                });
            }
        }
    }
}
