package com.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.noticeboard.Utils.AppUtils;

public class EditPost extends AppCompatActivity {

    EditText postTitle, postContent;
    String title, post;
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