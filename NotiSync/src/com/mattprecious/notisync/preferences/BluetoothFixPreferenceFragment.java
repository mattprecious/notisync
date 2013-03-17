
package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mattprecious.notisync.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BluetoothFixPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.global_bluetoothfix_preferences);
    }
}
