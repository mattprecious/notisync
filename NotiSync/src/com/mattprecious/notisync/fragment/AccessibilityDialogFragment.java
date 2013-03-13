
package com.mattprecious.notisync.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

public class AccessibilityDialogFragment extends DialogFragment {

    public interface AccessibilityDialogListener {
        public void onAccessibilityPositive();

        public void onAccessibilityNeutral();

        public void onAccessibilityNegative();
    }

    private AccessibilityDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (AccessibilityDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AccessibilityDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.accessibility_dialog_title);
        builder.setMessage(R.string.accessibility_dialog_message);

        builder.setPositiveButton(R.string.accessibility_dialog_positive, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onAccessibilityPositive();
            }
        });
        builder.setNeutralButton(R.string.accessibility_dialog_neutral, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onAccessibilityNeutral();
            }
        });

        builder.setNegativeButton(R.string.accessibility_dialog_negative, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onAccessibilityNegative();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }
}
