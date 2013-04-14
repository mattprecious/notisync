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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;

/**
 * Taken and modified from:
 * http://developer.samsung.com/forum/thread/samsung-devices-and-accessibility-
 * services/77/204387#post30
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SamsungTtsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View rootView = inflater.inflate(R.layout.samsung_tts, null);

        // for some reason when you replace the view on a legacy dialog it wipes
        // the background colour...
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            rootView.setBackgroundColor(getResources().getColor(
                    android.R.color.background_light));
        }

        rootView.findViewById(R.id.disable_talkback).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                launchAppSettings("com.google.android.marvin.talkback");
            }
        });

        rootView.findViewById(R.id.disable_google_tts).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                launchAppSettings("com.google.android.tts");
            }
        });

        rootView.findViewById(R.id.disable_samsung_tts).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                launchAppSettings("com.samsung.SMT");
            }
        });

        builder.setTitle(R.string.samsung_tts_title);
        builder.setView(rootView);

        builder.setNegativeButton(R.string.samsung_tts_negative, new Dialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().setContext(getActivity());
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    private void launchAppSettings(String app) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", app, null));
        startActivity(intent);
    }

}
