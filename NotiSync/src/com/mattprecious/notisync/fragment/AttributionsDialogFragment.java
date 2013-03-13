
package com.mattprecious.notisync.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.TextView;

public class AttributionsDialogFragment extends DialogFragment {
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

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }
}
