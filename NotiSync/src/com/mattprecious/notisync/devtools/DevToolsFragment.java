
package com.mattprecious.notisync.devtools;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.mattprecious.notisync.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

public class DevToolsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dev_tools, container, false);

        getActivity().setTitle("Dev tools");

        View textMessage = rootView.findViewById(R.id.text_message);
        textMessage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new TextMessageFragment())
                        .addToBackStack(null).commit();
            }
        });

        View phoneCall = rootView.findViewById(R.id.phone_call);
        phoneCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new PhoneCallFragment())
                        .addToBackStack(null).commit();
            }
        });

        View gtalkMessage = rootView.findViewById(R.id.gtalk_message);
        gtalkMessage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new GtalkMessageFragment())
                        .addToBackStack(null).commit();
            }
        });

        return rootView;
    }
}
