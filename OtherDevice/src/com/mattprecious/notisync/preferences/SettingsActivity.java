
package com.mattprecious.notisync.preferences;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.util.Preferences;
import com.mattprecious.notisync.R;

import org.holoeverywhere.preference.PreferenceActivity;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    public static final String EXTRA_SELECT_HEADER = "selectHeader";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        if (Preferences.isPrimary(this)) {
            loadHeadersFromResource(R.xml.primary_preference_headers, target);
            updatePrimaryHeaderList(target);
        } else {
            loadHeadersFromResource(R.xml.secondary_preference_headers, target);
            updateSecondaryHeaderList(target);
        }

    }

    private void updatePrimaryHeaderList(List<Header> target) {
        int i = 0;
        while (i < target.size()) {
            Header header = target.get(i);
            int id = (int) header.id;

            if (id == R.id.device_preferences) {
                // this should be caught well before getting here, but just in
                // case
                if (BluetoothAdapter.getDefaultAdapter() == null) {
                    target.remove(header);
                }
            }

            if (i < target.size() && target.get(i) == header) {
                i++;
            }
        }
    }

    private void updateSecondaryHeaderList(List<Header> target) {
        // int i = 0;
        // while (i < target.size()) {
        // Header header = target.get(i);
        // int id = (int) header.id;
        //
        // if (i < target.size() && target.get(i) == header) {
        // i++;
        // }
        // }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
