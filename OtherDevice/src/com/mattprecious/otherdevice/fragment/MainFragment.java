package com.mattprecious.otherdevice.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.preferences.PrimaryPreferenceActivity;
import com.mattprecious.otherdevice.preferences.SecondaryPreferenceActivity;
import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.service.SecondaryService;
import com.mattprecious.otherdevice.util.Preferences;

public class MainFragment extends SherlockFragment {
    private static final String TAG = "MainFragment";
    
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);

        setHasOptionsMenu(true);
        
        if (!Preferences.hasType(getActivity())) {
            setDefaults();
        }

        button = (Button) view.findViewById(R.id.button);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                togglePrimarySecondary();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateButtons();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                String type = Preferences.getType(getActivity());

                Intent intent;
                if (Preferences.TYPE_PRIMARY.equals(type)) {
                    intent = new Intent(getActivity(), PrimaryPreferenceActivity.class);
                } else {
                    intent = new Intent(getActivity(), SecondaryPreferenceActivity.class);
                }

                getActivity().startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void togglePrimarySecondary() {
        String type = Preferences.getType(getActivity());
        if (Preferences.TYPE_PRIMARY.equals(type)) {
            Preferences.setType(getActivity(), Preferences.TYPE_SECONDARY);
            getActivity().stopService(new Intent(getActivity(), PrimaryService.class));
            getActivity().startService(new Intent(getActivity(), SecondaryService.class));
        } else {
            Preferences.setType(getActivity(), Preferences.TYPE_PRIMARY);
            getActivity().stopService(new Intent(getActivity(), SecondaryService.class));
            getActivity().startService(new Intent(getActivity(), PrimaryService.class));
        }

        updateButtons();
    }

    private void updateButtons() {
        String type = Preferences.getType(getActivity());
        if (Preferences.TYPE_PRIMARY.equals(type)) {
            button.setText("Primary");
        } else {
            button.setText("Secondary");
        }
    }
    
    private void setDefaults() {
        ConnectivityManager connec = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile != null) {
            Preferences.setType(getActivity(), Preferences.TYPE_PRIMARY);
        } else {
            Preferences.setType(getActivity(), Preferences.TYPE_SECONDARY);
        }
    }
}
