package com.mattprecious.notisync.wizardpager.model;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.wizardpager.ui.WelcomeFragment;

public class WelcomePage extends Page {

    protected WelcomePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public SherlockFragment createFragment() {
        return WelcomeFragment.create();
    }

}
