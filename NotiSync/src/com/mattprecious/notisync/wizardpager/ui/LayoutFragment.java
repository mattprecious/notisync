
package com.mattprecious.notisync.wizardpager.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

public class LayoutFragment extends Fragment {
    public static final String EXTRA_LAYOUT_ID = "layoutId";

    protected int layoutId;

    public static LayoutFragment create(int layoutId) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_LAYOUT_ID, layoutId);

        LayoutFragment fragment = new LayoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args == null || !args.containsKey(EXTRA_LAYOUT_ID)) {
            throw new IllegalArgumentException(String.format("Must pass %s as an argument",
                    EXTRA_LAYOUT_ID));
        }

        layoutId = args.getInt(EXTRA_LAYOUT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(layoutId, container, false);

        return rootView;
    }
}
