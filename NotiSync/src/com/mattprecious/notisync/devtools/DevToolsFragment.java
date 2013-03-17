
package com.mattprecious.notisync.devtools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;

public class DevToolsFragment extends SherlockFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dev_tools, container, false);

        getActivity().setTitle("Dev tools");

        View textMessage = rootView.findViewById(R.id.text_message);
        textMessage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new TextMessageFragment())
                        .addToBackStack(null).commit();
            }
        });

        View phoneCall = rootView.findViewById(R.id.phone_call);
        phoneCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new PhoneCallFragment())
                        .addToBackStack(null).commit();
            }
        });

        View gtalkMessage = rootView.findViewById(R.id.gtalk_message);
        gtalkMessage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new GtalkMessageFragment())
                        .addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }
}
