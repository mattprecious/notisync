
package com.mattprecious.otherdevice.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.service.SecondaryService;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.Preferences;
import com.mattprecious.otherdevice.util.Preferences.Mode;

import org.holoeverywhere.preference.EditTextPreference;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BasicPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String category = getArguments().getString("category");
        Mode mode = Preferences.getMode(getActivity());

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

            findPreference(Preferences.KEY_GLOBAL_MODE).setOnPreferenceChangeListener(
                    new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            Mode newMode = Preferences.Mode.valueOf((String) newValue);
                            if (newMode == Preferences.Mode.PRIMARY) {
                                getActivity().stopService(
                                        new Intent(getActivity(), SecondaryService.class));
                                getActivity().startService(
                                        new Intent(getActivity(), PrimaryService.class));
                            } else {
                                getActivity().stopService(
                                        new Intent(getActivity(), PrimaryService.class));
                                getActivity().startService(
                                        new Intent(getActivity(), SecondaryService.class));
                            }

                            // TODO: Restart activity straight to this fragment
                            Intent intent = new Intent(getActivity(), SettingsActivity.class);
                            getActivity().finish();
                            startActivity(intent);

                            return true;
                        }
                    });

            findPreference(Preferences.KEY_GLOBAL_ANALYTICS).setOnPreferenceChangeListener(
                    new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            boolean analyticsEnabled = ((Boolean) newValue).booleanValue();
                            GoogleAnalytics.getInstance(getActivity()).setAppOptOut(
                                    !analyticsEnabled);
                            return true;
                        }

                    });

            if (mode == Mode.PRIMARY) {
                addPreferencesFromResource(R.xml.primary_general_preferences);

                ((EditTextPreference) findPreference(Preferences.KEY_PRIMARY_RECONNECT_DELAY))
                        .getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
                findPreference(Preferences.KEY_PRIMARY_RECONNECT_DELAY)
                        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                            @Override
                            public boolean onPreferenceChange(Preference preference, Object newValue) {
                                getActivity().sendBroadcast(
                                        new Intent(Constants.ACTION_UPDATE_TIMER));
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
                                getActivity().sendBroadcast(
                                        new Intent(Constants.ACTION_UPDATE_TIMER));
                                return true;
                            }
                        });
            }
        }
    }
}
