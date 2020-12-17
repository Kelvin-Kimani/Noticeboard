package com.noticeboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import com.noticeboard.ui.home.HomePostAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class PagesCreatedFragment extends Fragment {

    private static String staticUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static RecyclerView staticRecyclerView;
    FloatingActionButton createpageFAB;
    View v;
    PageProfileAdapter adapter;
    static PageProfileAdapter staticPageAdapter;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), pageID, pname, pinfo;
    RelativeLayout relativeLayout;
    String privacy, adminUserID;
    Bundle bundleRecyclerViewState;
    private Parcelable recyclerstate = null;
    private final String KEY_RECYCLER_STATE = "recycler_state";

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

        recyclerView = view.findViewById(R.id.pagesCreatedRecyclerView);
        staticRecyclerView = view.findViewById(R.id.pagesCreatedRecyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0){
                    createpageFAB.hide();
                }
                else {
                    createpageFAB.show();
                }
                super.onScrolled(recyclerView, dx, dy);
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

    public static void doSearch(String query) {

        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(FirebaseFirestore.getInstance().collection("Users").document(staticUserID).collection("Pages").orderBy("pagename", Query.Direction.ASCENDING).startAt(query).endAt(query + "\uf8ff"), PageDetails.class)
                .build();

        staticPageAdapter = new PageProfileAdapter(options);
        staticPageAdapter.startListening();
        staticRecyclerView.setAdapter(staticPageAdapter);
    }
}
