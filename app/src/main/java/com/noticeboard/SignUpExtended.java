package com.noticeboard;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SignUpExtended extends AppCompatActivity {

    EditText fname, pno, regno;
    Spinner spinner;
    String userID, email;
    ProgressBar progressBar;
    FloatingActionButton submit;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupextended);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = new ProgressBar(this);
        fname = findViewById(R.id.fullnames);
        pno = findViewById(R.id.phonenumber);
        spinner = findViewById(R.id.spinner);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            email = user.getEmail();

            if (email.contains("@egerton.ac.ke")) {

                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("Staff");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
                spinner.setAdapter(arrayAdapter);

            } else {

                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("Student");
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
                spinner.setAdapter(arrayAdapter);

            }
        }

        submit = findViewById(R.id.submitFAB);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {

                    progressBar.setVisibility(View.VISIBLE);

                    final String fullnames = fname.getText().toString().trim();

                    final String phonenumber = pno.getText().toString().trim();

                    final String level = spinner.getSelectedItem().toString();

                    UserDetails user = new UserDetails(fullnames, phonenumber, level, email, userID);
                    DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userID);
                    userRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            progressBar.setVisibility(View.INVISIBLE);

                            if (task.isSuccessful()) {

                                Toast.makeText(SignUpExtended.this, "Profile Details Updated", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignUpExtended.this, AddUserProfile.class));
                                finish();

                            } else {

                                Toast.makeText(SignUpExtended.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }
            }
        });
    }


    private boolean validateForm() {

        boolean valid = true;

        final String fullnames = fname.getText().toString().trim();

        final String phonenumber = pno.getText().toString().trim();


        if (fullnames.isEmpty()) {
            fname.setError("Please Enter your name");
            valid = false;
        } else if (fullnames.length() < 2) {
            fname.setError("Name should be at least 2 characters");
            valid = false;
        } else {
            fname.setError(null);
        }

        if (phonenumber.isEmpty()) {
            pno.setError("Please Enter Your Phone Number");
            valid = false;
        } else if (phonenumber.length() < 9) {
            pno.setError("Phonenumber too short!");
            valid = false;
        } else if (phonenumber.length() > 9) {
            pno.setError("Phonenumber Long!");
            valid = false;
        } else {
            pno.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {

        TextView title, text;
        Button yes, no;
        dialog.setContentView(R.layout.custom_pop_up);

        title = dialog.findViewById(R.id.dialogtitle);
        title.setText("Leaving now?");
        text = dialog.findViewById(R.id.dialogtext);
        text.setText(getString(R.string.leavesignup));

        yes = dialog.findViewById(R.id.positivebutton);
        no = dialog.findViewById(R.id.negativebutton);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpExtended.this, "Hope to see you soon", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpExtended.this, SignUp.class));
                            finish();
                        } else {

                            Toast.makeText(SignUpExtended.this, "Something wrong happened", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}







