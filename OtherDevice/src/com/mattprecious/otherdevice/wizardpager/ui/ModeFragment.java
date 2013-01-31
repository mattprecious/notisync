
package com.mattprecious.otherdevice.wizardpager.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.util.Preferences;
import com.mattprecious.otherdevice.wizardpager.model.Page;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.TextView;

public class ModeFragment extends Fragment implements OnClickListener {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    private TextView primaryText;
    private TextView secondaryText;

    public static ModeFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ModeFragment fragment = new ModeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wizard_mode, container, false);

        primaryText = (TextView) rootView.findViewById(R.id.primary);
        primaryText.setOnClickListener(this);

        secondaryText = (TextView) rootView.findViewById(R.id.secondary);
        secondaryText.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onClick(View v) {
        if (v == primaryText) {
            Preferences.setMode(getActivity(), Preferences.Mode.PRIMARY);
        } else {
            Preferences.setMode(getActivity(), Preferences.Mode.SECONDARY);
        }

        mPage.notifyDataChanged();
    }

}
