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

package com.mattprecious.notisync.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.service.PrimaryService;
import com.mattprecious.notisync.util.Helpers;
import com.mattprecious.notisync.util.Preferences;

import java.util.List;
import java.util.Set;

public class DeviceListFragment extends SherlockFragment {
    private LocalBroadcastManager broadcastManager;
    private Set<String> localDeviceSet;

    private ListView deviceList;
    private DeviceAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.device_list, container, false);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        localDeviceSet = Sets.newHashSet(Preferences.getDevices(getActivity()));

        listAdapter = new DeviceAdapter(getActivity(), Lists.newArrayList(btAdapter
                .getBondedDevices()));

        deviceList = (ListView) rootView.findViewById(R.id.deviceList);
        deviceList.setAdapter(listAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().setContext(getActivity());
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    private class DeviceAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<BluetoothDevice> data;

        public DeviceAdapter(Context context, List<BluetoothDevice> data) {
            this.inflater = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public BluetoothDevice getItem(int position) throws IndexOutOfBoundsException {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) throws IndexOutOfBoundsException {
            if (position < getCount() && position >= 0) {
                return position;
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BluetoothDevice device = getItem(position);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.device_list_item, parent, false);
            }

            ((ImageView) convertView.findViewById(android.R.id.icon)).setImageResource(Helpers
                    .getBtClassDrawable(device));

            ((TextView) convertView.findViewById(android.R.id.title)).setText(device.getName());

            ((TextView) convertView.findViewById(android.R.id.summary))
                    .setText(device.getAddress());

            CheckBox checkBox = (CheckBox) convertView.findViewById(android.R.id.checkbox);
            checkBox.setTag(device.getAddress());
            checkBox.setChecked(localDeviceSet.contains(device.getAddress()));
            checkBox.setOnCheckedChangeListener(changeListener);

            convertView.setTag(checkBox);
            convertView.setOnClickListener(clickListener);

            return convertView;
        }
    }

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            ((CheckBox) v.getTag()).performClick();
        }
    };

    private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                localDeviceSet.add((String) buttonView.getTag());
            } else {
                localDeviceSet.remove((String) buttonView.getTag());
            }

            Preferences.setDevices(getActivity(), localDeviceSet);
            broadcastManager.sendBroadcast(new Intent(PrimaryService.ACTION_UPDATE_DEVICES));
        }
    };
}
