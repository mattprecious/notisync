
package com.mattprecious.notisync.preferences;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.Helpers;
import com.mattprecious.notisync.util.Preferences;
import com.mattprecious.notisync.util.Preferences.Mode;

import java.util.List;

public class SettingsActivity extends SherlockPreferenceActivity {
    private Mode originalMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        originalMode = Preferences.getMode(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Preferences.getMode(this) != originalMode) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
        updateHeaderList(target);
    }

    private void updateHeaderList(List<Header> target) {
        int i = 0;
        while (i < target.size()) {
            Header header = target.get(i);
            int id = (int) header.id;

            if (id == R.id.device_preferences) {
                if (!Preferences.isPrimary(this) || BluetoothAdapter.getDefaultAdapter() == null) {
                    target.remove(header);
                }
            } else if (id == R.id.bluetoothfix_preferences) {
                if (!Helpers.hasBluetoothIssue() && !Preferences.getBluetoothFixEnabled(this)) {
                    target.remove(header);
                }
            }

            if (i < target.size() && target.get(i) == header) {
                i++;
            }
        }
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
