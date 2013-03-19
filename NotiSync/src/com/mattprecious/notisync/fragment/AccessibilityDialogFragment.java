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

package com.mattprecious.notisync.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;

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
