
package com.mattprecious.notisync.wizardpager.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.Preferences;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.app.TimePickerDialog;
import org.holoeverywhere.app.TimePickerDialog.OnTimeSetListener;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.TimePicker;

public class BluetoothBugFragment extends Fragment {

    private Button scheduleButton;

    public static BluetoothBugFragment create() {
        return new BluetoothBugFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_bluetooth_bug, container, false);

        ((TextView) rootView.findViewById(R.id.content))
                .setText(getString(R.string.wizard_bluetoothissue_content,
                        Build.VERSION.RELEASE));

        scheduleButton = (Button) rootView.findViewById(R.id.scheduleFixButton);
        scheduleButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String[] pieces = Preferences.getBluetoothFixTime(getActivity()).split(":");

                int hour = Integer.parseInt(pieces[0]);
                int minute = Integer.parseInt(pieces[1]);

                new TimePickerDialog(getActivity(), timeListener, hour, minute, false).show();
            }
        });

        return rootView;
    }

    private OnTimeSetListener timeListener = new OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Preferences.setBluetoothFixEnabled(getActivity(), true);
            Preferences.setBluetoothFixTime(getActivity(),
                    String.format("%d:%02d", hourOfDay, minute));
        }
    };
}
