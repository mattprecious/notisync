
package com.mattprecious.notisync.profile;

import com.google.analytics.tracking.android.EasyTracker;

import org.holoeverywhere.app.Fragment;

public abstract class StandardProfileFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    public abstract boolean onSave();
}
