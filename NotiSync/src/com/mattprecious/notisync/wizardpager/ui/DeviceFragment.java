/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.mattprecious.notisync.fragment.DeviceListFragment;

public class DeviceFragment extends SherlockFragment {

    public static DeviceFragment create() {
        DeviceFragment fragment = new DeviceFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_devices, container, false);

        SherlockFragment deviceFragment = new DeviceListFragment();
        getFragmentManager().beginTransaction().replace(R.id.deviceFragment, deviceFragment)
                .commit();

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
