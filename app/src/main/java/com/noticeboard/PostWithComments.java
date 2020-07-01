package com.noticeboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amulyakhare.textdrawable.TextDrawable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostWithComments extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView pagename, postTitle, postContent, time;
    String page_name, post_title, post_content, post_time, post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_with_comments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        circleImageView = findViewById(R.id.pageprofileimg);
        pagename = findViewById(R.id.pagename);
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.post);
        time = findViewById(R.id.time);

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        post_title = intent.getStringExtra("postTitle");
        post_content = intent.getStringExtra("postContent");
        post_time = intent.getStringExtra("postTime");
        post_id = intent.getStringExtra("postID");

        pagename.setText(page_name);
        postTitle.setText(post_title);
        postContent.setText(post_content);
        time.setText(post_time);

        Character firstLetter = page_name.charAt(0);
        TextDrawable drawable;
        drawable = TextDrawable.builder()
                .beginConfig()
                .textColor(Color.RED)
                .fontSize(30)
                .toUpperCase()
                .bold()
                .width(80)  // width in px
                .height(80) // height in px
                .endConfig()
                .buildRect(String.valueOf(firstLetter), Color.BLACK);

        circleImageView.setImageDrawable(drawable);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
