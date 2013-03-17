
package com.mattprecious.notisync.preferences;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.service.PrimaryService;
import com.mattprecious.notisync.service.SecondaryService;
import com.mattprecious.notisync.util.AlarmHelper;
import com.mattprecious.notisync.util.Helpers;
import com.mattprecious.notisync.util.Preferences;
import com.mattprecious.notisync.util.Preferences.Mode;

import java.util.List;

public class SettingsActivity extends SherlockPreferenceActivity {
    public static final String PREFS_DEVICES =
            "com.mattprecious.notisync.preferences.PREFS_DEVICES";
    public static final String PREFS_GENERAL =
            "com.mattprecious.notisync.preferences.PREFS_GENERAL";
    public static final String PREFS_BLUETOOTH_FIX =
            "com.mattprecious.notisync.preferences.PREFS_BLUETOOTH_FIX";
    public static final String PREFS_ABOUT =
            "com.mattprecious.notisync.preferences.PREFS_ABOUT";

    private PreferenceChangeListener changeListener;
    private Mode originalMode;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeListener = new PreferenceChangeListener(this);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(changeListener);

        originalMode = Preferences.getMode(this);

        String action = getIntent().getAction();
        if (PREFS_GENERAL.equals(action)) {
            addPreferencesFromResource(R.xml.global_general_preferences);
        } else if (PREFS_BLUETOOTH_FIX.equals(action)) {
            addPreferencesFromResource(R.xml.global_bluetoothfix_preferences);
        } else if (PREFS_ABOUT.equals(action)) {
            addPreferencesFromResource(R.xml.about_preferences);
            findPreference("about_version").setSummary(getAppVersion(this));
            findPreference("about_attribution").setOnPreferenceClickListener(
                    new OnPreferenceClickListener() {

                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            buildAttributionsDialog(SettingsActivity.this).show();
                            return true;
                        }
                    });
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preference_headers_legacy);

            if (!showDevicesHeader()) {
                getPreferenceScreen().removePreference(findPreference("header_devices"));
            }

            if (!showBluetoothFixHeader()) {
                getPreferenceScreen().removePreference(findPreference("header_bluetooth_fix"));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Preferences.getMode(this) != originalMode) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(changeListener);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
        updateHeaderList(target);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void updateHeaderList(List<Header> target) {
        int i = 0;
        while (i < target.size()) {
            Header header = target.get(i);
            int id = (int) header.id;

            if (id == R.id.device_preferences) {
                if (!showDevicesHeader()) {
                    target.remove(header);
                }
            } else if (id == R.id.bluetoothfix_preferences) {
                if (!showBluetoothFixHeader()) {
                    target.remove(header);
                }
            }

            if (i < target.size() && target.get(i) == header) {
                i++;
            }
        }
    }

    private boolean showDevicesHeader() {
        return Preferences.isPrimary(this) && BluetoothAdapter.getDefaultAdapter() != null;
    }

    private boolean showBluetoothFixHeader() {
        return Helpers.hasBluetoothIssue() || Preferences.getBluetoothFixEnabled(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String getAppVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);

            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
        }

        return null;
    }

    public static Dialog buildAttributionsDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.about_attributions, null);

        // for some reason when you replace the view on a legacy dialog it wipes
        // the background colour...
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            rootView.setBackgroundColor(context.getResources().getColor(
                    android.R.color.background_light));
        }

        TextView attributionsView = (TextView) rootView.findViewById(R.id.attributions);
        attributionsView.setText(Html.fromHtml(context.getString(R.string.attributions)));
        attributionsView.setMovementMethod(new LinkMovementMethod());

        builder.setTitle("Attributions");
        builder.setView(rootView);
        builder.setPositiveButton("Close", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public static class PreferenceChangeListener implements OnSharedPreferenceChangeListener {
        private final Context context;

        public PreferenceChangeListener(Context context) {
            this.context = context;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (Preferences.KEY_GLOBAL_BLUETOOTH_FIX_ENABLED.equals(key)) {
                if (Preferences.getBluetoothFixEnabled(context)) {
                    AlarmHelper.scheduleBluetoothFixAlarm(context);
                } else {
                    AlarmHelper.cancelBluetoothFixAlarm(context);
                }
            } else if (Preferences.KEY_GLOBAL_BLUETOOTH_FIX_TIME.equals(key)) {
                AlarmHelper.scheduleBluetoothFixAlarm(context);
            } else if (Preferences.KEY_GLOBAL_ANALYTICS.equals(key)) {
                GoogleAnalytics.getInstance(context).setAppOptOut(
                        !Preferences.getAnalytics(context));
            } else if (Preferences.KEY_GLOBAL_MODE.equals(key)) {
                if (Preferences.isPrimary(context)) {
                    context.stopService(new Intent(context, SecondaryService.class));
                    context.startService(new Intent(context, PrimaryService.class));
                } else {
                    context.stopService(new Intent(context, PrimaryService.class));
                    context.startService(new Intent(context, SecondaryService.class));
                }
            }
        }
    }

}
