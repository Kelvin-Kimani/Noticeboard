package com.noticeboard;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagesFollowedFragment extends Fragment {

    PageUserAdapter userAdapterFollowing;
    View v;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), pageID, pname, pinfo, administratorUID, privacy, username, level, phonenumber;

    RelativeLayout relativeLayout;

    public PagesFollowedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pages_followed, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        relativeLayout = v.findViewById(R.id.followpageRL);
        setUpPagesFollowedRecyclerView();
        recyclerViewOnClick();
    }

    private void setUpPagesFollowedRecyclerView() {

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").orderBy("pagename", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(query, PageDetails.class)
                .build();

        userAdapterFollowing = new PageUserAdapter(options);

        recyclerView = v.findViewById(R.id.PagesFollowedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userAdapterFollowing);

    }

    private void recyclerViewOnClick() {

        userAdapterFollowing.setOnItemClickListener(new PageUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pageID = page.getPageID();
                pname = page.getPagename();
                pinfo = page.getPageinfo();
                administratorUID = page.getUserID();
                privacy = page.getPrivacy();

                Toast.makeText(getActivity(),
                        "Position: " + position + " ID: " + pageID + "PN: " + pname + "bio: " + pinfo, Toast.LENGTH_LONG).show();


                Intent intent = new Intent(getActivity(), PageProfileUser.class);
                intent.putExtra("pagename", pname);
                intent.putExtra("pageinfo", pinfo);
                intent.putExtra("pageID", pageID);
                intent.putExtra("adminUID", administratorUID);
                intent.putExtra("privacy", privacy);

                startActivity(intent);

            }
        });

        userAdapterFollowing.setOnButtonClickListener(new PageUserAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(DocumentSnapshot documentSnapshot, int position) {
                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                final String followpagename = page.getPagename();
                final String adminUserID = page.getUserID();
                final String followedPageInfo = page.getPageinfo();
                final String followedPagePrivacy = page.getPrivacy();
                final String followedPageID = page.getPageID();


                //Follow a page
                DocumentReference pagefollower = FirebaseFirestore.getInstance().collection("Users").document(adminUserID).collection("Pages").document(followedPageID).collection("Followers").document(userID);
                pagefollower.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            // unfollow
                            final CollectionReference followers = FirebaseFirestore.getInstance().collection("Users").document(adminUserID).collection("Pages").document(followedPageID).collection("Followers");
                            Query query = followers.whereEqualTo("userID", userID);
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {

                                            followers.document(document.getId()).delete();

                                            final CollectionReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed");
                                            Query query1 = pagefollowed.whereEqualTo("pageID", followedPageID);
                                            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (task.isSuccessful()) {
                                                        for (DocumentSnapshot document : task.getResult()) {

                                                            pagefollowed.document(document.getId()).delete();
                                                            Toast.makeText(getActivity(), "Unfollowed", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            });


                                        }
                                    }
                                }

                            });

                        }

                    }
                });

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        userAdapterFollowing.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userAdapterFollowing.stopListening();
    }
}