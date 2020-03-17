package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatePage extends AppCompatActivity {

    private static final String TAG = "CreatePage";
    EditText page_name, bio;
    Spinner spinner;
    FirebaseFirestore firebaseFirestore;
    static final String KEY_PAGENAME = "Page Name";
    static final String KEY_BIO = "Page Information";
    static final String KEY_PRIVACY = "Privacy";

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpage);

        page_name = (EditText) findViewById(R.id.pagename);
        bio = (EditText) findViewById(R.id.bioinfo);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("--select--");
        arrayList.add("Public");
        arrayList.add("Private");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void savePage(View v){

        String pagename = page_name.getText().toString();

        String pageinfo = bio.getText().toString();

        String privacy = spinner.getSelectedItem().toString();

        Map<String, Object> page = new HashMap<>();

        page.put(KEY_PAGENAME, pagename);
        page.put(KEY_BIO, pageinfo);
        page.put(KEY_PRIVACY, privacy);

        firebaseFirestore.collection("Pages").document(pagename).set(page)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(CreatePage.this, "Page Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreatePage.this, CreatePageProfile.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(CreatePage.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}