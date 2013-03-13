
package com.mattprecious.notisync.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.activity.MainActivity;
import com.mattprecious.notisync.profile.StandardProfileActivity;
import com.mattprecious.notisync.profile.StandardProfileActivity.ProfileType;
import com.mattprecious.notisync.util.Preferences;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Switch;
import org.holoeverywhere.widget.TextView;

import java.util.ArrayList;

public class StandardProfileListFragment extends Fragment {
    @SuppressWarnings("unused")
    private final String TAG = getClass().getName();

    private ListView listView;
    private StandardProfileAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standard_list, container, false);

        ArrayList<ProfileType> titles = new ArrayList<ProfileType>();
        titles.add(ProfileType.TEXT);
        titles.add(ProfileType.PHONE);
        titles.add(ProfileType.GTALK);

        listAdapter = new StandardProfileAdapter(getActivity(), titles);

        listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProfileType type = (ProfileType) parent.getItemAtPosition(position);

                Intent intent = new Intent();
                intent.setClass(getActivity(), StandardProfileActivity.class);
                intent.putExtra(StandardProfileActivity.EXTRA_TYPE, type);

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

        listAdapter.notifyDataSetChanged();
    }

    private String getNameFromType(ProfileType type) {
        String name;
        switch (type) {
            case TEXT:
                name = getString(R.string.profile_text_messages);
                break;
            case PHONE:
                name = getString(R.string.profile_phone_calls);
                break;
            case GTALK:
                name = getString(R.string.profile_gtalk);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return name;
    }

    private boolean isEnabledFromType(ProfileType type) {
        boolean isPrimary = Preferences.isPrimary(getActivity());

        switch (type) {
            case TEXT:
                if (isPrimary) {
                    return Preferences.getPrimaryTextMessageEnabled(getActivity());
                } else {
                    return Preferences.getSecondaryTextMessageEnabled(getActivity());
                }
            case PHONE:
                if (isPrimary) {
                    return Preferences.getPrimaryPhoneCallEnabled(getActivity());
                } else {
                    return Preferences.getSecondaryPhoneCallEnabled(getActivity());
                }
            case GTALK:
                if (isPrimary) {
                    return Preferences.getPrimaryGtalkEnabled(getActivity());
                } else {
                    return Preferences.getSecondaryGtalkEnabled(getActivity());
                }
        }

        return false;
    }

    private void setEnabledFromType(ProfileType type, boolean enabled) {
        boolean isPrimary = Preferences.isPrimary(getActivity());

        switch (type) {
            case TEXT:
                if (isPrimary) {
                    Preferences.setPrimaryTextMessageEnabled(getActivity(), enabled);
                } else {
                    Preferences.setSecondaryTextMessageEnabled(getActivity(), enabled);
                }
                break;
            case PHONE:
                if (isPrimary) {
                    Preferences.setPrimaryPhoneCallEnabled(getActivity(), enabled);
                } else {
                    Preferences.setSecondaryPhoneCallEnabled(getActivity(), enabled);
                }
                break;
            case GTALK:
                if (isPrimary) {
                    Preferences.setPrimaryGtalkEnabled(getActivity(), enabled);
                } else {
                    Preferences.setSecondaryGtalkEnabled(getActivity(), enabled);
                }
                break;
        }
    }

    private class StandardProfileAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<ProfileType> data;

        public StandardProfileAdapter(Context context, ArrayList<ProfileType> data) {
            this.inflater = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public ProfileType getItem(int position) throws IndexOutOfBoundsException {
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
            ProfileType type = getItem(position);

            if (convertView == null) { // If the View is not cached
                // Inflates the Common View from XML file
                convertView = this.inflater.inflate(R.layout.profile_list_item, null);
            }

            convertView.setTag(type);

            TextView nameView = (TextView) convertView.findViewById(R.id.profile_name);
            nameView.setText(getNameFromType(type));
            nameView.setEnabled(isEnabledFromType(type));

            Switch profileSwitch = (Switch) convertView.findViewById(R.id.profile_switch);
            profileSwitch.setChecked(isEnabledFromType(type));
            profileSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    View parent = (View) buttonView.getParent();
                    ProfileType type = (ProfileType) parent.getTag();
                    setEnabledFromType(type, isChecked);

                    // TODO: not this
                    parent.findViewById(R.id.profile_name).setEnabled(isChecked);
                }
            });

            return convertView;
        }
    }
}
