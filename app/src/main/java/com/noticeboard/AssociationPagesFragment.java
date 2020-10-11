package com.noticeboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AssociationPagesFragment extends Fragment {

    PageProfileAdapter adapter;
    RecyclerView recyclerView;
    View v;
    RelativeLayout relativeLayout;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String pageID, adminUID;

    public AssociationPagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_association_pages, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        relativeLayout = v.findViewById(R.id.noAssociationPagesRL);
        setUpAssociatorRV();
        recyclerViewOnClick();

    }

    private void recyclerViewOnClick() {

        adapter.setOnItemClickListener(new PageProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                PageDetails page = documentSnapshot.toObject(PageDetails.class);
                pageID = page.getPageID();
                adminUID = page.getUserID();

                Intent intent = new Intent(getActivity(), AssociatorPage.class);
                intent.putExtra("pageID", pageID);
                intent.putExtra("adminUID", adminUID);

                startActivity(intent);

            }
        });

    }

    private void setUpAssociatorRV() {

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Associated Pages").orderBy("pagename", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(query, PageDetails.class)
                .build();

        adapter = new PageProfileAdapter(options);
        recyclerView = v.findViewById(R.id.associationpagesrecyclerview);
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