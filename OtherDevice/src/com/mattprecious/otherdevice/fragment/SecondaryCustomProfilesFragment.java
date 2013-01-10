
package com.mattprecious.otherdevice.fragment;

import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Switch;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.common.collect.Lists;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.activity.SecondaryCustomProfileActivity;
import com.mattprecious.otherdevice.db.DbAdapter;
import com.mattprecious.otherdevice.model.SecondaryProfile;
import com.mattprecious.otherdevice.util.UndoBarController;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SecondaryCustomProfilesFragment extends Fragment implements
        UndoBarController.UndoListener {
    @SuppressWarnings("unused")
    private static final String TAG = "SecondaryCustomProfilesFragment";

    private static final int REQUEST_CODE_EDIT_PROFILE = 1;

    public static final int RESULT_CODE_PROFILE_DELETED = 2;

    private DbAdapter dbAdapter;
    private ListView listView;
    private CustomProfileAdapter listAdapter;
    private UndoBarController mUndoBarController;

    public SecondaryCustomProfilesFragment() {

    }

    public SecondaryCustomProfilesFragment(UndoBarController undoBarController) {
        mUndoBarController = undoBarController;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_list, container, false);

        dbAdapter = new DbAdapter(getActivity());

        listAdapter = new CustomProfileAdapter(getActivity());
        listView = (ListView) view.findViewById(R.id.list);

        View footer = inflater.inflate(R.layout.custom_footer);
        listView.addFooterView(footer);

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SecondaryCustomProfileActivity.class);

                if (position < listView.getCount() - listView.getFooterViewsCount()) {
                    SecondaryProfile profile = (SecondaryProfile) parent
                            .getItemAtPosition(position);
                    intent.putExtra("profile", profile);
                }

                startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadProfiles();
    }

    private void reloadProfiles() {
        dbAdapter.openReadable();
        List<SecondaryProfile> profiles = dbAdapter.getSecondaryProfiles();
        dbAdapter.close();

        listAdapter.setData(profiles);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_EDIT_PROFILE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Crouton.showText(getActivity(), R.string.profile_saved, Style.CONFIRM);
                        break;
                    case Activity.RESULT_CANCELED:
                        Crouton.showText(getActivity(), R.string.profile_discarded, Style.INFO);
                        break;
                    case RESULT_CODE_PROFILE_DELETED:
                        if (mUndoBarController != null) {
                            mUndoBarController.showUndoBar(true,
                                    getString(R.string.profile_deleted),
                                    data.getParcelableExtra("profile"));
                        }
                        break;
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onUndo(Parcelable token) {
        SecondaryProfile profile = (SecondaryProfile) token;

        dbAdapter.openWritable();
        dbAdapter.insertSecondaryProfile(profile);
        dbAdapter.close();

        reloadProfiles();
    }

    private class CustomProfileAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<SecondaryProfile> data;

        public CustomProfileAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            data = Lists.newArrayList();
        }

        public void setData(List<SecondaryProfile> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public SecondaryProfile getItem(int position) throws IndexOutOfBoundsException {
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
            SecondaryProfile profile = getItem(position);

            if (convertView == null) { // If the View is not cached
                // Inflates the Common View from XML file
                convertView = this.inflater.inflate(R.layout.list_row, null);
            }

            convertView.setTag(profile);

            TextView nameView = (TextView) convertView.findViewById(R.id.profile_name);
            nameView.setText(profile.getName());

            Switch profileSwitch = (Switch) convertView.findViewById(R.id.profile_switch);
            profileSwitch.setChecked(profile.isEnabled());
            profileSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    View parent = (View) buttonView.getParent();
                    SecondaryProfile profile = (SecondaryProfile) parent.getTag();
                    profile.setEnabled(isChecked);

                    dbAdapter.openWritable();
                    dbAdapter.updateSecondaryProfile(profile);
                    dbAdapter.close();
                }
            });

            return convertView;
        }
    }
}
