
package com.mattprecious.notisync.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mattprecious.notisync.R;

public class EmptyFragment extends StandardProfileFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_empty, container, false);

        return rootView;
    }

    @Override
    public boolean onSave() {
        return false;
    }

}
