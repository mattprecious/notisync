
package com.mattprecious.notisync.profile;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.Constants;
import com.mattprecious.notisync.util.Preferences;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

public class TextMessageFragment extends StandardProfileFragment {

    private TextView ringtoneSelector;
    private CheckBox vibrateCheckBox;
    private CheckBox lightsCheckBox;

    private Uri ringtoneUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_text_message_secondary, container, false);

        ringtoneUri = getRingtoneUri(Preferences.getSecondaryTextMessageRingtone(getActivity()));

        ringtoneSelector = (TextView) rootView.findViewById(R.id.ringtoneSelector);
        ringtoneSelector.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);

                startActivityForResult(intent, Constants.REQUEST_CODE_RINGTONE_PICKER);

            }
        });

        vibrateCheckBox = (CheckBox) rootView.findViewById(R.id.vibrateCheckBox);
        vibrateCheckBox.setChecked(Preferences.getSecondaryTextMessageVibrate(getActivity()));

        lightsCheckBox = (CheckBox) rootView.findViewById(R.id.lightsCheckBox);
        lightsCheckBox.setChecked(Preferences.getSecondaryTextMessageLights(getActivity()));

        updateRingtoneSelector();

        return rootView;
    }

    @Override
    public boolean onSave() {
        Preferences.setSecondaryTextMessageRingtone(getActivity(), uriToString(ringtoneUri));
        Preferences.setSecondaryTextMessageVibrate(getActivity(), vibrateCheckBox.isChecked());
        Preferences.setSecondaryTextMessageLights(getActivity(), lightsCheckBox.isChecked());
        return true;
    }

    private void updateRingtoneSelector() {
        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);

        ringtoneSelector.setText(ringtone.getTitle(getActivity()));
    }

    private Uri getRingtoneUri(String ringtone) {
        return (ringtone == null) ? null : Uri.parse(ringtone);
    }

    private String uriToString(Uri uri) {
        return (uri == null) ? null : uri.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_RINGTONE_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    ringtoneUri = uri;

                    updateRingtoneSelector();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

}
