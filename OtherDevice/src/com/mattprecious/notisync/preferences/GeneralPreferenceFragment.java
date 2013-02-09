
package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.service.PrimaryService;
import com.mattprecious.notisync.service.SecondaryService;
import com.mattprecious.notisync.util.Preferences;
import com.mattprecious.notisync.util.Preferences.Mode;

import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceChangeListener;
import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                        if (((PreferenceActivity) getActivity()).onIsMultiPane()) {
                            Intent intent = new Intent(getActivity(), SettingsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                                    GeneralPreferenceFragment.class.getName());

                            getActivity().finish();
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                        }

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
    }
}
