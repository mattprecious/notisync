
package com.mattprecious.notisync.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.common.collect.Lists;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.activity.MainActivity;
import com.mattprecious.notisync.db.DbAdapter;
import com.mattprecious.notisync.model.PrimaryProfile;
import com.mattprecious.notisync.profile.PrimaryCustomProfileActivity;
import com.mattprecious.notisync.util.UndoBarController;

import org.jraf.android.backport.switchwidget.Switch;

import java.util.List;

public class PrimaryCustomProfileListFragment extends Fragment implements
        UndoBarController.UndoListener {
    @SuppressWarnings("unused")
    private final String TAG = getClass().getName();

    private DbAdapter dbAdapter;
    private LocalBroadcastManager broadcastManager;
    private ListView listView;
    private CustomProfileAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_list, container, false);

        dbAdapter = new DbAdapter(getActivity());

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        broadcastManager.registerReceiver(profileUpdateReceiver, new IntentFilter(
                DbAdapter.ACTION_PROFILES_UPDATED));

        listAdapter = new CustomProfileAdapter(getActivity());
        listView = (ListView) view.findViewById(R.id.list);

        View footer = inflater.inflate(R.layout.custom_footer, listView, false);
        listView.addFooterView(footer);

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PrimaryCustomProfileActivity.class);

                if (position < listView.getCount() - listView.getFooterViewsCount()) {
                    PrimaryProfile profile = (PrimaryProfile) parent.getItemAtPosition(position);
                    intent.putExtra("profile", profile);
                }

                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_EDIT_PROFILE);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadProfiles();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            broadcastManager.unregisterReceiver(profileUpdateReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    private void reloadProfiles() {
        dbAdapter.openReadable();
        List<PrimaryProfile> profiles = dbAdapter.getPrimaryProfiles();
        dbAdapter.close();

        listAdapter.setData(profiles);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUndo(Parcelable token) {
        PrimaryProfile profile = (PrimaryProfile) token;

        dbAdapter.openWritable();
        dbAdapter.insertPrimaryProfile(profile);
        dbAdapter.close();

        reloadProfiles();
    }

    private BroadcastReceiver profileUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            reloadProfiles();
        }
    };

    private class CustomProfileAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<PrimaryProfile> data;

        public CustomProfileAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            data = Lists.newArrayList();
        }

        public void setData(List<PrimaryProfile> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public PrimaryProfile getItem(int position) throws IndexOutOfBoundsException {
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
            PrimaryProfile profile = getItem(position);

            if (convertView == null) { // If the View is not cached
                // Inflates the Common View from XML file
                convertView = this.inflater.inflate(R.layout.profile_list_item, null);
            }

            convertView.setTag(profile);

            TextView nameView = (TextView) convertView.findViewById(R.id.profile_name);
            nameView.setText(profile.getName());
            nameView.setEnabled(profile.isEnabled());

            Switch profileSwitch = (Switch) convertView.findViewById(R.id.profile_switch);
            profileSwitch.setChecked(profile.isEnabled());
            profileSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    View parent = (View) buttonView.getParent();
                    PrimaryProfile profile = (PrimaryProfile) parent.getTag();
                    profile.setEnabled(isChecked);

                    // TODO: not this
                    parent.findViewById(R.id.profile_name).setEnabled(isChecked);

                    dbAdapter.openWritable();
                    dbAdapter.updatePrimaryProfile(profile);
                    dbAdapter.close();
                }
            });

            return convertView;
        }
    }
}
