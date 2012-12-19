package com.mattprecious.otherdevice.preferences;

import android.annotation.TargetApi;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.db.DbAdapter;
import com.mattprecious.otherdevice.model.SecondaryProfile;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SecondaryCustomProfileFragment extends PreferenceFragment {
    private DbAdapter dbAdapter;
    private SecondaryProfile profile;

    private EditTextPreference namePreference;
    private EditTextPreference tagPreference;
    private CheckBoxPreference enabledPreference;
    private CheckBoxPreference unconnectedOnlyPreference;
    private RingtonePreference ringtonePreference;
    private CheckBoxPreference vibratePreference;
    private CheckBoxPreference ledPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        dbAdapter = new DbAdapter(getActivity());

        if (getArguments() != null && getArguments().containsKey("profile")) {
            profile = getArguments().getParcelable("profile");
        } else {
            profile = new SecondaryProfile();
        }

        PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(
                getActivity());

        namePreference = new EditTextPreference(getActivity());
        namePreference.setPersistent(false);
        namePreference.setOnPreferenceChangeListener(preferenceListener);
        namePreference.setTitle("Name");
        namePreference.setDefaultValue(profile.getName());
        preferenceScreen.addPreference(namePreference);

        tagPreference = new EditTextPreference(getActivity());
        tagPreference.setPersistent(false);
        tagPreference.setOnPreferenceChangeListener(preferenceListener);
        tagPreference.setTitle("TAG");
        tagPreference.setSummary("A unique string to identify this profile on other devices");
        tagPreference.setDefaultValue(profile.getTag());
        preferenceScreen.addPreference(tagPreference);
        
        enabledPreference = new CheckBoxPreference(getActivity());
        enabledPreference.setPersistent(false);
        enabledPreference.setOnPreferenceChangeListener(preferenceListener);
        enabledPreference.setTitle("Enabled");
        enabledPreference.setDefaultValue(profile.isEnabled());
        preferenceScreen.addPreference(enabledPreference);
        
        unconnectedOnlyPreference = new CheckBoxPreference(getActivity());
        unconnectedOnlyPreference.setPersistent(false);
        unconnectedOnlyPreference.setOnPreferenceChangeListener(preferenceListener);
        unconnectedOnlyPreference.setTitle("Unconnected only");
        unconnectedOnlyPreference.setDefaultValue(profile.isUnconnectedOnly());
        preferenceScreen.addPreference(unconnectedOnlyPreference);
        
        ringtonePreference = new RingtonePreference(getActivity());
        ringtonePreference.setPersistent(false);
        ringtonePreference.setOnPreferenceChangeListener(preferenceListener);
        ringtonePreference.setRingtoneType(RingtoneManager.TYPE_NOTIFICATION);
        ringtonePreference.setTitle("Ringtone");
        ringtonePreference.setDefaultValue(profile.getRingtone());
        preferenceScreen.addPreference(ringtonePreference);
        
        vibratePreference = new CheckBoxPreference(getActivity());
        vibratePreference.setPersistent(false);
        vibratePreference.setOnPreferenceChangeListener(preferenceListener);
        vibratePreference.setTitle("Vibrate");
        vibratePreference.setDefaultValue(profile.isVibrate());
        preferenceScreen.addPreference(vibratePreference);
        
        ledPreference = new CheckBoxPreference(getActivity());
        ledPreference.setPersistent(false);
        ledPreference.setOnPreferenceChangeListener(preferenceListener);
        ledPreference.setTitle("Flash lights");
        ledPreference.setDefaultValue(profile.isLed());
        preferenceScreen.addPreference(ledPreference);

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
                    dbAdapter.updateSecondaryProfile(profile);
                } else {
                    dbAdapter.insertSecondaryProfile(profile);
                }
                dbAdapter.close();

                getActivity().finish();

                return true;
            case R.id.menu_delete:
                if (profile.getId() != 0) {
                    dbAdapter.openWritable();
                    dbAdapter.deleteSecondaryProfile(profile);
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
            } else if (preference == enabledPreference) {
                profile.setEnabled((Boolean) newValue);
            } else if (preference == unconnectedOnlyPreference) {
                profile.setUnconnectedOnly((Boolean) newValue);
            } else if (preference == ringtonePreference) {
                profile.setRingtone((String) newValue);
            } else if (preference == vibratePreference) {
                profile.setVibrate((Boolean) newValue);
            } else if (preference == ledPreference) {
                profile.setLed((Boolean) newValue);
            }

            return true;
        }
    };

    private void discarded() {
        Toast.makeText(getActivity(), "Changes discarded", Toast.LENGTH_SHORT).show();
    }
}
