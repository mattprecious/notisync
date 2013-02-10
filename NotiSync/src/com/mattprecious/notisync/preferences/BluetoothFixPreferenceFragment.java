
package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.AlarmHelper;
import com.mattprecious.notisync.util.Preferences;

import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BluetoothFixPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.global_bluetoothfix_preferences);

        findPreference(Preferences.KEY_GLOBAL_BLUETOOTH_FIX_ENABLED).setOnPreferenceChangeListener(
                new OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean enabled = ((Boolean) newValue).booleanValue();
                        if (enabled) {
                            AlarmHelper.scheduleBluetoothFixAlarm(getActivity());
                        } else {
                            AlarmHelper.cancelBluetoothFixAlarm(getActivity());
                        }

                        return true;
                    }

                });

        findPreference(Preferences.KEY_GLOBAL_BLUETOOTH_FIX_TIME).setOnPreferenceChangeListener(
                new OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        AlarmHelper.scheduleBluetoothFixAlarm(getActivity());
                        return true;
                    }

                });
    }
}
