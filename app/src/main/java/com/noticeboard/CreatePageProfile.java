package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class CreatePageProfile extends AppCompatActivity {

    Button cancel, add;
    FloatingActionButton fab;
    ImageView pageprofileimg;
    ProgressBar progressBar;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Uri imageUri;
    StorageTask storageTask;
    Intent CropIntent;
    private static final int gallerypick =1;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpageprofile);

        pageprofileimg =(ImageView) findViewById(R.id.pageprofileimg);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        add = (Button) findViewById(R.id.addbutton);
        cancel = (Button) findViewById(R.id.skipbutton);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);


        storageReference = FirebaseStorage.getInstance().getReference().child("Pages Profiles");
       // databaseReference = FirebaseDatabase.getInstance().getReference("PageProfileAdmin");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallerypick);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (storageTask != null && storageTask.isInProgress()){
                    Toast.makeText(CreatePageProfile.this, "Upload in progress", Toast.LENGTH_LONG).show();
                }

                else{
                    //uploadImage();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreatePageProfile.this, "Okay, No problem", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CreatePageProfile.this, MainActivity.class));
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallerypick && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK){

                    Uri resultUri = result.getUri();

                    StorageReference filereference = storageReference.child(userID +".jpg");

                    filereference.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(CreatePageProfile.this, "Pic updated", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }

            }
        }

    }


   /* private void uploadImage() {
        if (imageUri!=null){

            StorageReference filereference = storageReference.child("");

            storageTask = filereference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            },5000);

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl = urlTask.getResult();
                            String imageurl = String.valueOf(downloadUrl);



                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            ImageUpload pageimage = new ImageUpload(imageurl);
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(userID).child(uploadId).setValue(pageimage);

                            Toast.makeText(CreatePageProfile.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(CreatePageProfile.this, Pages.class);
                            //Make sure to not go back to the previous Activity
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        }
                    });
        }
        else {
            Toast.makeText(CreatePageProfile.this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }*/

}
