
package com.mattprecious.notisync.wizardpager.model;

import com.mattprecious.notisync.wizardpager.ui.DeviceFragment;

import org.holoeverywhere.app.Fragment;

public class DevicePage extends Page {

    protected DevicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return DeviceFragment.create();
    }
}
