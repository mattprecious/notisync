package com.mattprecious.otherdevice.preferences;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.service.SecondaryService;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.Preferences;

@SuppressWarnings("deprecation")
public class BasicPreferenceActivity extends SherlockPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String category = getIntent().getStringExtra("category");
        String type = getIntent().getStringExtra("type");

        if ("textmessage".equals(category)) {
            if ("primary".equals(type)) {
                addPreferencesFromResource(R.xml.primary_text_message_preferences);
            } else {
                addPreferencesFromResource(R.xml.secondary_text_message_preferences);
            }
        } else if ("phonecall".equals(category)) {
            if ("primary".equals(type)) {
                addPreferencesFromResource(R.xml.primary_phone_call_preferences);
            } else {
                addPreferencesFromResource(R.xml.secondary_phone_call_preferences);
            }
        } else if ("gtalk".equals(category)) {
            if ("primary".equals(type)) {
                addPreferencesFromResource(R.xml.primary_gtalk_preferences);
            } else {
                addPreferencesFromResource(R.xml.secondary_gtalk_preferences);
            }
        } else if ("general".equals(category)) {
            addPreferencesFromResource(R.xml.global_general_preferences);
            if ("primary".equals(type)) {
                addPreferencesFromResource(R.xml.primary_general_preferences);

                ((EditTextPreference) findPreference(Preferences.KEY_PRIMARY_RECONNECT_DELAY))
                        .getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                findPreference(Preferences.KEY_PRIMARY_RECONNECT_DELAY)
                        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                            @Override
                            public boolean onPreferenceChange(Preference preference, Object newValue) {
                                sendBroadcast(new Intent(Constants.ACTION_UPDATE_TIMER));
                                return true;
                            }
                        });
            } else {
                addPreferencesFromResource(R.xml.secondary_general_preferences);

                ((EditTextPreference) findPreference(Preferences.KEY_SECONDARY_RECONNECT_DELAY))
                        .getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                findPreference(Preferences.KEY_SECONDARY_RECONNECT_DELAY)
                        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                            @Override
                            public boolean onPreferenceChange(Preference preference, Object newValue) {
                                sendBroadcast(new Intent(Constants.ACTION_UPDATE_TIMER));
                                return true;
                            }
                        });
            }
        }
    }
}
