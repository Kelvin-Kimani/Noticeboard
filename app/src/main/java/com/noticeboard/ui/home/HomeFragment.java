package com.noticeboard.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.noticeboard.R;
import com.noticeboard.CreatePage;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    FloatingActionButton floatingActionButton;

    public HomeFragment() {

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       /* homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);*/

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        floatingActionButton = (FloatingActionButton) root.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CreatePage.class);
                startActivity(intent);
            }
        });

        return root;

    }

}
