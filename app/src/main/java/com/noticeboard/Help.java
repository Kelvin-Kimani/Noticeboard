package com.noticeboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Help extends AppCompatActivity {

    TextView createpage, followpage;
    String createpagelink, followpagelink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        createpage = findViewById(R.id.createpage);
        followpage = findViewById(R.id.followpage);

        createpagelink = "Create a page here and start to post to your followers.";
        SpannableString ss = new SpannableString(createpagelink);
        ClickableSpan createpagespan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                startActivity(new Intent(Help.this, CreatePage.class));

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(createpagespan, 14, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        createpage.setText(ss);
        createpage.setMovementMethod(LinkMovementMethod.getInstance());

        followpagelink = "Follow pages from here and get instant news.";
        SpannableString string = new SpannableString(followpagelink);
        ClickableSpan followpagespan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                int page = 3;
                Intent intent = new Intent(Help.this, Pages.class);
                intent.putExtra("IntendedPage", page);
                startActivity(intent);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };

        string.setSpan(followpagespan, 18, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        followpage.setText(string);
        followpage.setMovementMethod(LinkMovementMethod.getInstance());

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
