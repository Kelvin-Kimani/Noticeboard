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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.noticeboard.PageProfileAdapter;
import com.noticeboard.PageDetails;
import com.noticeboard.PostPage;
import com.noticeboard.R;

public class PostBottomSheetDialog extends BottomSheetDialogFragment {

    View v;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    CollectionReference pageref = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("Pages");;
    private PageProfileAdapter adapter;
    RecyclerView recyclerView;
    RelativeLayout relativeLayout;
    String pageID, pname, pinfo, pageAdminID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.post_pop_up, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;
        relativeLayout = v.findViewById(R.id.postpopupRL);

        loadPages();
        recyclerViewOnClick();
    }

    private void loadPages() {

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

        Query query = pageref.orderBy("pagename", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(query, PageDetails.class)
                .build();

        adapter = new PageProfileAdapter(options);

        recyclerView = (RecyclerView)v.findViewById(R.id.postpopuprecyclerview);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


    }


    private void recyclerViewOnClick() {

        adapter.setOnItemClickListener(new PageProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pageID = documentSnapshot.getId();
                pname = page.getPagename();
                pinfo = page.getPageinfo();
                pageAdminID = page.getUserID();

                Toast.makeText(getActivity(),
                        "Position: " + position + " ID: " + pageID + "PN: " + pname + "bio: " + pinfo, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), PostPage.class);
                intent.putExtra("pagename", pname);
                intent.putExtra("pageinfo", pinfo);
                intent.putExtra("pageID", pageID);
                intent.putExtra("pageAdminID", pageAdminID);

                startActivity(intent);

            }

        });
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
