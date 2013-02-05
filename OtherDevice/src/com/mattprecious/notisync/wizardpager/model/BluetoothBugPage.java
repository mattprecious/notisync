
package com.mattprecious.notisync.wizardpager.model;

import android.os.Build;

import com.mattprecious.notisync.wizardpager.ui.BluetoothBugFragment;

import org.holoeverywhere.app.Fragment;

import java.util.ArrayList;

public class BluetoothBugPage extends Page {

    protected BluetoothBugPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return BluetoothBugFragment.create();
    }

    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> dest) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            super.flattenCurrentPageSequence(dest);
        }
    }
}
