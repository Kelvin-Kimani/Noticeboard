package com.noticeboard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amulyakhare.textdrawable.TextDrawable;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.noticeboard.Utils.AppUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostWithComments extends AppCompatActivity {

    RelativeLayout commentTextbox, noComment, addComment;
    CircleImageView circleImageView;
    TextView pagename, postTitle, postContent, time, postedby, firstComment, yesComment, addCommentTV;
    String page_name, post_title, post_content, post_time, post_id, posters_id, username, userimageURL, pageAdminID, pageID, userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    EditText comment;
    Context context = this;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_with_comments);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        circleImageView = findViewById(R.id.pageprofileimg);
        pagename = findViewById(R.id.pagename);
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.post);
        time = findViewById(R.id.time);
        postedby = findViewById(R.id.postersname);
        commentTextbox = findViewById(R.id.commentTextbox);
        firstComment = findViewById(R.id.firstcomment);
        comment = findViewById(R.id.TypeComment);
        noComment = findViewById(R.id.firstcommentRL);
        yesComment = findViewById(R.id.loadComments);
        addComment = findViewById(R.id.addComment);
        addCommentTV = findViewById(R.id.addCommentText);

        Intent intent = getIntent();

        page_name = intent.getStringExtra("pagename");
        post_time = intent.getStringExtra("postTime");
        post_id = intent.getStringExtra("postID");
        posters_id = intent.getStringExtra("postersID");
        pageAdminID = intent.getStringExtra("pageAdminID");
        pageID = intent.getStringExtra("pageID");


        time.setText(post_time);

        DocumentReference getPageName = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID);
        getPageName.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                page_name = documentSnapshot.getString("pagename");
                pagename.setText(page_name);

                Character firstLetter = page_name.charAt(0);
                TextDrawable drawable;
                drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.RED)
                        .fontSize(30)
                        .toUpperCase()
                        .bold()
                        .width(80)  // width in px
                        .height(80) // height in px
                        .endConfig()
                        .buildRect(String.valueOf(firstLetter), Color.BLACK);

                circleImageView.setImageDrawable(drawable);

            }
        });

        DocumentReference getPostDetails = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(post_id);
        getPostDetails.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                post_content = documentSnapshot.getString("content");
                post_title = documentSnapshot.getString("title");

                postTitle.setText(post_title);
                postContent.setText(post_content);

            }
        });

        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(posters_id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                postedby.setText(documentSnapshot.getString("fullname"));

            }
        });

        DocumentReference userdetails = FirebaseFirestore.getInstance().collection("Users").document(userID);
        userdetails.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                username = documentSnapshot.getString("fullname");
                userimageURL = documentSnapshot.getString("userimage");

            }
        });

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(post_id).collection("Comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().size() > 0) {

                        noComment.setVisibility(View.GONE);
                        yesComment.setVisibility(View.VISIBLE);
                        addComment.setVisibility(View.VISIBLE);


                    } else {

                        noComment.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(PostWithComments.this, Comments.class);
                intent1.putExtra("pageID", pageID);
                intent1.putExtra("postTitle", post_title);
                intent1.putExtra("postContent", post_content);
                intent1.putExtra("postID", post_id);
                intent1.putExtra("pageAdminID", pageAdminID);

                startActivity(intent1);
            }
        });

        yesComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(PostWithComments.this, Comments.class);
                intent1.putExtra("pageID", pageID);
                intent1.putExtra("postTitle", post_title);
                intent1.putExtra("postContent", post_content);
                intent1.putExtra("postID", post_id);
                intent1.putExtra("pageAdminID", pageAdminID);

                startActivity(intent1);
            }
        });
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    public void openKeyboardForComments(View view) {

        commentTextbox.setVisibility(View.VISIBLE);
        firstComment.setVisibility(View.GONE);
        comment.setFocusableInTouchMode(true);
        comment.requestFocus();
        AppUtils.openSoftKeyboard(context);

    }

    public void postComment(View view) {

        final String commentDetails = comment.getText().toString().trim();
        if (commentDetails.isEmpty()) {
            comment.setError("Comment cannot be empty");
        } else {

            DateFormat dateTimeInstance = SimpleDateFormat.getDateTimeInstance();
            //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            final String time = dateTimeInstance.format(Calendar.getInstance().getTime());

            DocumentReference commentsRef = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(post_id).collection("Comments").document();
            String commentID = commentsRef.getId();
            commentsRef.set(new CommentDetails(commentDetails, userID, commentID, time, username, userimageURL)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    Toast.makeText(PostWithComments.this, "Comment Posted", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        final MenuInflater inflater = getMenuInflater();

        DocumentReference checkPostedBy = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("All Posts").document(post_id);
        checkPostedBy.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                final String postedby = documentSnapshot.getString("postersID");


                if (userID.equals(postedby)) {

                    inflater.inflate(R.menu.postmenuoptions, menu);

                }

            }
        });

        return true;
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

    public void editPost(MenuItem item) {
        //pass title and post content
        Intent intent = new Intent(PostWithComments.this, EditPost.class);
        intent.putExtra("title", post_title);
        intent.putExtra("post", post_content);
        intent.putExtra("pageID", pageID);
        intent.putExtra("postID", post_id);
        intent.putExtra("pageAdminID", pageAdminID);


        startActivity(intent);
    }

    public void deletePost(MenuItem item) {
        //pop up
        TextView title, text;
        Button yes, no;
        dialog.setContentView(R.layout.custom_pop_up);

        title = dialog.findViewById(R.id.dialogtitle);
        title.setText("Delete Post?");
        text = dialog.findViewById(R.id.dialogtext);
        text.setVisibility(View.GONE);

        yes = dialog.findViewById(R.id.positivebutton);
        no = dialog.findViewById(R.id.negativebutton);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppUtils.isNetworkConnected(context)) {

                    //delete post on page
                    DocumentReference post = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Posts").document(post_id);
                    post.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(PostWithComments.this, "Post deleted", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            startActivity(new Intent(PostWithComments.this, MainActivity.class));

                            //delete on page admin
                            DocumentReference post = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("All Posts").document(post_id);
                            post.delete();

                            //delete on followers
                            CollectionReference follower = FirebaseFirestore.getInstance().collection("Users").document(pageAdminID).collection("Pages").document(pageID).collection("Followers");
                            follower.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            UserDetails user = document.toObject(UserDetails.class);

                                            String followerID = user.getUserID();

                                            DocumentReference post = FirebaseFirestore.getInstance().collection("Users").document(followerID).collection("All Posts").document(post_id);
                                            post.delete();

                                        }
                                    }
                                }
                            });
                        }
                    });
                } else {

                    Toast.makeText(PostWithComments.this, "Check your internet connection", Toast.LENGTH_LONG).show();

                }

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
