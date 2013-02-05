
package com.mattprecious.notisync.fragment;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mattprecious.notisync.R;

public class ThirdPartyProfileListFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = "ThirdPartyProfileListFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standard_list, container, false);

        return view;
    }
}
