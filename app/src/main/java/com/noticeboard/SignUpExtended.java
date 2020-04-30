package com.noticeboard;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class SignUpExtended extends AppCompatActivity {

    EditText fname, pno, regno;
    Spinner spinner;
    String userID;
    ProgressBar progressBar;
    Button submit;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupextended);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar = new ProgressBar(this);
        fname = (EditText) findViewById(R.id.fullnames);
        pno = (EditText) findViewById(R.id.phonenumber);
        regno = (EditText) findViewById(R.id.regno);
        spinner = (Spinner) findViewById(R.id.spinner);

        //adding elements to a spinner
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("--select--");
        arrayList.add("Staff");
        arrayList.add("Student");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);
        submit = (Button) findViewById(R.id.submitbutton);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()){

                    final String fullnames = fname.getText().toString();

                    final String phonenumber = pno.getText().toString();

                    final String level = spinner.getSelectedItem().toString();

                    final String registration = regno.getText().toString();



                    UserDetails user = new UserDetails(fullnames, phonenumber, level, registration);
                    DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userID);
                    userRef.set(user);
                    Toast.makeText(SignUpExtended.this, "Profile Details Updated", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpExtended.this, AddUserProfile.class));
                    finish();

                }
            }
        });
    }


    private boolean validateForm() {

        boolean valid = true;

        final String fullnames = fname.getText().toString().trim();

        final String phonenumber = pno.getText().toString().trim();

        final String level = spinner.getSelectedItem().toString();

        final String registration = regno.getText().toString().trim();

        if (fullnames.isEmpty()) {
            fname.setError("Please Enter your name");
            valid = false;
        }
        else if ( fullnames.length() < 2) {
            fname.setError("Name should be at least 2 characters");
            valid = false;
        }
        else
         {fname.setError(null);}

        if (phonenumber.isEmpty()){
            pno.setError("Please Enter Your Phone Number");
            valid = false;
        }
        else if (phonenumber.length()<9|| phonenumber.length()>9) {
            pno.setError("Enter A Valid Phonenumber");
            valid = false;
        }
        else{pno.setError(null);}

        return valid;
    }


}






