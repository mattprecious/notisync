package com.mattprecious.otherdevice.activity;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.Switch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.fragment.PrimaryCustomProfilesFragment;
import com.mattprecious.otherdevice.fragment.SecondaryCustomProfilesFragment;
import com.mattprecious.otherdevice.fragment.StandardProfilesFragment;
import com.mattprecious.otherdevice.fragment.ThirdPartyProfilesFragment;
import com.mattprecious.otherdevice.model.PrimaryProfile;
import com.mattprecious.otherdevice.model.SecondaryProfile;
import com.mattprecious.otherdevice.preferences.PrimaryPreferenceActivity;
import com.mattprecious.otherdevice.preferences.SecondaryPreferenceActivity;
import com.mattprecious.otherdevice.service.NotificationService;
import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.service.SecondaryService;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.Preferences;
import com.mattprecious.otherdevice.util.UndoBarController;
import com.viewpagerindicator.TitlePageIndicator;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class OtherDevice extends Activity implements UndoBarController.UndoListener {
    private final static String TAG = "OtherDevice";

    private Switch actionBarSwitch;
    private MyPagerAdapter adapter;
    private ViewPager pager;
    private TitlePageIndicator indicator;
    private UndoBarController undoBarController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Preferences.hasMode(this)) {
            setDefaults();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.pager);
        configureActionBar();

        registerReceiver(serviceStatusReceiver, new IntentFilter(
                Constants.ACTION_PRIMARY_SERVICE_STARTED));
        registerReceiver(serviceStatusReceiver, new IntentFilter(
                Constants.ACTION_PRIMARY_SERVICE_STOPPED));
        registerReceiver(serviceStatusReceiver, new IntentFilter(
                Constants.ACTION_SECONDARY_SERVICE_STARTED));
        registerReceiver(serviceStatusReceiver, new IntentFilter(
                Constants.ACTION_SECONDARY_SERVICE_STOPPED));

        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);

        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        undoBarController = new UndoBarController(findViewById(R.id.undobar), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();

        try {
            unregisterReceiver(serviceStatusReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    private void configureActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBarSwitch = new Switch(this);
        actionBarSwitch.setSwitchTextAppearance(this, R.style.Switch_TextAppearance);
        actionBarSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Preferences.isPrimary(getApplicationContext())) {
                    if (isChecked) {
                        startService(new Intent(getApplicationContext(), PrimaryService.class));
                    } else {
                        stopService(new Intent(getApplicationContext(), PrimaryService.class));
                    }
                } else {
                    if (isChecked) {
                        startService(new Intent(getApplicationContext(), SecondaryService.class));
                    } else {
                        stopService(new Intent(getApplicationContext(), SecondaryService.class));
                    }
                }
            }
        });

        updateSwitch();

        final int padding = getResources().getDimensionPixelSize(R.dimen.action_bar_switch_padding);
        actionBarSwitch.setPadding(0, 0, padding, 0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.END));
    }

    private void updateSwitch() {
        if (Preferences.isPrimary(this)) {
            if (PrimaryService.isRunning()) {
                actionBarSwitch.setChecked(true);
            } else {
                actionBarSwitch.setChecked(false);
            }
        } else {
            if (SecondaryService.isRunning()) {
                actionBarSwitch.setChecked(true);
            } else {
                actionBarSwitch.setChecked(false);
            }
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private final int NUM_FRAGMENTS = 3;
        private Fragment[] FRAGMENTS = new Fragment[NUM_FRAGMENTS];
        private String[] TITLES = new String[NUM_FRAGMENTS];

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            TITLES[0] = "Standard";
            FRAGMENTS[0] = new StandardProfilesFragment();

            TITLES[1] = "Custom";
            initCustom();

            TITLES[2] = "Third Party";
            FRAGMENTS[2] = new ThirdPartyProfilesFragment();
        }

        private void initCustom() {
            if (Preferences.isPrimary(getApplicationContext())) {
                FRAGMENTS[1] = new PrimaryCustomProfilesFragment(undoBarController);
            } else {
                FRAGMENTS[1] = new SecondaryCustomProfilesFragment(undoBarController);
            }
        }

        @Override
        public void notifyDataSetChanged() {
            initCustom();
            super.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= 0 && position < NUM_FRAGMENTS) {
                return FRAGMENTS[position];
            }

            Log.e(TAG, "invalid getItem position: " + position);
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0 && position < NUM_FRAGMENTS) {
                return TITLES[position];
            }

            Log.e(TAG, "invalid getPageTitle position: " + position);
            return null;
        }

        @Override
        public int getCount() {
            return NUM_FRAGMENTS;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean showAccessibilityAction = Preferences.isPrimary(this) && !NotificationService.isRunning();
        menu.findItem(R.id.menu_accessibility).setVisible(showAccessibilityAction);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_accessibility:
                Intent accessibilityIntent = new Intent();
                accessibilityIntent
                        .setAction(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(accessibilityIntent);
                return true;
            case R.id.menu_preferences:
                Intent btIntent;
                if (Preferences.isPrimary(this)) {
                    btIntent = new Intent(this, PrimaryPreferenceActivity.class);
                } else {
                    btIntent = new Intent(this, SecondaryPreferenceActivity.class);
                }

                startActivity(btIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDefaults() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile != null) {
            Preferences.setMode(this, Preferences.Mode.PRIMARY);
        } else {
            Preferences.setMode(this, Preferences.Mode.SECONDARY);
        }

        Preferences.populate(this);
    }

    private BroadcastReceiver serviceStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateSwitch();
        }
    };

    @Override
    public void onUndo(Parcelable token) {
        if (token instanceof PrimaryProfile) {
            PrimaryCustomProfilesFragment fragment = (PrimaryCustomProfilesFragment) adapter
                    .getItem(1);
            fragment.onUndo(token);
        } else if (token instanceof SecondaryProfile) {
            SecondaryCustomProfilesFragment fragment = (SecondaryCustomProfilesFragment) adapter
                    .getItem(1);
            fragment.onUndo(token);
        }
    }

}
