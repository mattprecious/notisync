package com.mattprecious.notisync.wizardpager.model;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.wizardpager.ui.AccessibilityFragment;

public class AccessibilityPage extends Page {

    protected AccessibilityPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public SherlockFragment createFragment() {
        return AccessibilityFragment.create();
    }

}
