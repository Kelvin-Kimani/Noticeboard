package com.noticeboard.ui.newpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.noticeboard.R;

public class NewPageFragment extends Fragment {

    private NewPageViewModel newPageViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        newPageViewModel =
                ViewModelProviders.of(this).get(NewPageViewModel.class);
        View root = inflater.inflate(R.layout.activity_createpage, container, false);
        return root;
    }
}
