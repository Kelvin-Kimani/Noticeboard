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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagesCreatedFragment extends Fragment {

    FloatingActionButton createpageFAB;
    View v;
    PageProfileAdapter adapter;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), pageID, pname, pinfo;
    RelativeLayout relativeLayout;
    String privacy, adminUserID;

    public PagesCreatedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_pages_created, container, false);
        createpageFAB = view.findViewById(R.id.fab);
        createpageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), CreatePage.class));

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        relativeLayout = v.findViewById(R.id.createpageRL);
        setUpPagesCreatedRecyclerView();
        recyclerViewOnClick();
    }

    private void recyclerViewOnClick() {

        adapter.setOnItemClickListener(new PageProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pageID = page.getPageID();
                adminUserID = page.getUserID();
                pname = page.getPagename();
                pinfo = page.getPageinfo();
                privacy = page.getPrivacy();


                Toast.makeText(getActivity(),
                        "Position: " + position + " ID: " + pageID + "PN: " + pname + "bio: " + pinfo, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), PageProfileAdmin.class);
                intent.putExtra("pageID", pageID);
                intent.putExtra("privacy", privacy);
                intent.putExtra("adminUserID", adminUserID);

                startActivity(intent);

            }
        });


    }

    private void setUpPagesCreatedRecyclerView() {

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages").orderBy("pagename", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(query, PageDetails.class)
                .build();

        adapter = new PageProfileAdapter(options);

        recyclerView = v.findViewById(R.id.pagesCreatedRecyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
