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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
public class PagesFollowedFragment extends Fragment {

    PageUserAdapter followingAdapter;
    View v;
    RecyclerView recyclerView;
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid(), pageID, pname, pinfo, administratorUID, privacy, username, level, phonenumber;
    RelativeLayout relativeLayout;
    private FirestoreRecyclerAdapter adapter;
    Dialog dialog;
    Bundle bundleRecyclerViewState;
    private Parcelable recyclerstate = null;
    private final String KEY_RECYCLER_STATE = "recycler_state";


    public PagesFollowedFragment() {
        // Required empty public constructor
    }

    public static void doSearch(String query) {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pages_followed, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.v = view;

        dialog = new Dialog(getActivity());
        relativeLayout = v.findViewById(R.id.followpageRL);
        setUpPagesFollowedRecyclerView();
        recyclerViewOnClick();
    }

    private void setUpPagesFollowedRecyclerView() {

        Task<QuerySnapshot> queryforemptiness = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        Query query = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed").orderBy("pagename", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<PageDetails> options = new FirestoreRecyclerOptions.Builder<PageDetails>()
                .setQuery(query, PageDetails.class)
                .build();

        followingAdapter = new PageUserAdapter(options);

        recyclerView = v.findViewById(R.id.PagesFollowedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(followingAdapter);

    }

    private void recyclerViewOnClick() {

        followingAdapter.setOnItemClickListener(new PageUserAdapter.OnItemClickListener() {
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

        followingAdapter.setOnFollowingButtonClickListener(new PageUserAdapter.OnFollowingButtonClickListener() {
            @Override
            public void onFollowingButtonClick(DocumentSnapshot documentSnapshot, final int position) {

                PageDetails page = documentSnapshot.toObject(PageDetails.class);

                pageID = page.getPageID();
                pname = page.getPagename();
                pinfo = page.getPageinfo();
                administratorUID = page.getUserID();
                privacy = page.getPrivacy();

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

                                    Toast.makeText(getActivity(), "Unfollowed", Toast.LENGTH_SHORT).show();
                                    final CollectionReference pagefollowed = FirebaseFirestore.getInstance().collection("Users").document(userID).collection("PagesFollowed");
                                    Query query = pagefollowed.whereEqualTo("pageID", pageID);
                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if (task.isSuccessful()) {

                                                for (DocumentSnapshot document : task.getResult()) {

                                                    pagefollowed.document(document.getId()).delete();
                                                    followingAdapter.notifyItemChanged(position);
                                                    dialog.dismiss();
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
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        followingAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        followingAdapter.stopListening();
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
