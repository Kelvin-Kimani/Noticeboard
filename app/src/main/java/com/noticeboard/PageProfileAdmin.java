package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PageProfileAdmin extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView pagename,pageinfo;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String userID;
    Switch notificationswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_profile_admin);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        notificationswitch = (Switch) findViewById(R.id.notificationswitch);
        notificationswitch.setChecked(true);

        circleImageView = (CircleImageView) findViewById(R.id.pageprofileimg);
        pagename = (TextView) findViewById(R.id.pagename);
        pageinfo = (TextView) findViewById(R.id.bio);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userID = auth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Users").document(userID).collection("Pages").document();
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pagename.setText(documentSnapshot.getString("pagename"));
                pageinfo.setText(documentSnapshot.getString("pageinfo"));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);

        return true;
    }

    public void popupprivacy(View view) {

    }

    public void openPagePosts(View view) {
        startActivity(new Intent(PageProfileAdmin.this, PagePosts.class));
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), Pages.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

}
