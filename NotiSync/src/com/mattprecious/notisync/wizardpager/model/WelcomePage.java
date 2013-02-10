package com.mattprecious.notisync.wizardpager.model;

import com.mattprecious.notisync.wizardpager.ui.WelcomeFragment;

import org.holoeverywhere.app.Fragment;

public class WelcomePage extends Page {

    protected WelcomePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return WelcomeFragment.create();
    }

}
