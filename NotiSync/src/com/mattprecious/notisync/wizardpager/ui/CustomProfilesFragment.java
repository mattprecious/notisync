
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
