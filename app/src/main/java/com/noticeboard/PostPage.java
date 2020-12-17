package com.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

        if (AppUtils.isNetworkConnected(context)) {

            if (validateForm()) {

                finish();

                final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String title = posttitle.getText().toString().trim();
                final String content = post.getText().toString().trim();
                final String status = "UNREAD";


                DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
                final String time = dateTimeInstance.format(Calendar.getInstance().getTime());

                DocumentReference postreference = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document();
                final String postID = postreference.getId();

                PostDetails post = new PostDetails();
                post.setTitle(title);
                post.setContent(content);
                post.setPostID(postID);
                post.setTime(time);
                post.setSearch_post(title.toLowerCase());

                postreference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(PostPage.this, "Posted Successfully!", Toast.LENGTH_LONG).show();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(PostPage.this, "An Error Occurred. Please try again later ", Toast.LENGTH_LONG).show();

                            }
                        });

                //all posts on admin
                DocumentReference allpostreference = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("All Posts").document(postID);

                PostDetails adminPost = new PostDetails();
                adminPost.setPagename(page_name);
                adminPost.setTitle(title);
                adminPost.setContent(content);
                adminPost.setPageID(pageID);
                adminPost.setPostID(postID);
                adminPost.setSaveValue(defaultValue);
                adminPost.setPostersID(userID);
                adminPost.setPageAdminID(pageAdminID);
                adminPost.setStatus(status);
                adminPost.setTime(time);
                adminPost.setSearch_post(title.toLowerCase());

                allpostreference.set(adminPost);

                //all posts on followers
                CollectionReference follower = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Followers");
                follower.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserDetails user = document.toObject(UserDetails.class);

                                String followerID = user.getUserID();

                                DocumentReference allpostreference = FirebaseFirestore.getInstance().collection("Users").document(followerID).collection("All Posts").document(postID);

                                PostDetails followerPost = new PostDetails();
                                followerPost.setPagename(page_name);
                                followerPost.setTitle(title);
                                followerPost.setContent(content);
                                followerPost.setPageID(pageID);
                                followerPost.setPostID(postID);
                                followerPost.setSaveValue(defaultValue);
                                followerPost.setPostersID(userID);
                                followerPost.setPageAdminID(pageAdminID);
                                followerPost.setStatus(status);
                                followerPost.setTime(time);
                                followerPost.setSearch_post(title.toLowerCase());

                                allpostreference.set(followerPost);

                            }
                        }
                    }
                });

            }
        } else {

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
