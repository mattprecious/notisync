
package com.mattprecious.notisync.preferences;

import java.util.HashSet;
import java.util.Set;

import org.holoeverywhere.preference.CheckBoxPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.PreferenceFragment;
import org.holoeverywhere.preference.PreferenceScreen;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mattprecious.notisync.util.Constants;
import com.mattprecious.notisync.util.Preferences;
import com.mattprecious.notisync.R;

public class DevicePreferenceFragment extends PreferenceFragment {
    private static final String TAG = "DevicePreferenceFragment";
    
    public static final String EXTRA_SHOW_MENU = "showMenu";

    private final Set<String> localDeviceSet = new HashSet<String>();

    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null || getArguments().getBoolean(EXTRA_SHOW_MENU)) {
            setHasOptionsMenu(true);
        }

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());

        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

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

            int iconResId = getBtClassDrawable(device);
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

    /**
     * From {@link BluetoothPreference}
     */
    private int getBtClassDrawable(BluetoothDevice device) {
        BluetoothClass btClass = device.getBluetoothClass();
        if (btClass != null) {
            switch (btClass.getMajorDeviceClass()) {
                case BluetoothClass.Device.Major.COMPUTER:
                    return R.drawable.ic_bt_laptop;

                case BluetoothClass.Device.Major.PHONE:
                    return R.drawable.ic_bt_cellphone;

                case BluetoothClass.Device.Major.IMAGING:
                    // return R.drawable.ic_bt_imaging;
                    break;

                case BluetoothClass.Device.Major.AUDIO_VIDEO:
                    return R.drawable.ic_bt_headphones;

                default:
                    // unrecognized device class; continue
            }
        } else {
            Log.w(TAG, "mBtClass is null");
        }

        return 0;
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
            broadcastManager.sendBroadcast(new Intent(Constants.ACTION_UPDATE_DEVICES));

            return true;
        }
    };

}
