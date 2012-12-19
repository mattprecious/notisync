package com.mattprecious.otherdevice.preferences;

import java.util.HashSet;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.Preferences;

public class DevicePreferenceActivity extends SherlockPreferenceActivity {
    private final Set<String> localDeviceSet = new HashSet<String>();

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Manage Devices");

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(this);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            return;
        }

        Set<String> selectedDevices = Preferences.getDevices(this);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            CheckBoxPreference preference = new CheckBoxPreference(this);
            preference.setOnPreferenceChangeListener(preferenceListener);
            preference.setTitle(device.getName());
            preference.setSummary(device.getAddress());

            if (selectedDevices.contains(device.getAddress())) {
                preference.setChecked(true);
                localDeviceSet.add(device.getAddress());
            }

            preferenceScreen.addPreference(preference);
        }

        setPreferenceScreen(preferenceScreen);
    }

    private OnPreferenceChangeListener preferenceListener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if ("true".equals(newValue.toString())) {
                localDeviceSet.add((String) preference.getSummary());
            } else {
                localDeviceSet.remove((String) preference.getSummary());
            }

            Preferences.setDevices(getApplicationContext(), localDeviceSet);
            sendBroadcast(new Intent(Constants.ACTION_UPDATE_DEVICES));

            return true;
        }
    };
}
