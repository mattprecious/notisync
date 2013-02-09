
package com.mattprecious.notisync.wizardpager.model;

import com.mattprecious.notisync.util.Helpers;
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
        if (Helpers.hasBluetoothIssue()) {
            super.flattenCurrentPageSequence(dest);
        }
    }
}
