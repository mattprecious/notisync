package com.mattprecious.otherdevice.fragment;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mattprecious.otherdevice.R;

public class ThirdPartyProfilesFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = "ThirdPartyProfilesFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standard_list, container, false);

        return view;
    }
}
