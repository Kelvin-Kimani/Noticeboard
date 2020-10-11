package com.noticeboard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.noticeboard.Comments;
import com.noticeboard.PostDetails;
import com.noticeboard.PostWithComments;
import com.noticeboard.R;

public class HomeFragment extends Fragment {

    HomePostAdapter postAdapter;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String postID, postTitle, postContent, postPageName, postersID, pageID,pageAdminID;
    View v;
    RelativeLayout relativeLayout;
    private CollectionReference postref;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        relativeLayout = v.findViewById(R.id.welcometextRL);
        floatingActionButton = v.findViewById(R.id.postsFragmentFAB);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PostBottomSheetDialog bottomSheetDialog = new PostBottomSheetDialog();
                bottomSheetDialog.show(getFragmentManager(), "bottomSheet");


            }
        });

        loadPosts();
        recyclerViewOnClick();
    }

    private void loadPosts() {

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("All Posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        postref = firebaseFirestore.collection("Users").document(userID).collection("All Posts");
        Query query = postref.orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PostDetails> options = new FirestoreRecyclerOptions.Builder<PostDetails>()
                .setQuery(query, PostDetails.class)
                .build();

        postAdapter = new HomePostAdapter(options);

        recyclerView = v.findViewById(R.id.homerecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);

    }

    private void recyclerViewOnClick() {

        postAdapter.setOnItemClickListener(new HomePostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                PostDetails post = documentSnapshot.toObject(PostDetails.class);
                postID = post.getPostID();
                postTitle = post.getTitle();
                postContent = post.getContent();
                postPageName = post.getPagename();
                postersID = post.getPostersID();
                pageAdminID = post.getPageAdminID();
                pageID = post.getPageID();

                Toast.makeText(getActivity(),
                        "PageName:" + postPageName + "PostID: " + postID, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getContext(), PostWithComments.class);

                intent.putExtra("pagename", postPageName);
                intent.putExtra("postTitle", postTitle);
                intent.putExtra("postContent", postContent);
                intent.putExtra("postTime", post.getTime());
                intent.putExtra("postID", postID);
                intent.putExtra("postersID", postersID);
                intent.putExtra("pageAdminID", pageAdminID);
                intent.putExtra("pageID", pageID);

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
                                    Toast.makeText(getContext(), "Removed", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();

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

                Toast.makeText(getActivity(),
                        "PageName:" + postPageName + "PostID: " + postID, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getContext(), Comments.class);

                intent.putExtra("pageID", pageID);
                intent.putExtra("postTitle", postTitle);
                intent.putExtra("postContent", postContent);
                intent.putExtra("postID", postID);
                intent.putExtra("pageAdminID", pageAdminID);

                startActivity(intent);

            }
        });
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
}
