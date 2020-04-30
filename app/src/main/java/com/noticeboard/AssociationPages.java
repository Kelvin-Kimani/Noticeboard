package com.noticeboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class AssociationPages extends AppCompatActivity {

    RecyclerView recyclerView;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_association_pages);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Association Pages");

        recyclerView = (RecyclerView) findViewById(R.id.associationpagesrecycler);
        view = (View) findViewById(R.id.view);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        // back functionality
        Intent myIntent = new Intent(getApplicationContext(), UserProfile.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
