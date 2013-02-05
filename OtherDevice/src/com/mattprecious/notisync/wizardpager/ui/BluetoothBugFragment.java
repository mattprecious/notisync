
package com.mattprecious.notisync.wizardpager.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mattprecious.notisync.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

public class BluetoothBugFragment extends Fragment {
    public static final String EXTRA_LAYOUT_ID = "layoutId";

    public static BluetoothBugFragment create() {
        return new BluetoothBugFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_bluetooth_bug, container, false);

        ((TextView) rootView.findViewById(R.id.content))
                .setText(getString(R.string.wizard_bluetoothissue_content,
                        Build.VERSION.RELEASE));

        return rootView;
    }
}
