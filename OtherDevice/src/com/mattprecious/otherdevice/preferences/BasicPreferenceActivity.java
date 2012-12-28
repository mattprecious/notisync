package com.mattprecious.otherdevice.preferences;

import java.util.Locale;

import org.holoeverywhere.preference.EditTextPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.PreferenceActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;

import com.actionbarsherlock.view.MenuItem;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.service.SecondaryService;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.Preferences;
import com.mattprecious.otherdevice.util.Preferences.Mode;

@SuppressWarnings("deprecation")
public class BasicPreferenceActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String category = getIntent().getStringExtra("category");
        Mode mode = Preferences.Mode.valueOf(getIntent().getStringExtra("mode").toUpperCase(
                Locale.getDefault()));

        if ("textmessage".equals(category)) {
            if (mode == Mode.PRIMARY) {
                addPreferencesFromResource(R.xml.primary_text_message_preferences);
            } else {
                addPreferencesFromResource(R.xml.secondary_text_message_preferences);
            }
        } else if ("phonecall".equals(category)) {
            if (mode == Mode.PRIMARY) {
                addPreferencesFromResource(R.xml.primary_phone_call_preferences);
            } else {
                addPreferencesFromResource(R.xml.secondary_phone_call_preferences);
            }
        } else if ("gtalk".equals(category)) {
            if (mode == Mode.PRIMARY) {
                addPreferencesFromResource(R.xml.primary_gtalk_preferences);
            } else {
                addPreferencesFromResource(R.xml.secondary_gtalk_preferences);
            }
        } else if ("general".equals(category)) {
            addPreferencesFromResource(R.xml.global_general_preferences);
            if (mode == Mode.PRIMARY) {
                addPreferencesFromResource(R.xml.primary_general_preferences);

                findPreference(Preferences.KEY_GLOBAL_MODE).setOnPreferenceChangeListener(
                        new OnPreferenceChangeListener() {

                            @Override
                            public boolean onPreferenceChange(Preference preference, Object newValue) {
                                Mode newMode = Preferences.Mode.valueOf((String) newValue);
                                if (newMode == Preferences.Mode.PRIMARY) {
                                    stopService(new Intent(getApplicationContext(),
                                            SecondaryService.class));
                                    startService(new Intent(getApplicationContext(),
                                            PrimaryService.class));
                                } else {
                                    stopService(new Intent(getApplicationContext(),
                                            PrimaryService.class));
                                    startService(new Intent(getApplicationContext(),
                                            SecondaryService.class));
                                }
                                return true;
                            }
                        });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
