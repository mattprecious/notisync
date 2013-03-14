
package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.Preferences;

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
                        if (((PreferenceActivity) getActivity()).onIsMultiPane()) {
//                            Intent intent = new Intent(getActivity(), SettingsActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
//                                    GeneralPreferenceFragment.class.getName());

                            getActivity().finish();
//                            startActivity(intent);
//                            getActivity().overridePendingTransition(0, 0);
                        }

                        return true;
                    }
                });
    }
}
