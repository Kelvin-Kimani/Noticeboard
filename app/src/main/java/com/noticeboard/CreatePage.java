package com.noticeboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.noticeboard.Utils.AppUtils;

import java.util.ArrayList;

public class CreatePage extends AppCompatActivity {

    EditText page_name, bio;
    Spinner spinner;
    String userID;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        page_name = findViewById(R.id.pagename);
        bio = findViewById(R.id.bioinfo);
        spinner = findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Public");
        arrayList.add("Private");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private boolean validateForm() {

        boolean valid = true;

        final String pagename = page_name.getText().toString().trim();


        if (pagename.isEmpty()) {
            page_name.setError("Please Enter A Page Name");
            valid = false;
        } else if (pagename.length() < 3) {
            page_name.setError("Page Name should be at least 3 characters");
            valid = false;
        } else {
            page_name.setError(null);
        }

        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.accept, menu);
        return true;
    }

    public void updateProfile(MenuItem item) {

        if (AppUtils.isNetworkConnected(context)) {

            if (validateForm()) {

                final String pagename = page_name.getText().toString().trim();

                final String pageinfo = bio.getText().toString().trim();

                final String privacy = spinner.getSelectedItem().toString();

                DocumentReference pageref = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document();
                String pageID = pageref.getId();

                pageref.set(new PageDetails(pagename, pageinfo, privacy, pageID, userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(CreatePage.this, "Page Created", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(CreatePage.this, Pages.class));
                        finish();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(CreatePage.this, "An Error Occurred. Please try again later ", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(CreatePage.this, CreatePage.class));
                                finish();
                            }
                        });

                PageDetails globalPage = new PageDetails(pagename, pageinfo, privacy, pageID, userID);
                DocumentReference globalPageRef = FirebaseFirestore.getInstance().collection("Global Pages").document();

                globalPageRef.set(globalPage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(CreatePage.this, "It's global now!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }

        } else {

            Toast.makeText(CreatePage.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }
    }
}

