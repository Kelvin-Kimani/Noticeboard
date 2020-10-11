package com.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.noticeboard.Utils.AppUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostPage extends AppCompatActivity {

    EditText posttitle, post;
    Button postbutton;
    String pageID, pageAdminID;
    String page_name;
    String defaultValue = "No";
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Compose");

        posttitle = findViewById(R.id.posttitle);
        post = findViewById(R.id.post);

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        page_name = intent.getStringExtra("pagename");
        pageAdminID = intent.getStringExtra("pageAdminID");

        posttitle.setFocusableInTouchMode(true);
        posttitle.requestFocus();
        AppUtils.openSoftKeyboard(context);

    }

    private boolean validateForm() {

        boolean valid = true;
        String title = posttitle.getText().toString().trim();
        String content = post.getText().toString().trim();

        if (title.isEmpty()) {
            posttitle.setError("Post Title Cannot Be Empty!");
            valid = false;
        } else if (title.length() < 2) {
            posttitle.setError("Post Title should be at least 2 characters");
            valid = false;
        } else {
            posttitle.setError(null);
        }

        if (content.isEmpty()) {
            post.setError("Post Cannot Be Empty!");
            valid = false;
        } else {
            post.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    public void post(MenuItem item) {

        if (AppUtils.isNetworkConnected(context)){

            if (validateForm()) {

                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String title = posttitle.getText().toString().trim();
                final String content = post.getText().toString().trim();


                //SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
                DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
                //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                final String time = dateTimeInstance.format(Calendar.getInstance().getTime());

                DocumentReference postreference = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document();
                final String postID = postreference.getId();
                postreference.set(new PostDetails(title, content, time, postID));


                DocumentReference allpostreference = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("All Posts").document(postID);
                allpostreference.set(new PostDetails(page_name, title, content, time, pageID, postID, defaultValue, userID, pageAdminID)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(PostPage.this, "Information Posted Successfully!", Toast.LENGTH_LONG).show();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(PostPage.this, "An Error Occurred. Please try again later ", Toast.LENGTH_LONG).show();

                    }
                });

                CollectionReference follower = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Followers");
                follower.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){

                                UserDetails user = document.toObject(UserDetails.class);

                                String followerID = user.getUserID();

                                DocumentReference allpostreference = FirebaseFirestore.getInstance().collection("Users").document(followerID).collection("All Posts").document(postID);
                                allpostreference.set(new PostDetails(page_name, title, content, time, pageID, postID, defaultValue, userID, pageAdminID)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(PostPage.this, "Posted Successfully!", Toast.LENGTH_LONG).show();
                                        finish();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(PostPage.this, "An Error Occurred. Please try again later ", Toast.LENGTH_LONG).show();

                                    }
                                });


                            }
                        }
                    }
                });

            }
        }

        else {

            Toast.makeText(PostPage.this, "Check your internet connection", Toast.LENGTH_LONG).show();

        }
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
