package com.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class EditPage extends AppCompatActivity {

    EditText pagename, bio;
    Spinner spinner;
    String userID, pageID, page_name, page_info;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        page_name = intent.getStringExtra("pagename");
        page_info = intent.getStringExtra("pageinfo");

        pagename = findViewById(R.id.pagename);
        bio = findViewById(R.id.bioinfo);

        pagename.setText(page_name);
        bio.setText(page_info);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.accept, menu);
        return true;
    }

    public void updateProfile(MenuItem item) {

        TextView title, text;
        Button yes, no;
        dialog.setContentView(R.layout.custom_pop_up);

        title = dialog.findViewById(R.id.dialogtitle);
        title.setText("Update Page");
        text = dialog.findViewById(R.id.dialogtext);
        text.setText("Do you wish to continue?");

        yes = dialog.findViewById(R.id.positivebutton);
        no = dialog.findViewById(R.id.negativebutton);


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {

                    final String pagenameupdated = pagename.getText().toString().trim();
                    final String pageinfoUpdated = bio.getText().toString().trim();

                    DocumentReference pageReference = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").document(pageID);
                    pageReference.update("pagename", pagenameupdated);
                    pageReference.update("pageinfo", pageinfoUpdated).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(EditPage.this, "Page Information Updated Successfully", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        }
                    });

                    final CollectionReference globalPageRef = FirebaseFirestore.getInstance().collection("Global Pages");
                    Query query1 = globalPageRef.whereEqualTo("pageID", pageID);
                    query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    globalPageRef.document(document.getId()).update("pagename", pagenameupdated);
                                    globalPageRef.document(document.getId()).update("pageinfo", pageinfoUpdated);

                                }
                            }
                        }
                    });
                }
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

    private boolean validateForm() {

        boolean valid = true;

        page_name = pagename.getText().toString().trim();


        if (page_name.isEmpty()) {
            pagename.setError("Please Enter A Page Name");
            valid = false;
        } else if (page_name.length() < 3) {
            pagename.setError("Page Name should be at least 3 characters");
            valid = false;
        } else {
            pagename.setError(null);
        }

        return valid;
    }
}
