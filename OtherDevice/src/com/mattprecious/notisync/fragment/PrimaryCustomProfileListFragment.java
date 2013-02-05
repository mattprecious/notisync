
package com.mattprecious.notisync.fragment;

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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.common.collect.Lists;
import com.mattprecious.notisync.activity.PrimaryCustomProfileActivity;
import com.mattprecious.notisync.db.DbAdapter;
import com.mattprecious.notisync.model.PrimaryProfile;
import com.mattprecious.notisync.util.UndoBarController;
import com.mattprecious.notisync.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PrimaryCustomProfileListFragment extends Fragment implements
        UndoBarController.UndoListener {
    private static final String TAG = "PrimaryCustomProfileListFragment";

    private static final int REQUEST_CODE_EDIT_PROFILE = 1;

    public static final int RESULT_CODE_PROFILE_DELETED = 2;

    private DbAdapter dbAdapter;
    private ListView listView;
    private CustomProfileAdapter listAdapter;
    private UndoBarController mUndoBarController;

    public PrimaryCustomProfileListFragment() {

    }

    public PrimaryCustomProfileListFragment(UndoBarController undoBarController) {
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
                Intent intent = new Intent(getActivity(), PrimaryCustomProfileActivity.class);

                if (position < listView.getCount() - listView.getFooterViewsCount()) {
                    PrimaryProfile profile = (PrimaryProfile) parent.getItemAtPosition(position);
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
        List<PrimaryProfile> profiles = dbAdapter.getPrimaryProfiles();
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
                        Log.d(TAG, "crouton shown");
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
        PrimaryProfile profile = (PrimaryProfile) token;

        dbAdapter.openWritable();
        dbAdapter.insertPrimaryProfile(profile);
        dbAdapter.close();

        reloadProfiles();
    }

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
                    PrimaryProfile profile = (PrimaryProfile) parent.getTag();
                    profile.setEnabled(isChecked);

                    dbAdapter.openWritable();
                    dbAdapter.updatePrimaryProfile(profile);
                    dbAdapter.close();
                }
            });

            return convertView;
        }
    }
}
