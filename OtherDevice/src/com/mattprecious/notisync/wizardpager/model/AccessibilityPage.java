package com.mattprecious.notisync.wizardpager.model;

import com.mattprecious.notisync.wizardpager.ui.AccessibilityFragment;

import org.holoeverywhere.app.Fragment;

public class AccessibilityPage extends Page {

    protected AccessibilityPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return AccessibilityFragment.create();
    }

}
