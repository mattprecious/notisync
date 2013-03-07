
package com.mattprecious.notisync.devtools;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.message.GtalkMessage;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.EditText;

public class GtalkMessageFragment extends Fragment {
    private EditText senderText;
    private EditText messageText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dev_tools_gtalk_message, container, false);

        getActivity().setTitle("Gtalk message");

        senderText = (EditText) rootView.findViewById(R.id.sender);
        messageText = (EditText) rootView.findViewById(R.id.message);

        rootView.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack();
            }
        });

        rootView.findViewById(R.id.send).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                send();
            }
        });

        return rootView;
    }

    private void send() {
        GtalkMessage.Builder builder = new GtalkMessage.Builder();
        builder.sender(senderText.getText().toString());
        builder.message(messageText.getText().toString());

        GtalkMessage message = builder.build();
        ((DevToolsActivity) getActivity()).sendMessage(message);
    }
}
