package com.noticeboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class signupextended extends AppCompatActivity {

    EditText fullnames,phonenumber,regno;
    Spinner level;
    Button submit;


    //list to store all users from database

    List<UserDetails> users;

    DatabaseReference user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupextended);

        user = FirebaseDatabase.getInstance().getReference();

        fullnames = (EditText) findViewById(R.id.fullnames);
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        regno = (EditText) findViewById(R.id.regno);
        level = (Spinner) findViewById(R.id.spinner);
        submit = (Button) findViewById(R.id.submitbutton);

        users = new ArrayList<>();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }

    private void addUser(){

            String fullname = fullnames.getText().toString();
            String phone = phonenumber.getText().toString();
            String registration = regno.getText().toString();
            String spinner = level.getSelectedItem().toString();

            if (!TextUtils.isEmpty(fullname)){

                String id =user.push().getKey();

                //Creating a user object

                UserDetails userDetails = new UserDetails(id,fullname, phone,registration,spinner);

                //Saving the user
                user.child(id).setValue(userDetails);

                //Displaying a success toast
                Toast.makeText(this,"User added", Toast.LENGTH_LONG).show();

            }

            else {
                //If value is not given display the toast
                Toast.makeText(this,"Please enter your fullnames",Toast.LENGTH_LONG).show();
            }
    }

}
