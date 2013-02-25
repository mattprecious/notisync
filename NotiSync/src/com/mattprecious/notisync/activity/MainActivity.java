
package com.mattprecious.notisync.activity;

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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.fragment.AccessibilityDialogFragment;
import com.mattprecious.notisync.fragment.AccessibilityDialogFragment.AccessibilityDialogListener;
import com.mattprecious.notisync.fragment.PrimaryCustomProfileListFragment;
import com.mattprecious.notisync.fragment.SecondaryCustomProfileListFragment;
import com.mattprecious.notisync.fragment.StandardProfileListFragment;
import com.mattprecious.notisync.model.PrimaryProfile;
import com.mattprecious.notisync.model.SecondaryProfile;
import com.mattprecious.notisync.preferences.AboutPreferenceFragment;
import com.mattprecious.notisync.preferences.SettingsActivity;
import com.mattprecious.notisync.service.NotificationService;
import com.mattprecious.notisync.service.PrimaryService;
import com.mattprecious.notisync.service.SecondaryService;
import com.mattprecious.notisync.util.Constants;
import com.mattprecious.notisync.util.MyLog;
import com.mattprecious.notisync.util.Preferences;
import com.mattprecious.notisync.util.UndoBarController;
import com.mattprecious.notisync.util.UndoBarController.UndoListener;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.preference.PreferenceActivity;
import org.holoeverywhere.widget.Switch;

public class MainActivity extends Activity implements UndoListener, AccessibilityDialogListener {
    private final static String TAG = "MainActivity";

    private final String KEY_IGNORE_ACCESSIBILITY = "ignore_accessibility";

    private LocalBroadcastManager broadcastManager;

    private Switch actionBarSwitch;
    private MyPagerAdapter adapter;
    private ViewPager pager;
    private UndoBarController undoBarController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Preferences.hasMode(this)) {
            setDefaults();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        broadcastManager = LocalBroadcastManager.getInstance(this);

        broadcastManager.registerReceiver(serviceStatusReceiver, new IntentFilter(
                Constants.ACTION_SERVICE_STARTED));
        broadcastManager.registerReceiver(serviceStatusReceiver, new IntentFilter(
                Constants.ACTION_SERVICE_STOPPED));

        adapter = new MyPagerAdapter(getSupportFragmentManager());

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(1);
        pager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });

        undoBarController = new UndoBarController(findViewById(R.id.undobar), this);

        configureActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

        supportInvalidateOptionsMenu();

        if (!Preferences.getCompletedWizard(this)) {
            startActivityForResult(new Intent(this, WizardActivity.class),
                    Constants.REQUEST_CODE_WIZARD);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Crouton.clearCroutonsForActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();

        try {
            broadcastManager.unregisterReceiver(serviceStatusReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    private void configureActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        for (int i = 0; i < adapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(new TabListener() {

                                @Override
                                public void onTabUnselected(Tab tab, FragmentTransaction ft) {
                                }

                                @Override
                                public void onTabSelected(Tab tab, FragmentTransaction ft) {
                                    pager.setCurrentItem(tab.getPosition());
                                }

                                @Override
                                public void onTabReselected(Tab tab, FragmentTransaction ft) {
                                }
                            }));
        }

        actionBarSwitch = new Switch(this);
        actionBarSwitch.setSwitchTextAppearance(this, R.style.Switch_TextAppearance);
        actionBarSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService();
                } else {
                    stopService();
                }
            }
        });

        updateSwitch();

        final int padding = getResources().getDimensionPixelSize(R.dimen.action_bar_switch_padding);
        actionBarSwitch.setPadding(0, 0, padding, 0);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_VERTICAL | Gravity.RIGHT));
    }

    private void startService() {
        if (Preferences.isPrimary(this)) {
            startService(new Intent(this, PrimaryService.class));
        } else {
            startService(new Intent(this, SecondaryService.class));
        }
    }

    private void stopService() {
        stopService(new Intent(this, PrimaryService.class));
        stopService(new Intent(this, SecondaryService.class));
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
        private final int NUM_FRAGMENTS = 2;
        private final Fragment[] FRAGMENTS = new Fragment[NUM_FRAGMENTS];
        private final String[] TITLES = new String[NUM_FRAGMENTS];

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);

            TITLES[0] = getString(R.string.pager_title_standard);
            FRAGMENTS[0] = new StandardProfileListFragment();

            TITLES[1] = getString(R.string.pager_title_custom);
            initCustom();

            // TITLES[2] = getString(R.string.pager_title_third_party);
            // FRAGMENTS[2] = new ThirdPartyProfileListFragment();
        }

        private void initCustom() {
            if (Preferences.isPrimary(getApplicationContext())) {
                FRAGMENTS[1] = new PrimaryCustomProfileListFragment();
            } else {
                FRAGMENTS[1] = new SecondaryCustomProfileListFragment();
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

            MyLog.e(TAG, "invalid getItem position: " + position);
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

            MyLog.e(TAG, "invalid getPageTitle position: " + position);
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
        boolean showAccessibilityAction = Preferences.isPrimary(this)
                && !NotificationService.isRunning();

        showAccessibilityAction &= !getPreferences(Activity.MODE_PRIVATE).getBoolean(
                KEY_IGNORE_ACCESSIBILITY, false);

        menu.findItem(R.id.menu_accessibility).setVisible(showAccessibilityAction);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_accessibility:
                DialogFragment newFragment = new AccessibilityDialogFragment();
                newFragment.show(getSupportFragmentManager());

                return true;
            case R.id.menu_preferences:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_wizard:
                startActivityForResult(new Intent(this, WizardActivity.class),
                        Constants.REQUEST_CODE_WIZARD);

                return true;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, SettingsActivity.class);
                aboutIntent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                        AboutPreferenceFragment.class.getName());
                startActivity(aboutIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_WIZARD:
                if (resultCode == RESULT_OK) {
                    startService();
                } else if (!Preferences.getCompletedWizard(this)) {
                    finish();
                }

                break;
            case Constants.REQUEST_CODE_EDIT_PROFILE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Crouton.showText(this, R.string.profile_saved, Style.CONFIRM,
                                R.id.content_wrapper);
                        break;
                    case Activity.RESULT_CANCELED:
                        Crouton.showText(this, R.string.profile_discarded, Style.INFO,
                                R.id.content_wrapper);
                        break;
                    case Constants.RESULT_CODE_PROFILE_DELETED:
                        if (undoBarController != null) {
                            undoBarController.showUndoBar(true,
                                    getString(R.string.profile_deleted),
                                    data.getParcelableExtra("profile"));
                        }
                        break;
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void setDefaults() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile != null) {
            Preferences.setMode(this, Preferences.Mode.PRIMARY);
        } else {
            Preferences.setMode(this, Preferences.Mode.SECONDARY);
        }

        Preferences.populateDefaults(this);
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
            PrimaryCustomProfileListFragment fragment = (PrimaryCustomProfileListFragment) adapter
                    .getItem(1);
            fragment.onUndo(token);
        } else if (token instanceof SecondaryProfile) {
            SecondaryCustomProfileListFragment fragment = (SecondaryCustomProfileListFragment) adapter
                    .getItem(1);
            fragment.onUndo(token);
        }
    }

    @Override
    public void onAccessibilityPositive() {
        Intent accessibilityIntent = new Intent();
        accessibilityIntent
                .setAction(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(accessibilityIntent);
    }

    @Override
    public void onAccessibilityNeutral() {
    }

    @Override
    public void onAccessibilityNegative() {
        getPreferences(Activity.MODE_PRIVATE).edit().putBoolean(KEY_IGNORE_ACCESSIBILITY, true)
                .commit();
        supportInvalidateOptionsMenu();
    }
}