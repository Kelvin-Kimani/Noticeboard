package com.noticeboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class PostPage extends AppCompatActivity{
    EditText posttitle, post;
    Button postbutton;
    DatabaseReference reference;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_page);

        posttitle = (EditText) findViewById(R.id.posttitle);
        post = (EditText) findViewById(R.id.post);
        postbutton = (Button) findViewById(R.id.postbutton);
        db = FirebaseFirestore.getInstance();

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = posttitle.getText().toString();
                String content = post.getText().toString();

                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getApplicationContext(), "Post Something please", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {

                    CollectionReference dbPosts = db.collection("Posts");

                    PostDetails postDetails = new PostDetails(title, content);

                    dbPosts.add(postDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(PostPage.this, "Posted Successfully", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostPage.this, "Couldn't be posted", Toast.LENGTH_LONG).show();
                        }
                    });
                }



            }
        });

    }
}
