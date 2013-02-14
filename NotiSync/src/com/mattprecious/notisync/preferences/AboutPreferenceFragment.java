
package com.mattprecious.notisync.preferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.fragment.AttributionsDialogFragment;

import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.preference.Preference;
import org.holoeverywhere.preference.Preference.OnPreferenceClickListener;
import org.holoeverywhere.preference.PreferenceFragment;

public class AboutPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_preferences);

        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(),
                    0);

            findPreference("about_version").setSummary(packageInfo.versionName);
        } catch (NameNotFoundException e) {
        }

        findPreference("about_attribution").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        DialogFragment licensesDialog = new AttributionsDialogFragment();

                        licensesDialog.show(ft);

                        return false;
                    }
                });
    }
}
