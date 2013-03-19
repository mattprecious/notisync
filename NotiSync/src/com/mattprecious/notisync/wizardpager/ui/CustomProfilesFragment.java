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

package com.mattprecious.notisync.wizardpager.ui;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.Preferences;

/**
 * TextView with id android.R.id.content is required
 */
public class CustomProfilesFragment extends SherlockFragment {

    public static CustomProfilesFragment create() {
        CustomProfilesFragment fragment = new CustomProfilesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_custom_profiles, container, false);

        TextView textView = (TextView) rootView.findViewById(android.R.id.content);

        if (Preferences.isPrimary(getActivity())) {
            textView.setText(Html.fromHtml(getResources().getString(
                    R.string.wizard_customprofiles_content_primary)));
        } else {
            textView.setText(Html.fromHtml(getResources().getString(
                    R.string.wizard_customprofiles_content_secondary)));
        }

        return rootView;
    }
}
