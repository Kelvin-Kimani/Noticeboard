package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpExtended extends AppCompatActivity {

    private static final String TAG = "SignUpExtended";
    private static final String KEY_FULLNAME = "Full Names";
    private static final String KEY_PHONE = "Phone Number";
    private static final String KEY_REGNO = "Registration Number";
    private static final String KEY_LEVEL = "Level";
    EditText fname, pno, regno;
    Spinner spinner;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupextended);


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

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    public void saveUserDetails(View view) {

        String fullnames = fname.getText().toString();

        String phonenumber = pno.getText().toString();

        String registration = regno.getText().toString();

        String level = spinner.getSelectedItem().toString();


        Map<String, Object> user = new HashMap<>();

        user.put(KEY_FULLNAME, fullnames);
        user.put(KEY_PHONE, phonenumber);
        user.put(KEY_REGNO, registration);
        user.put(KEY_LEVEL, level);

        firebaseFirestore.collection("Users").document(fullnames).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(SignUpExtended.this, "User details updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpExtended.this, AddUserProfile.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(SignUpExtended.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}






