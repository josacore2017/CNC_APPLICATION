package com.josacore.cncpro.ui.adding;

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

import com.josacore.cncpro.R;

public class AddingFragment extends Fragment {

    private AddingViewModel addingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addingViewModel =
                ViewModelProviders.of(this).get(AddingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_adding, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        addingViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}