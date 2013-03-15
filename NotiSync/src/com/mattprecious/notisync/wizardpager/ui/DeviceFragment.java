
package com.mattprecious.notisync.wizardpager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.R;

public class DeviceFragment extends SherlockFragment {

    public static DeviceFragment create() {
        DeviceFragment fragment = new DeviceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_devices, container, false);

//        SherlockFragment deviceFragment = new DevicePreferenceFragment();
//        deviceFragment.setArguments(args);
//
//        getSupportFragmentManager().beginTransaction().replace(R.id.deviceFragment, deviceFragment)
//                .commit();

        Button bluetoothButton = (Button) rootView.findViewById(R.id.bluetoothButton);
        bluetoothButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
            }
        });

        return rootView;
    }
}
