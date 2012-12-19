package com.mattprecious.otherdevice.preferences;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.db.DbAdapter;
import com.mattprecious.otherdevice.model.SecondaryProfile;

public class SecondaryPreferenceActivity extends SherlockPreferenceActivity {

    DbAdapter dbAdapter;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbAdapter = new DbAdapter(this);

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Load the legacy preferences headers
            addPreferencesFromResource(R.xml.secondary_preference_headers_legacy);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.secondary_preference_headers, target);

        updateHeaderList(target);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void updateHeaderList(List<Header> target) {
        int i = 0;
        while (i < target.size()) {
            Header header = target.get(i);
            int id = (int) header.id;

            if (id == R.id.add_custom) {
                i = addCustomProfiles(target, i) + 1;
            }

            if (i < target.size() && target.get(i) == header) {
                i++;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private int addCustomProfiles(List<Header> target, int headerIndex) {
        dbAdapter.openReadable();
        List<SecondaryProfile> profiles = dbAdapter.getSecondaryProfiles();
        dbAdapter.close();

        for (SecondaryProfile profile : profiles) {
            Header header = new Header();
            header.title = profile.getName();
            header.fragment = SecondaryCustomProfileFragment.class.getName();
            header.iconRes = R.drawable.ic_settings_custom;

            Bundle extras = new Bundle();
            extras.putParcelable("profile", profile);
            header.fragmentArguments = extras;

            target.add(headerIndex++, header);
        }

        return headerIndex;
    }

}
