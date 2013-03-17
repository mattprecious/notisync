
package com.mattprecious.notisync.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.Preferences;

public class CustomHelpDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Help");
        builder.setMessage(Html.fromHtml(getString(Preferences.isPrimary(getActivity()) ? R.string.wizard_customprofiles_content_primary
                : R.string.wizard_customprofiles_content_secondary)));
        builder.setNegativeButton("Close", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
