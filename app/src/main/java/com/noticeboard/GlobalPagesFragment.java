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
