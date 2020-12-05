package com.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageProfileUser extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView pagename, pageinfo, posts_no;
    String userID;
    String page_name, page_info, pageID;
    Switch notificationswitch;
    Dialog dialog;
    Character firstLetter;
    TextDrawable drawable;
    String adminUID;
    Button follow, Requested, following;
    private String privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_profile_user);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        notificationswitch = findViewById(R.id.notificationswitch);
        notificationswitch.setChecked(true);

        circleImageView = findViewById(R.id.pageprofileimg);
        pagename = findViewById(R.id.pagename);
        pageinfo = findViewById(R.id.bio);
        follow = findViewById(R.id.followstate);
        Requested = findViewById(R.id.requestedstate);
        following = findViewById(R.id.followingstate);
        posts_no = findViewById(R.id.noofposts);

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        page_info = intent.getStringExtra("pageinfo");
        pageID = intent.getStringExtra("pageID");
        adminUID = intent.getStringExtra("adminUID");
        privacy = intent.getStringExtra("privacy");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
        pagefollowed.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    following.setVisibility(View.VISIBLE);
                    follow.setVisibility(View.GONE);

                } else {
                    //maybe the follower requested
                    DocumentReference requested = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Requested").document(userID);
                    requested.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists()) {
                                Requested.setVisibility(View.VISIBLE);
                                follow.setVisibility(View.GONE);
                            }

                        }
                    });

                }
            }
        });

        pagename.setText(page_name);
        pageinfo.setText(page_info);

        firstLetter = page_name.charAt(0);
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

        CollectionReference page_posts = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Posts");
        page_posts.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    posts_no.setText(Integer.toString(task.getResult().size()));
                }
            }
        });
    }


    public void openPosts(View view) {

        Intent intent = new Intent(PageProfileUser.this, UserPagePosts.class);
        intent.putExtra("userID", userID);
        intent.putExtra("pageID", pageID);
        intent.putExtra("AdminUserID", adminUID);
        intent.putExtra("pagename", page_name);

        startActivity(intent);
    }

    public void followpage(View view) {

        //check page privacy
        DocumentReference checkPrivacy = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID);
        checkPrivacy.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                String privacy = documentSnapshot.getString("privacy");
                if ("Private".equals(privacy)) {

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
                                DocumentReference requested = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Requested").document(userID);
                                requested.set(new UserDetails(username, level, userID, phonenumber, userImage, email)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Requested.setVisibility(View.VISIBLE);
                                            follow.setVisibility(View.GONE);

                                            Toast.makeText(PageProfileUser.this, username + " " + " Requested", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                        }

                    });

                } else {

                    //save page details
                    DocumentReference pagefollowed1 = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").document(pageID);
                    pagefollowed1.set(new PageDetails(page_name, page_info, privacy, pageID, adminUID));

                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(userID);
                    documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                final String username = documentSnapshot.getString("fullname");
                                String level = documentSnapshot.getString("level");
                                String userImage = documentSnapshot.getString("userimage");

                                //save follower details
                                DocumentReference followers = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
                                followers.set(new UserDetails(username, level, userID, userImage)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            following.setVisibility(View.VISIBLE);
                                            follow.setVisibility(View.GONE);

                                            Toast.makeText(PageProfileUser.this, username + " " + " Followed", Toast.LENGTH_SHORT).show();

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

    public void unfollowpage(View view) {

        //check if is an associator
        DocumentReference ifassociator = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Associators").document(userID);
        ifassociator.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        final TextView title, text, unfollow;
                        final RelativeLayout accept;
                        dialog.setContentView(R.layout.unfollow_pop_up);

                        title = dialog.findViewById(R.id.unfollowTitle);
                        text = dialog.findViewById(R.id.defaulttext);
                        unfollow = dialog.findViewById(R.id.unfollowText);
                        accept = dialog.findViewById(R.id.unfollowview);

                        title.setText("You're an associator");
                        text.setText("You'll stop being an associator if you unfollow. Do you wish to continue?");
                        unfollow.setText("Yes");

                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DocumentReference pagefollower1 = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
                                pagefollower1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(PageProfileUser.this, "Unfollowed", Toast.LENGTH_SHORT).show();

                                            //Delete Associator and association page
                                            DocumentReference ifassociator = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Associator").document(userID);
                                            ifassociator.delete();

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
                                                            follow.setVisibility(View.VISIBLE);
                                                            following.setVisibility(View.GONE);
                                                            dialog.cancel();
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

                        final TextView title, text, unfollow;
                        final RelativeLayout accept;
                        dialog.setContentView(R.layout.unfollow_pop_up);

                        title = dialog.findViewById(R.id.unfollowTitle);
                        text = dialog.findViewById(R.id.defaulttext);
                        unfollow = dialog.findViewById(R.id.unfollowText);
                        accept = dialog.findViewById(R.id.unfollowview);

                        title.setText("Unfollow " + page_name);
                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                DocumentReference pagefollower1 = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
                                pagefollower1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(PageProfileUser.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                                            final CollectionReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed");
                                            Query query = pagefollowed.whereEqualTo("pageID", pageID);
                                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    if (task.isSuccessful()) {

                                                        for (DocumentSnapshot document : task.getResult()) {

                                                            pagefollowed.document(document.getId()).delete();
                                                            follow.setVisibility(View.VISIBLE);
                                                            following.setVisibility(View.GONE);
                                                            dialog.cancel();
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

            }
        });

    }

    public void cancelRequest(View view) {

        TextView title, text, cancel;
        RelativeLayout accept;
        dialog.setContentView(R.layout.unfollow_pop_up);

        title = dialog.findViewById(R.id.unfollowTitle);
        title.setText("Discard Follow Request?");

        text = dialog.findViewById(R.id.defaulttext);
        text.setText("This will cancel your pending request on " + page_name);

        accept = dialog.findViewById(R.id.unfollowview);
        cancel = dialog.findViewById(R.id.unfollowText);
        cancel.setText("Cancel Request");

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference requested = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Requested").document(userID);
                requested.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            follow.setVisibility(View.VISIBLE);
                            Requested.setVisibility(View.GONE);
                            dialog.cancel();
                            Toast.makeText(PageProfileUser.this, "Request has been cancelled", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        final MenuInflater inflater = getMenuInflater();

        DocumentReference checkPrivacy = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID);
        checkPrivacy.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                final String privacy = documentSnapshot.getString("privacy");

                if ("Private".equals(privacy)) {

                    inflater.inflate(R.menu.private_account, menu);


                } else {

                    inflater.inflate(R.menu.public_account, menu);

                }

            }
        });

        return true;
    }

    public void publicpopup(MenuItem item) {

        TextView title, description;

        dialog.setContentView(R.layout.description_pop_up);
        title = dialog.findViewById(R.id.title);
        title.setText("Public");
        description = dialog.findViewById(R.id.description);
        description.setText("This page is open to everyone and therefore one can follow to get updates.");

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void privatepopup(MenuItem item) {

        TextView title, description;

        dialog.setContentView(R.layout.description_pop_up);
        title = dialog.findViewById(R.id.title);
        title.setText("Private");
        description = dialog.findViewById(R.id.description);
        description.setText("This page is private and one has to request to follow this page.");

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }
}


