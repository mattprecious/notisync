
package com.mattprecious.notisync.wizardpager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mattprecious.notisync.preferences.DevicePreferenceFragment;
import com.mattprecious.notisync.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;

public class DeviceFragment extends Fragment {

    public static DeviceFragment create() {
        DeviceFragment fragment = new DeviceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_devices, container, false);

        // ActionBar items look stupid in the wizard, we'll add our own button
        // for this in-line
        Bundle args = new Bundle();
        args.putBoolean(DevicePreferenceFragment.EXTRA_SHOW_MENU, false);

        Fragment deviceFragment = new DevicePreferenceFragment();
        deviceFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.deviceFragment, deviceFragment)
                .commit();
        
        Button bluetoothButton = (Button) rootView.findViewById(R.id.bluetoothButton);
        bluetoothButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
