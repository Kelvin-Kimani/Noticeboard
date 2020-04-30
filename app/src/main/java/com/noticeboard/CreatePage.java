package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class CreatePage extends AppCompatActivity {

    EditText page_name, bio;
    Spinner spinner;
    String userID;
    Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Page");

        page_name = (EditText) findViewById(R.id.pagename);
        bio = (EditText) findViewById(R.id.bioinfo);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("--select--");
        arrayList.add("Public");
        arrayList.add("Private");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);
        create = (Button) findViewById(R.id.createpagebutton);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (validateForm()){

                    final String pagename = page_name.getText().toString().trim();

                    final String pageinfo = bio.getText().toString().trim();

                    final String privacy = spinner.getSelectedItem().toString();

                    PageDetails page = new PageDetails(pagename, pageinfo, privacy);
                    DocumentReference pageref = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document();

                    pageref.set(page).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(CreatePage.this, "Page Created", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(CreatePage.this, CreatePageProfile.class));
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
                }
            }
        });
    }

    private boolean validateForm() {

        boolean valid = true;

        final String pagename = page_name.getText().toString().trim();

       // final String pageinfo = bio.getText().toString().trim();

        //final String privacy = spinner.getSelectedItem().toString();

        if (pagename.isEmpty()) {
            page_name.setError("Please Enter A Page Name");
            valid = false;
        }
        else if ( pagename.length() < 3) {
            page_name.setError("Page Name should be at least 3 characters");
            valid = false;
        }
        else
        {page_name.setError(null);}

        return valid;
    }

}

