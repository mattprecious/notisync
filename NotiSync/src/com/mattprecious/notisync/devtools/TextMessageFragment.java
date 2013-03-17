
package com.mattprecious.notisync.devtools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.message.TextMessage;

public class TextMessageFragment extends SherlockFragment {
    private EditText numberText;
    private EditText nameText;
    private EditText messageText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dev_tools_text_message, container, false);

        getActivity().setTitle("Text message");

        numberText = (EditText) rootView.findViewById(R.id.number);
        nameText = (EditText) rootView.findViewById(R.id.name);
        messageText = (EditText) rootView.findViewById(R.id.message);

        rootView.findViewById(R.id.back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
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

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }

    private void send() {
        TextMessage.Builder builder = new TextMessage.Builder();
        builder.number(numberText.getText().toString());
        builder.name(nameText.getText().toString());
        builder.message(messageText.getText().toString());

        TextMessage message = builder.build();
        ((DevToolsActivity) getActivity()).sendMessage(message);
    }
}
