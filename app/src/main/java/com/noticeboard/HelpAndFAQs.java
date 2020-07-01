package com.noticeboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HelpAndFAQs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_faqs);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Help & FAQs");



    }
}
