
package com.mattprecious.notisync.wizardpager.model;

import com.mattprecious.notisync.wizardpager.ui.CustomProfilesFragment;

import org.holoeverywhere.app.Fragment;

public class CustomProfilesPage extends LayoutPage {

    protected CustomProfilesPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return CustomProfilesFragment.create();
    }
}
