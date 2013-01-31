
package com.mattprecious.otherdevice.wizardpager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.service.NotificationService;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Button;

public class AccessibilityFragment extends Fragment {

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
            enableButton.setText("Enabled!");
        } else {
            enableButton.setEnabled(true);
            enableButton.setText("Enable now");
        }
    }
}
