
package com.mattprecious.notisync.wizardpager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.service.NotificationService;

public class AccessibilityFragment extends SherlockFragment {

    private Button enableButton;

    public static AccessibilityFragment create() {
        return new AccessibilityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_accessibility, container, false);

        enableButton = (Button) rootView.findViewById(R.id.accessibilityButton);
        enableButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent accessibilityIntent = new Intent();
                accessibilityIntent
                        .setAction(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(accessibilityIntent);
            }
        });

        updateStatus();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateStatus();
    }

    public void updateStatus() {
        if (NotificationService.isRunning()) {
            enableButton.setEnabled(false);
            enableButton.setText(R.string.wizard_accessibility_enabled);
        } else {
            enableButton.setEnabled(true);
            enableButton.setText(R.string.wizard_accessibility_enable);
        }
    }
}
