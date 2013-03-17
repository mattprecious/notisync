
package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.fragment.AttributionsDialogFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AboutPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_preferences);

        findPreference("about_version").setSummary(SettingsActivity.getAppVersion(getActivity()));
        findPreference("about_attribution").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        DialogFragment licensesDialog = new AttributionsDialogFragment();
                        licensesDialog.show(getFragmentManager(), null);

                        return false;
                    }
                });
    }
}
