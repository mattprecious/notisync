
package com.mattprecious.notisync.profile;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;

public abstract class StandardProfileFragment extends SherlockFragment {

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    public abstract boolean onSave();
}
