package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.noticeboard.ui.home.HomePostAdapter;

public class SavedPost extends AppCompatActivity {

    HomePostAdapter postAdapter;
    RecyclerView recyclerView;
    String changedValue = "Yes", userID, postID, postTitle, postContent, postPageName;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Saved Posts");

        relativeLayout = findViewById(R.id.savedpostRL);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setUpSavedPostRecyclerView();

        postAdapter.setOnItemClickListener(new HomePostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                postID = documentSnapshot.getId();
                postTitle = post.getTitle();
                postContent = post.getContent();
                postPageName = post.getPagename();

                Toast.makeText(SavedPost.this,
                        "Title: " + postTitle, Toast.LENGTH_LONG).show();


                Intent intent = new Intent(SavedPost.this, PostWithComments.class);
                //intent.putExtra("model", model);
                intent.putExtra("pagename", post.getPagename());
                intent.putExtra("postTitle", post.getTitle());
                intent.putExtra("postContent", post.getContent());
                intent.putExtra("postTime", post.getTime());
                intent.putExtra("postID", documentSnapshot.getId());

                startActivity(intent);

            }
        });


        postAdapter.setOnSaveItemClickListener(new HomePostAdapter.OnSaveItemClickListener() {
            @Override
            public void onSaveItemClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                String saveValue = post.getSaveValue();
                final String defaultValue = "No", changedValue = "Yes";
                String postID = post.getPostID();

                if (saveValue.equals(changedValue)) {

                    final CollectionReference pagePostsRef = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("All Posts");
                    Query query = pagePostsRef.whereEqualTo("postID", postID);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {

                                    pagePostsRef.document(document.getId()).update("saveValue", defaultValue);
                                    Toast.makeText(SavedPost.this, "Removed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
                } else {

                    final CollectionReference pagePostsRef = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("All Posts");
                    Query query = pagePostsRef.whereEqualTo("postID", postID);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {

                                    pagePostsRef.document(document.getId()).update("saveValue", changedValue);
                                    Toast.makeText(SavedPost.this, "Saved", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });

                }

            }
        });
    }

    private void setUpSavedPostRecyclerView() {

        final CollectionReference pagePostsRef = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("All Posts");
        Task<QuerySnapshot> queryforemptiness = pagePostsRef.whereEqualTo("saveValue", changedValue).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().size() > 0) {

                        relativeLayout.setVisibility(View.GONE);

                    } else {

                        relativeLayout.setVisibility(View.VISIBLE);

                    }
                }

            }
        });
        Query query = pagePostsRef.whereEqualTo("saveValue", changedValue).orderBy("pagename", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<PostDetails> options = new FirestoreRecyclerOptions.Builder<PostDetails>()
                .setQuery(query, PostDetails.class)
                .build();

        postAdapter = new HomePostAdapter(options);

        recyclerView = findViewById(R.id.savedPostRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
