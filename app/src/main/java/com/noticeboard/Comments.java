package com.noticeboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.noticeboard.Utils.AppUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Comments extends AppCompatActivity {

    CommentAdapter commentAdapter;
    RecyclerView commentsRecycler;
    String pageID, postID, postTitle, postContent, pageAdminID, username, userimageURL;
    TextView title, content;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    EditText commentET;
    RelativeLayout noComment;
    Dialog dialog;
    Context context = this;
    ImageView sendbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Comments");

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        commentET = findViewById(R.id.comment);
        noComment = findViewById(R.id.noComments);
        sendbutton = findViewById(R.id.send);

        Intent intent = getIntent();
        pageID = intent.getStringExtra("pageID");
        postID = intent.getStringExtra("postID");
        postTitle = intent.getStringExtra("postTitle");
        postContent = intent.getStringExtra("postContent");
        pageAdminID = intent.getStringExtra("pageAdminID");

        title.setText(postTitle);
        content.setText(postContent);

        setUpCommentsRecyclerVIew();

        DocumentReference userdetails = FirebaseFirestore.getInstance().collection("Users").document(userID);
        userdetails.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                username = documentSnapshot.getString("fullname");
                userimageURL = documentSnapshot.getString("userimage");

            }
        });

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String commentDetails = commentET.getText().toString().trim();
                if (commentDetails.isEmpty()) {
                    commentET.setError("Comment cannot be empty");
                } else {

                    //DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
                    //final String time = dateTimeInstance.format(Calendar.getInstance().getTime());

                    SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                    Date date = new Date();
                    String Date_Time = formatter.format(date);

                    DocumentReference commentsRef = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(postID).collection("Comments").document();
                    String commentID = commentsRef.getId();
                    commentsRef.set(new CommentDetails(commentDetails, userID, commentID, Date_Time, username, userimageURL)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(Comments.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                    });

                }
            }
        });
        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                final CommentDetails comment = documentSnapshot.toObject(CommentDetails.class);

                String commentUID = comment.getUserID();
                final String commentID = comment.getCommentID();
                final String commentDetails = comment.getComment();

                if (userID.equals(commentUID)) {

                    final RelativeLayout edit, delete;
                    TextView editcomment, deletecomment;

                    dialog.setContentView(R.layout.addassociator_pop_up);
                    editcomment = dialog.findViewById(R.id.viewText);
                    editcomment.setText("Edit Comment");
                    deletecomment = dialog.findViewById(R.id.makeAssociatorText);
                    deletecomment.setText("Delete Comment");

                    edit = dialog.findViewById(R.id.viewprofile);
                    delete = dialog.findViewById(R.id.makeAssociator);

                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (AppUtils.isNetworkConnected(context)) {

                                dialog.dismiss();
                                commentET.setText(commentDetails);
                                commentET.setFocusableInTouchMode(true);
                                commentET.requestFocus();
                                AppUtils.openSoftKeyboard(context);

                                sendbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        final String newComment = commentET.getText().toString().trim();
                                        if (commentDetails.isEmpty()) {
                                            commentET.setError("Comment cannot be empty");
                                        } else if (commentDetails.equals(newComment)) {
                                            finish();
                                        } else {

                                            finish();
                                            startActivity(getIntent());
                                            DocumentReference commentsRef = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(postID).collection("Comments").document(commentID);
                                            commentsRef.update("comment", newComment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(Comments.this, "Comment updated", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {

                                Toast.makeText(Comments.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtils.isNetworkConnected(context)) {
                                DocumentReference deletecomment = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(postID).collection("Comments").document(commentID);
                                deletecomment.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(Comments.this, "Comment deleted", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();

                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Comments.this, "Check your internet connection", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();


                }
            }
        });
    }


    private void setUpCommentsRecyclerVIew() {

        final CollectionReference comments = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(postID).collection("Comments");
        Task<QuerySnapshot> queryforemptiness = comments.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().size() > 0) {

                        noComment.setVisibility(View.GONE);

                    } else {

                        noComment.setVisibility(View.VISIBLE);

                    }
                }

            }
        });

        Query query = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(postID).collection("Comments").orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<CommentDetails> options = new FirestoreRecyclerOptions.Builder<CommentDetails>()
                .setQuery(query, CommentDetails.class)
                .build();
        commentAdapter = new CommentAdapter(options);

        commentsRecycler = findViewById(R.id.commentsRecyclerView);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentsRecycler.setAdapter(commentAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        commentAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        commentAdapter.stopListening();
    }


    public void postComment(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
