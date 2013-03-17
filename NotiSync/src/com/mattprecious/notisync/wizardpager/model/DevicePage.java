
package com.mattprecious.notisync.wizardpager.model;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.wizardpager.ui.DeviceFragment;

public class DevicePage extends Page {

    protected DevicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public SherlockFragment createFragment() {
        return DeviceFragment.create();
    }
}
