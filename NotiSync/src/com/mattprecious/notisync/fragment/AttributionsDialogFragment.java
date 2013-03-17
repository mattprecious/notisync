
package com.mattprecious.notisync.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;

import com.mattprecious.notisync.preferences.SettingsActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AttributionsDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return SettingsActivity.buildAttributionsDialog(getActivity());
    }
}
