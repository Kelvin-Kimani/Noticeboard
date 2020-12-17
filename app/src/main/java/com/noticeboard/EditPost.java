package com.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.noticeboard.Utils.AppUtils;

public class EditPost extends AppCompatActivity {

    EditText postTitle, postContent;
    String title, post, pageID, postID, pageAdminID;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Compose");

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        post = intent.getStringExtra("post");
        pageID = intent.getStringExtra("pageID");
        postID = intent.getStringExtra("postID");
        pageAdminID = intent.getStringExtra("pageAdminID");

        postTitle = findViewById(R.id.posttitle);
        postContent = findViewById(R.id.post);

        postTitle.setText(title);
        postContent.setText(post);

        postTitle.setFocusableInTouchMode(true);
        postTitle.requestFocus();
        AppUtils.openSoftKeyboard(context);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    public void post(MenuItem item) {

        final String updatedTitle = postTitle.getText().toString().trim();
        final String updatedPost = postContent.getText().toString().trim();

        if (title.equals(updatedTitle) && post.equals(updatedPost)) {

            finish();

        } else {
            if (AppUtils.isNetworkConnected(context)) {

                if (validateForm()) {
                    //update post on page
                    DocumentReference post = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(postID);
                    post.update("title", updatedTitle);
                    post.update("content", updatedPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            finish();
                            Toast.makeText(EditPost.this, "Updated", Toast.LENGTH_SHORT).show();

                            //update on page admin
                            DocumentReference post = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("All Posts").document(postID);
                            post.update("title", updatedTitle);
                            post.update("title", updatedTitle.toLowerCase());
                            post.update("content", updatedPost);
                            post.update("status", "UNREAD");

                            //update on followers
                            CollectionReference follower = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Followers");
                            follower.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            UserDetails user = document.toObject(UserDetails.class);

                                            String followerID = user.getUserID();

                                            DocumentReference post = FirebaseFirestore.getInstance().collection("Users").document(followerID).collection("All Posts").document(postID);
                                            post.update("title", updatedTitle);
                                            post.update("title", updatedTitle.toLowerCase());
                                            post.update("content", updatedPost);
                                            post.update("status", "UNREAD");

                                        }
                                    }
                                }
                            });
                        }
                    });
                }

            } else {

                Toast.makeText(EditPost.this, "Check your internet connection", Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateForm() {

        boolean valid = true;
        String updatedTitle = postTitle.getText().toString().trim();
        String updatedPost = postContent.getText().toString().trim();

        if (updatedTitle.isEmpty()) {
            postTitle.setError("Post Title Cannot Be Empty!");
            valid = false;
        } else {
            postTitle.setError(null);
        }

        if (updatedPost.isEmpty()) {
            postContent.setError("Post Cannot Be Empty!");
            valid = false;
        } else {
            postContent.setError(null);
        }

        return valid;
    }
}