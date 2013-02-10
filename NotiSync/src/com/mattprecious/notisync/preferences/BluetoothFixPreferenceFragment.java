
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
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.OnSharedPreferenceChangeListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BluetoothFixPreferenceFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {
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

        // need to do it this way because onPreferenceChange fires before the
        // preference is stored, so AlarmManager will get old values
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Preferences.KEY_GLOBAL_BLUETOOTH_FIX_TIME.equals(key)) {
            AlarmHelper.scheduleBluetoothFixAlarm(getActivity());
        }
    }
}
