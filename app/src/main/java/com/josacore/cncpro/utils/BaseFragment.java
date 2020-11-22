package com.josacore.cncpro.utils;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by ari on 8/16/16.
 */
public abstract class BaseFragment extends Fragment {

    private BaseFragmentCallbacks mCallbacks;

    public interface BaseFragmentCallbacks{
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseFragmentCallbacks) {
            mCallbacks = (BaseFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseExampleFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public abstract boolean onActivityBackPress();


}