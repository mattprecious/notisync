
package com.mattprecious.otherdevice.wizardpager.model;

import com.mattprecious.otherdevice.wizardpager.ui.LayoutFragment;

import org.holoeverywhere.app.Fragment;

public class LayoutPage extends Page {
    private int layoutId;

    protected LayoutPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return LayoutFragment.create(layoutId);
    }

    public LayoutPage setLayout(int resId) {
        layoutId = resId;
        return this;
    }

}
