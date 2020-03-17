package com.noticeboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentAssociators extends Fragment {

    View view;
    FloatingActionButton floatingActionButton;
    public FragmentAssociators() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        floatingActionButton = (FloatingActionButton)view.findViewById(R.id.fab);
        view = inflater.inflate(R.layout.fragment_associators, container, false);
        return view;
    }
}
