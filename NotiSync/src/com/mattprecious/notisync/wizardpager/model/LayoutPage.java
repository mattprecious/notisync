
package com.mattprecious.notisync.wizardpager.model;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.wizardpager.ui.LayoutFragment;

public class LayoutPage extends Page {
    protected int layoutId;

    protected LayoutPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public SherlockFragment createFragment() {
        return LayoutFragment.create(layoutId);
    }

    public LayoutPage setLayout(int resId) {
        layoutId = resId;
        return this;
    }

}
