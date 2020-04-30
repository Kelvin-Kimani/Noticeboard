package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostPage extends AppCompatActivity{

    private static final String TAG = "Post";
    EditText posttitle, post;
    Button postbutton;
    DatabaseReference reference;
    static final String KEY_TITLE= "Post Title";
    static final String KEY_CONTENT = "Post Content";
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Compose");

        posttitle = (EditText) findViewById(R.id.posttitle);
        post = (EditText) findViewById(R.id.post);
        postbutton = (Button) findViewById(R.id.postbutton);

        db = FirebaseFirestore.getInstance();
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        /*db.collection("Users").document(userID).collection("Pages").document(pagename).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String pagename = document.getString("Page Name");

                        Log.d("TAG", pagename );
                    }
                }
            }
        }); */

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()){

                    String title = posttitle.getText().toString().trim();
                    String content = post.getText().toString().trim();

                    Map<String, Object> post = new HashMap<>();

                    post.put(KEY_TITLE, title);
                    post.put(KEY_CONTENT, content);
                    post.put("Date", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

                    //remember to edit document name
                    db.collection("Users").document(userID).collection("Pages").document().collection("Posts").document("Date").set(post)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(PostPage.this, "Information Posted Successfully", Toast.LENGTH_LONG).show();
                                    //add intent flag
                                    startActivity(new Intent(PostPage.this, MainActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(PostPage.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                            }
                    });
                }
            }
        });
    }

    private boolean validateForm() {

        boolean valid = true;
        String title = posttitle.getText().toString().trim();
        String content = post.getText().toString().trim();

        if (title.isEmpty()) {
            posttitle.setError("Post Title Cannot Be Empty!");
            valid = false;
        }
        else if (title.length() < 2) {
            posttitle.setError("Post Title should be at least 2 characters");
            valid = false;
        }
        else
        {posttitle.setError(null);}

        if (content.isEmpty()){
            post.setError("Post Cannot Be Empty!");
            valid = false;
        }
        else{post.setError(null);}

        return valid;
    }
}
