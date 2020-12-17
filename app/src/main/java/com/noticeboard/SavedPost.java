package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    String changedValue = "Yes", userID, postID, postTitle, postContent, postPageName, posters_id, pageID, pageAdminID;
    RelativeLayout relativeLayout;
    Bundle bundleRecyclerViewState;
    private Parcelable recyclerstate = null;
    private final String KEY_RECYCLER_STATE = "recycler_state";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Saved Posts");

        relativeLayout = findViewById(R.id.savedpostRL);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setUpSavedPostRecyclerView();
        recyclerOnClick();


    }

    private void recyclerOnClick() {

        postAdapter.setOnItemClickListener(new HomePostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                postID = post.getPostID();
                postTitle = post.getTitle();
                postContent = post.getContent();
                postPageName = post.getPagename();
                posters_id = post.getPostersID();
                pageAdminID = post.getPageAdminID();
                pageID = post.getPageID();

                Toast.makeText(SavedPost.this,
                        "Title: " + postTitle, Toast.LENGTH_LONG).show();


                Intent intent = new Intent(SavedPost.this, PostWithComments.class);
                //intent.putExtra("model", model);
                intent.putExtra("pagename", postPageName);
                intent.putExtra("postTitle", postTitle);
                intent.putExtra("postContent", postContent);
                intent.putExtra("postTime", post.getTime());
                intent.putExtra("postID", postID);
                intent.putExtra("postersID", posters_id);
                intent.putExtra("pageID", pageID);
                intent.putExtra("pageAdminID", pageAdminID);


                startActivity(intent);

            }
        });


        postAdapter.setOnSaveItemClickListener(new HomePostAdapter.OnSaveItemClickListener() {
            @Override
            public void onSaveItemClick(DocumentSnapshot documentSnapshot, final int position) {

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
                                    postAdapter.notifyDataSetChanged();

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
                                    postAdapter.notifyItemChanged(position);

                                }
                            }
                        }
                    });

                }

            }
        });

        postAdapter.setOnCommentsClickListener(new HomePostAdapter.OnCommentsClickListener() {
            @Override
            public void onCommentsClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                postID = post.getPostID();
                pageID = post.getPageID();
                postTitle = post.getTitle();
                postContent = post.getContent();
                pageAdminID = post.getPageAdminID();

                Intent intent = new Intent(SavedPost.this, Comments.class);

                intent.putExtra("pageID", pageID);
                intent.putExtra("postTitle", postTitle);
                intent.putExtra("postContent", postContent);
                intent.putExtra("postID", postID);
                intent.putExtra("pageAdminID", pageAdminID);

                startActivity(intent);

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
        Query query = pagePostsRef.whereEqualTo("saveValue", changedValue).orderBy("timestamp", Query.Direction.DESCENDING);

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
    public void onPause() {
        super.onPause();
        bundleRecyclerViewState = new Bundle();

        recyclerstate = recyclerView.getLayoutManager().onSaveInstanceState();

        bundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, recyclerstate);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (bundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    recyclerstate = bundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerstate);

                }
            }, 50);
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchData(query);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

                return false;
            }
        });
        return true;
    }

    private void searchData(String query) {

        FirestoreRecyclerOptions<PostDetails> options = new FirestoreRecyclerOptions.Builder<PostDetails>()
                .setQuery(FirebaseFirestore.getInstance().collection("Users").document(userID).collection("All Posts").whereEqualTo("saveValue", changedValue).orderBy("title", Query.Direction.ASCENDING).startAt(query.toUpperCase()).endAt(query.toLowerCase() + "\uf8ff"), PostDetails.class)
                .build();

        postAdapter = new HomePostAdapter(options);
        postAdapter.startListening();
        postAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(postAdapter);
        recyclerOnClick();
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
