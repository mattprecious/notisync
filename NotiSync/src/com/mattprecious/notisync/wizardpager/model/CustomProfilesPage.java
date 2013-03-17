
package com.mattprecious.notisync.wizardpager.model;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.wizardpager.ui.CustomProfilesFragment;

public class CustomProfilesPage extends LayoutPage {

    protected CustomProfilesPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public SherlockFragment createFragment() {
        return CustomProfilesFragment.create();
    }
}
