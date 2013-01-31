package com.mattprecious.otherdevice.wizardpager.model;

import com.mattprecious.otherdevice.wizardpager.ui.WelcomeFragment;

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
