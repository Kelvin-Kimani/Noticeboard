package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class GlobalPagesFragment extends Fragment {

    View v;
    PageUserAdapter userAdapter;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), pageID, pname, pinfo, administratorUID, privacy, username, level, phonenumber, email;

    public GlobalPagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_global_pages, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        setUpGlobalPages();
        recyclerViewOnClick();
    }


    public void setUpGlobalPages() {

        //Add query to remove user pages from global ones

        Query greaterquery = FirebaseFirestore.getInstance().collection("Global Pages").whereGreaterThan("userID", userID).orderBy("userID", Query.Direction.ASCENDING);
        Query lessquery = FirebaseFirestore.getInstance().collection("Global Pages").whereLessThan("userID", userID).orderBy("userID", Query.Direction.ASCENDING);

        //Task firstQuery = lessquery.get();
        //Task secondQuery = greaterquery.get();

        /*Task combinedQuery = Tasks.whenAllSuccess(firstQuery, secondQuery).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {



            }
        });*/


        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(lessquery, PageDetails.class)
                .build();

        FirestoreRecyclerOptions<PageDetails> options1 = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(greaterquery, PageDetails.class)
                .build();

        userAdapter = new PageUserAdapter(options);
        userAdapter = new PageUserAdapter(options1);

        recyclerView = v.findViewById(R.id.globalpagesrecyclerview);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userAdapter);

    }

    private void recyclerViewOnClick() {

        userAdapter.setOnItemClickListener(new PageUserAdapter.OnItemClickListener() {
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

        userAdapter.setOnButtonClickListener(new PageUserAdapter.OnButtonClickListener() {
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

                        } else {

                            //follow
                            DocumentReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").document();
                            pagefollowed.set(new PageDetails(followpagename, followedPageInfo, followedPagePrivacy, followedPageID, adminUserID));

                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(userID);
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {

                                        username = documentSnapshot.getString("fullname");
                                        level = documentSnapshot.getString("level");
                                        phonenumber = "+254" + documentSnapshot.getString("phonenumber");
                                        email = documentSnapshot.getString("email");

                                        DocumentReference followers = FirebaseFirestore.getInstance().collection("Users").document(adminUserID).collection("Pages").document(followedPageID).collection("Followers").document(userID);
                                        followers.set(new UserDetails(username, level, userID, phonenumber,email)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(getActivity(), username + " " + userID + " Followed", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });

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
        userAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userAdapter.stopListening();
    }
}
