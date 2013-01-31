
package com.mattprecious.notisync.wizardpager.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

public class LayoutFragment extends Fragment {
    public static final String EXTRA_LAYOUT_ID = "layoutId";
    
    public static LayoutFragment create(int layoutId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_LAYOUT_ID, layoutId);

        LayoutFragment fragment = new LayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() == null || !getArguments().containsKey(EXTRA_LAYOUT_ID)) {
            throw new IllegalArgumentException(String.format("Must pass %s as an argument",
                    EXTRA_LAYOUT_ID));
        }

        View rootView = inflater.inflate(getArguments().getInt(EXTRA_LAYOUT_ID), container, false);

        return rootView;
    }
}
