package com.mattprecious.otherdevice.preferences;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.db.DbAdapter;
import com.mattprecious.otherdevice.model.PrimaryProfile;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PrimaryCustomProfileFragment extends PreferenceFragment {
    private DbAdapter dbAdapter;
    private PrimaryProfile profile;

    private EditTextPreference namePreference;
    private EditTextPreference tagPreference;
    private EditTextPreference packagePreference;
    private CheckBoxPreference enabledPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        dbAdapter = new DbAdapter(getActivity());

        if (getArguments() != null && getArguments().containsKey("profile")) {
            profile = getArguments().getParcelable("profile");
        } else {
            profile = new PrimaryProfile();
        }

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(
                getActivity());

        namePreference = new EditTextPreference(getActivity());
        namePreference.setOnPreferenceChangeListener(preferenceListener);
        namePreference.setTitle("Name");
        namePreference.setDefaultValue(profile.getName());
        preferenceScreen.addPreference(namePreference);

        tagPreference = new EditTextPreference(getActivity());
        tagPreference.setOnPreferenceChangeListener(preferenceListener);
        tagPreference.setTitle("TAG");
        tagPreference.setSummary("A unique string to identify this profile on other devices");
        tagPreference.setDefaultValue(profile.getTag());
        preferenceScreen.addPreference(tagPreference);
        
        packagePreference = new EditTextPreference(getActivity());
        packagePreference.setOnPreferenceChangeListener(preferenceListener);
        packagePreference.setTitle("Package");
        packagePreference.setDefaultValue(profile.getPackageName());
        preferenceScreen.addPreference(packagePreference);

        enabledPreference = new CheckBoxPreference(getActivity());
        enabledPreference.setOnPreferenceChangeListener(preferenceListener);
        enabledPreference.setTitle("Enabled");
        enabledPreference.setDefaultValue(profile.isEnabled());
        preferenceScreen.addPreference(enabledPreference);

        setPreferenceScreen(preferenceScreen);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.custom_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                dbAdapter.openWritable();
                if (profile.getId() != 0) {
                    dbAdapter.updatePrimaryProfile(profile);
                } else {
                    dbAdapter.insertPrimaryProfile(profile);
                }
                dbAdapter.close();

                getActivity().finish();

                return true;
            case R.id.menu_delete:
                if (profile.getId() != 0) {
                    dbAdapter.openWritable();
                    dbAdapter.deletePrimaryProfile(profile);
                    dbAdapter.close();
                } else {
                    discarded();
                }

                getActivity().finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private OnPreferenceChangeListener preferenceListener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == namePreference) {
                profile.setName((String) newValue);
            } else if (preference == tagPreference) {
                profile.setTag((String) newValue);
            } else if (preference == packagePreference) {
                profile.setPackageName((String) newValue);
            } else if (preference == enabledPreference) {
                profile.setEnabled((Boolean) newValue);
            }

            return true;
        }
    };

    private void discarded() {
        Toast.makeText(getActivity(), "Changes discarded", Toast.LENGTH_SHORT).show();
    }
}
