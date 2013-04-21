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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralPreferenceFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.global_general_preferences);

        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().setContext(getActivity());
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // yes, there is already a listener in SettingsActivity, but may as well
        // put this here since it's only applicable in a fragment environment
        // plus, don't need to hack up the static listener class to take a
        // reference to the activity just to do this
        if (((PreferenceActivity) getActivity()).onIsMultiPane()) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                    GeneralPreferenceFragment.class.getName());

            getActivity().finish();
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        }
    }
}
