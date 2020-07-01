package com.noticeboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amulyakhare.textdrawable.TextDrawable;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class PageProfileUser extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView pagename, pageinfo;
    ToggleButton follow;
    FirebaseAuth auth;
    String userID;
    String page_name, page_info, pageID;
    Switch notificationswitch;
    Dialog dialog;
    Character firstLetter;
    TextDrawable drawable;
    String adminUID;
    private String privacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_profile_user);
        dialog = new Dialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        notificationswitch = findViewById(R.id.notificationswitch);
        notificationswitch.setChecked(true);

        circleImageView = findViewById(R.id.pageprofileimg);
        pagename = findViewById(R.id.pagename);
        pageinfo = findViewById(R.id.bio);
        follow = findViewById(R.id.followbutton);

        Intent intent = getIntent();
        page_name = intent.getStringExtra("pagename");
        page_info = intent.getStringExtra("pageinfo");
        pageID = intent.getStringExtra("pageID");
        adminUID = intent.getStringExtra("adminUID");
        privacy = intent.getStringExtra("privacy");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").document(pageID);
        pagefollowed.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {

                    follow.setChecked(true);

                } else {
                    follow.setChecked(false);
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

    }

    public void onToggleClick(View view) {

        if (((ToggleButton) view).isChecked()) {

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

                        DocumentReference followers = FirebaseFirestore.getInstance().collection("Users").document(adminUID).collection("Pages").document(pageID).collection("Followers").document(userID);
                        followers.set(new UserDetails(username, level, userID,  userImage)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(PageProfileUser.this, username + " " + " Followed", Toast.LENGTH_SHORT).show();
                                    follow.setChecked(true);

                                }
                            }
                        });

                    }
                }

            });

        } else {

            TextView title, text;
            RelativeLayout accept;
            dialog.setContentView(R.layout.unfollow_pop_up);

            title = dialog.findViewById(R.id.unfollowTitle);
            title.setText("Unfollow " + page_name);

            accept = dialog.findViewById(R.id.unfollowview);

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
                                                follow.setChecked(false);
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
            follow.setChecked(true);
            dialog.show();

        }

    }

    public void openPosts(View view) {

        Intent intent = new Intent(PageProfileUser.this, UserPagePosts.class);
        intent.putExtra("userID", userID);
        intent.putExtra("pageID", pageID);
        intent.putExtra("AdminUserID", adminUID);

        startActivity(intent);
    }
}

