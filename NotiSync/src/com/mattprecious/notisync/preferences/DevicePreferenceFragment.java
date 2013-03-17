
package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.service.PrimaryService;
import com.mattprecious.notisync.util.Helpers;
import com.mattprecious.notisync.util.Preferences;

import java.util.HashSet;
import java.util.Set;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DevicePreferenceFragment extends PreferenceFragment {
    @SuppressWarnings("unused")
    private static final String TAG = "DevicePreferenceFragment";

    private final Set<String> localDeviceSet = new HashSet<String>();

    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());

        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            return;
        }

        PreferenceScreen screen = getPreferenceScreen();
        screen.removeAll();

        Set<String> selectedDevices = Preferences.getDevices(getActivity());

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            CheckBoxPreference preference = new CheckBoxPreference(getActivity());
            preference.setOnPreferenceChangeListener(preferenceListener);
            preference.setTitle(device.getName());
            preference.setSummary(device.getAddress());

            int iconResId = Helpers.getBtClassDrawable(device);
            if (iconResId != 0) {
                preference.setIcon(iconResId);
            }

            if (selectedDevices.contains(device.getAddress())) {
                preference.setDefaultValue(true);
                localDeviceSet.add(device.getAddress());
            }

            getPreferenceScreen().addPreference(preference);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.devices, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_bt_devices:
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private OnPreferenceChangeListener preferenceListener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if ((Boolean) newValue) {
                localDeviceSet.add((String) preference.getSummary());
            } else {
                localDeviceSet.remove((String) preference.getSummary());
            }

            Preferences.setDevices(getActivity(), localDeviceSet);
            broadcastManager.sendBroadcast(new Intent(PrimaryService.ACTION_UPDATE_DEVICES));

            return true;
        }
    };

}
