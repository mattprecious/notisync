package com.mattprecious.otherdevice.preferences;

import java.util.List;

import org.holoeverywhere.preference.PreferenceActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.view.MenuItem;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.util.Preferences;

public class PrimaryPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Preferences.isPrimary(this)) {
            startActivity(new Intent(this, SecondaryPreferenceActivity.class));
            finish();
        }
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.primary_preference_headers, target);

        updateHeaderList(target);
    }

    private void updateHeaderList(List<Header> target) {
        int i = 0;
        while (i < target.size()) {
            Header header = target.get(i);
            int id = (int) header.id;

            if (id == R.id.device_preferences) {
                // this should be caught well before getting here, but just in case
                if (BluetoothAdapter.getDefaultAdapter() == null) {
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
