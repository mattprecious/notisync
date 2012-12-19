package com.mattprecious.otherdevice.preferences;

import java.util.HashSet;
import java.util.Set;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.Preferences;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DevicePreferenceFragment extends PreferenceFragment {
    private static final String TAG = "DevicePreferenceFragment";

    private final Set<String> localDeviceSet = new HashSet<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(
                getActivity());

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            return;
        }

        if (!btAdapter.isEnabled()) {
            // TODO: handle bt being disabled
        }

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
                preference.setChecked(true);
                localDeviceSet.add(device.getAddress());
            }

            preferenceScreen.addPreference(preference);
        }

        setPreferenceScreen(preferenceScreen);
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
                    return R.drawable.ic_bt_imaging;

                case BluetoothClass.Device.Major.AUDIO_VIDEO:
                    return R.drawable.ic_bt_headphones_a2dp;

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
            getActivity().sendBroadcast(new Intent(Constants.ACTION_UPDATE_DEVICES));

            return true;
        }
    };

}
