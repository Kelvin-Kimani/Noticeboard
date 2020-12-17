package com.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    Dialog dialog;
    Bundle bundleRecyclerViewState;
    private Parcelable recyclerstate = null;
    private final String KEY_RECYCLER_STATE = "recycler_state";


    public GlobalPagesFragment() {
        // Required empty public constructor
    }

    public static void doSearch(String query) {
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
        dialog = new Dialog(getActivity());

        setUpGlobalPages();
        recyclerViewOnClick();
    }


    public void setUpGlobalPages() {

        Query query = FirebaseFirestore.getInstance().collection("Global Pages").orderBy("userID", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<PageDetails> options1 = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(query, PageDetails.class)
                .build();

        userAdapter = new PageUserAdapter(options1);

        recyclerView = v.findViewById(R.id.globalpagesrecyclerview);
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

                if (userID.equals(administratorUID)) {

                    Toast.makeText(getActivity(), "This is your page", Toast.LENGTH_LONG).show();

                } else {

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

            }
        });

        userAdapter.setOnFollowButtonClickListener(new PageUserAdapter.OnFollowButtonClickListener() {
            @Override
            public void onFollowButtonClick(DocumentSnapshot documentSnapshot, final int position) {

                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pageID = page.getPageID();
                pname = page.getPagename();
                pinfo = page.getPageinfo();
                administratorUID = page.getUserID();
                privacy = page.getPrivacy();

                if ("Private".equals(privacy)) {

                    TextView title, description;

                    dialog.setContentView(R.layout.description_pop_up);
                    title = dialog.findViewById(R.id.title);
                    title.setText("Private");
                    description = dialog.findViewById(R.id.description);
                    description.setText("This page is private and your request has to be accepted first");

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(userID);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                final String username = documentSnapshot.getString("fullname");
                                String level = documentSnapshot.getString("level");
                                String userImage = documentSnapshot.getString("userimage");
                                String phonenumber = documentSnapshot.getString("phonenumber");
                                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                //save requested follower details
                                DocumentReference requested = FirebaseFirestore.getInstance().collection("Users").document(administratorUID).collection("Pages").document(pageID).collection("Requested").document(userID);

                                UserDetails requestedFollower = new UserDetails();
                                requestedFollower.setFullname(username);
                                requestedFollower.setLevel(level);
                                requestedFollower.setUserID(userID);
                                requestedFollower.setPhonenumber(phonenumber);
                                requestedFollower.setUserimage(userImage);
                                requestedFollower.setEmail(email);
                                //add tolowercase

                                requested.set(requestedFollower).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(getActivity(), username + " " + " Requested", Toast.LENGTH_SHORT).show();
                                            userAdapter.notifyItemChanged(position);

                                        }
                                    }
                                });

                            }
                        }

                    });

                } else {

                    //save page details
                    DocumentReference pagefollowed1 = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").document(pageID);
                    pagefollowed1.set(new PageDetails(pname, pinfo, privacy, pageID, administratorUID));

                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(userID);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                final String username = documentSnapshot.getString("fullname");
                                String level = documentSnapshot.getString("level");
                                String userImage = documentSnapshot.getString("userimage");

                                //save follower details
                                DocumentReference followers = FirebaseFirestore.getInstance().collection("Users").document(administratorUID).collection("Pages").document(pageID).collection("Followers").document(userID);

                                UserDetails follower = new UserDetails();
                                follower.setFullname(username);
                                follower.setLevel(level);
                                follower.setUserID(userID);
                                follower.setPhonenumber(phonenumber);
                                follower.setUserimage(userImage);
                                follower.setEmail(email);
                                // add to lowercase

                                followers.set(follower).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(getActivity(), username + " " + " Followed", Toast.LENGTH_SHORT).show();
                                            userAdapter.notifyItemChanged(position);

                                        }
                                    }
                                });

                            }
                        }

                    });
                }

            }

        });

        userAdapter.setOnFollowingButtonClickListener(new PageUserAdapter.OnFollowingButtonClickListener() {
            @Override
            public void onFollowingButtonClick(DocumentSnapshot documentSnapshot, final int position) {

                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pageID = page.getPageID();
                pname = page.getPagename();
                pinfo = page.getPageinfo();
                administratorUID = page.getUserID();
                privacy = page.getPrivacy();

                //check if associator first
                DocumentReference ifassociator = FirebaseFirestore.getInstance().collection("Users").document(administratorUID).collection("Pages").document(pageID).collection("Associators").document(userID);
                ifassociator.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            final TextView title, text, unfollow;
                            final RelativeLayout accept;
                            dialog.setContentView(R.layout.unfollow_pop_up);

                            title = dialog.findViewById(R.id.unfollowTitle);
                            text = dialog.findViewById(R.id.defaulttext);
                            unfollow = dialog.findViewById(R.id.unfollowText);
                            accept = dialog.findViewById(R.id.unfollowview);

                            title.setText("You're an associator");
                            text.setText(R.string.associatorunfollowwarning);
                            unfollow.setText("Yes");

                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    DocumentReference pagefollower1 = FirebaseFirestore.getInstance().collection("Users").document(administratorUID).collection("Pages").document(pageID).collection("Followers").document(userID);
                                    pagefollower1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Unfollowed", Toast.LENGTH_SHORT).show();

                                                //Delete Associator and association page
                                                DocumentReference associator = FirebaseFirestore.getInstance().collection("Users").document(administratorUID).collection("Pages").document(pageID).collection("Associators").document(userID);
                                                associator.delete();

                                                DocumentReference associatedpage = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Associated Pages").document(pageID);
                                                associatedpage.delete();

                                                final CollectionReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed");
                                                Query query = pagefollowed.whereEqualTo("pageID", pageID);
                                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                        if (task.isSuccessful()) {

                                                            for (DocumentSnapshot document : task.getResult()) {

                                                                pagefollowed.document(document.getId()).delete();
                                                                userAdapter.notifyItemChanged(position);

                                                            }
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
                                }
                            });

                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();

                        } else {

                            TextView title;
                            RelativeLayout accept;
                            dialog.setContentView(R.layout.unfollow_pop_up);

                            title = dialog.findViewById(R.id.unfollowTitle);
                            title.setText("Unfollow " + pname);

                            accept = dialog.findViewById(R.id.unfollowview);
                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    DocumentReference pagefollower1 = FirebaseFirestore.getInstance().collection("Users").document(administratorUID).collection("Pages").document(pageID).collection("Followers").document(userID);
                                    pagefollower1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                userAdapter.notifyItemChanged(position);
                                                dialog.dismiss();
                                                Toast.makeText(getActivity(), "Unfollowed", Toast.LENGTH_SHORT).show();

                                                final CollectionReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed");
                                                Query query = pagefollowed.whereEqualTo("pageID", pageID);
                                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                        if (task.isSuccessful()) {

                                                            for (DocumentSnapshot document : task.getResult()) {

                                                                pagefollowed.document(document.getId()).delete();
                                                            }
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    });
                                }
                            });


                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();


                        }
                    }
                });

            }
        });

        userAdapter.setOnRequestedButtonClickListener(new PageUserAdapter.OnRequestedButtonClickListener() {
            @Override
            public void onRequestedButtonClick(DocumentSnapshot documentSnapshot, final int position) {

                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pageID = page.getPageID();
                pname = page.getPagename();
                pinfo = page.getPageinfo();
                administratorUID = page.getUserID();
                privacy = page.getPrivacy();

                TextView title, text, cancel;
                RelativeLayout accept;
                dialog.setContentView(R.layout.unfollow_pop_up);

                title = dialog.findViewById(R.id.unfollowTitle);
                title.setText("Discard Follow Request?");

                text = dialog.findViewById(R.id.defaulttext);
                text.setText("This will cancel your pending request on " + pname);

                accept = dialog.findViewById(R.id.unfollowview);
                cancel = dialog.findViewById(R.id.unfollowText);
                cancel.setText("Cancel Request");

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DocumentReference requested = FirebaseFirestore.getInstance().collection("Users").document(administratorUID).collection("Pages").document(pageID).collection("Requested").document(userID);
                        requested.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    userAdapter.notifyItemChanged(position);
                                    dialog.cancel();
                                    Toast.makeText(getActivity(), "Request has been cancelled", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
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


        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
