
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
                        .replace(android.R.id.content, new TestMessageFragment())
                        .addToBackStack(null).commit();
            }
        });

        return rootView;
    }
}
