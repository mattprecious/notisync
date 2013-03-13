
package com.mattprecious.notisync.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.mattprecious.notisync.R;

public class AttributionsDialogFragment extends SherlockDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.about_attributions, null);

        TextView attributionsView = (TextView) rootView.findViewById(R.id.attributions);
        attributionsView.setText(Html.fromHtml(getString(R.string.attributions)));
        attributionsView.setMovementMethod(new LinkMovementMethod());

        builder.setTitle("Attributions");
        builder.setView(rootView);
        builder.setPositiveButton("Close", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
