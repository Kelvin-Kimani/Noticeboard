package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PageAssociators extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    String page_name, pageID, userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_associators);

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        pageID = intent.getStringExtra("pageID");
        userID = intent.getStringExtra("userID");

        getSupportActionBar().setTitle(page_name + " Associators");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        floatingActionButton = findViewById(R.id.addassociatorfab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent1 = new Intent(PageAssociators.this, AddAssociators.class);
               intent1.putExtra("pageID", pageID);
               intent1.putExtra("userID", userID);

               startActivity(intent1);

            }
        });


    }
}
