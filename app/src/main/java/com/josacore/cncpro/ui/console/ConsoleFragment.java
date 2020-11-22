package com.josacore.cncpro.ui.console;

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
import com.josacore.cncpro.ui.adding.AddingViewModel;

public class ConsoleFragment extends Fragment {

    private ConsoleViewModel consoleViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        consoleViewModel =
                ViewModelProviders.of(this).get(ConsoleViewModel.class);
        View root = inflater.inflate(R.layout.fragment_console, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        consoleViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}