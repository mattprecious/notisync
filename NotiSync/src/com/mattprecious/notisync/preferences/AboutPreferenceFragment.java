/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.fragment.AttributionsDialogFragment;
import com.mattprecious.notisync.util.Helpers;

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
        findPreference("about_feedback").setOnPreferenceClickListener(
                new OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Helpers.openSupportPage(getActivity());
                        return false;
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().setContext(getActivity());
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }
}
