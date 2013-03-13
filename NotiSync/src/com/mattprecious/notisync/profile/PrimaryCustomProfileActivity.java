
package com.mattprecious.notisync.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.activity.MainActivity;
import com.mattprecious.notisync.db.DbAdapter;
import com.mattprecious.notisync.fragment.PackagePickerFragment;
import com.mattprecious.notisync.message.BaseMessage;
import com.mattprecious.notisync.message.TagPushMessage;
import com.mattprecious.notisync.model.PrimaryProfile;
import com.mattprecious.notisync.service.PrimaryService;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;

import java.util.Locale;

public class PrimaryCustomProfileActivity extends Activity implements
        PackagePickerFragment.OnPackageSelectedListener {
    private final int ERROR_FLAG_NAME = 1 << 0;
    private final int ERROR_FLAG_TAG = 1 << 1;
    private final int ERROR_FLAG_PACKAGE = 1 << 2;

    private static final String PREFERENCES_KEY_PUSH_PROFILE = "push_profile";

    private DbAdapter dbAdapter;
    private LocalBroadcastManager broadcastManager;
    private PrimaryProfile profile;

    private int errorFlags = 0;

    private EditText nameField;
    private EditText tagField;
    private EditText packageField;
    private ImageButton packageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_custom_primary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DbAdapter(this);
        broadcastManager = LocalBroadcastManager.getInstance(this);

        if (getIntent().hasExtra("profile")) {
            profile = getIntent().getParcelableExtra("profile");
        } else {
            profile = new PrimaryProfile();
            profile.setEnabled(true);
        }

        nameField = (EditText) findViewById(R.id.nameField);
        nameField.setText(profile.getName());
        nameField.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateName();

                    if (tagField.getText().length() == 0) {
                        tagField.setText(nameField.getText().toString()
                                .toLowerCase(Locale.getDefault()).replaceAll("\\s", ""));
                        validateTag();
                    }
                }
            }

        });

        tagField = (EditText) findViewById(R.id.tagField);
        tagField.setText(profile.getTag());
        tagField.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateTag();
                }

            }

        });

        packageField = (EditText) findViewById(R.id.packageField);
        packageField.setText(profile.getPackageName());
        packageField.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validatePackage();
                }
            }

        });

        packageButton = (ImageButton) findViewById(R.id.packageButton);
        packageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new PackagePickerFragment();

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(newFragment, null);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Crouton.clearCroutonsForActivity(this);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.custom_profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (profile.getId() == 0) {
            menu.removeItem(R.id.menu_delete);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_save:
                validate();
                if (errorFlags > 0) {
                    Crouton.showText(this, R.string.custom_profile_fix_errors, Style.ALERT);
                } else {
                    if (save()) {
                        boolean pushProfile = getPreferences(MODE_PRIVATE).getBoolean(
                                PREFERENCES_KEY_PUSH_PROFILE, true);
                        if (pushProfile && profile.getId() == 0) {
                            DialogFragment dialogFragment = new PushConfirmDialogFragment();
                            dialogFragment.show(getSupportFragmentManager());
                        } else {
                            finishOk();
                        }
                    } else {
                        Crouton.showText(this, R.string.custom_profile_error, Style.ALERT);
                    }
                }

                return true;
            case R.id.menu_discard:
                setResult(RESULT_CANCELED);
                finish();

                return true;
            case R.id.menu_delete:
                if (delete()) {
                    setResult(MainActivity.RESULT_CODE_PROFILE_DELETED,
                            getIntent());
                    finish();
                } else {
                    Crouton.showText(this, R.string.custom_profile_error, Style.ALERT);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setError(int flag) {
        errorFlags |= flag;
    }

    private void removeError(int flag) {
        errorFlags &= ~flag;
    }

    @Override
    public void onPackageSelected(CharSequence packageName) {
        packageField.setText(packageName);
    }

    private boolean save() {
        boolean result = false;

        profile.setName(nameField.getText().toString());
        profile.setTag(tagField.getText().toString());
        profile.setPackageName(packageField.getText().toString());

        dbAdapter.openWritable();
        if (profile.getId() != 0) {
            result = dbAdapter.updatePrimaryProfile(profile);
        } else {
            result = dbAdapter.insertPrimaryProfile(profile);
        }
        dbAdapter.close();

        return result;
    }

    private void pushProfile() {
        if (profile.getId() == 0) {
            TagPushMessage message = new TagPushMessage.Builder().name(profile.getName())
                    .tag(profile.getTag()).build();

            Intent intent = new Intent(PrimaryService.ACTION_SEND_MESSAGE);
            intent.putExtra(PrimaryService.EXTRA_MESSAGE, BaseMessage.toJsonString(message));
            broadcastManager.sendBroadcast(intent);
        }
    }

    private void finishOk() {
        setResult(RESULT_OK);
        finish();
    }

    private boolean delete() {
        dbAdapter.openWritable();
        boolean result = dbAdapter.deletePrimaryProfile(profile);
        dbAdapter.close();

        return result;
    }

    private void validate() {
        validateName();
        validateTag();
        validatePackage();
    }

    private void validateName() {
        if (nameField.getText().length() == 0) {
            nameField.setError(getString(R.string.custom_profile_invalid_empty,
                    getString(R.string.custom_profile_header_name)));
            setError(ERROR_FLAG_NAME);
        } else {
            nameField.setError(null);
            removeError(ERROR_FLAG_NAME);
        }
    }

    private void validateTag() {
        if (tagField.getText().length() == 0) {
            tagField.setError(getString(R.string.custom_profile_invalid_empty,
                    getString(R.string.custom_profile_header_tag)));
            setError(ERROR_FLAG_TAG);
        } else {
            String tag = tagField.getText().toString();
            dbAdapter.openReadable();
            PrimaryProfile tagProfile = dbAdapter.getPrimaryProfileByTag(tag);
            dbAdapter.close();

            if (tagProfile != null && tagProfile.getId() != profile.getId()) {
                tagField.setError(getString(R.string.custom_profile_invalid_tag_clash,
                        tagProfile.getName()));
                setError(ERROR_FLAG_TAG);
            } else {
                tagField.setError(null);
                removeError(ERROR_FLAG_TAG);
            }
        }
    }

    private void validatePackage() {
        if (packageField.getText().length() == 0) {
            packageField.setError(getString(R.string.custom_profile_invalid_empty,
                    getString(R.string.custom_profile_header_package)));
            setError(ERROR_FLAG_PACKAGE);
        } else {
            packageField.setError(null);
            removeError(ERROR_FLAG_PACKAGE);
        }
    }

    public static class PushConfirmDialogFragment extends DialogFragment {
        private PrimaryCustomProfileActivity activity;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            this.activity = (PrimaryCustomProfileActivity) activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.custom_profile_push_title);
            builder.setMessage(R.string.custom_profile_push_message);

            builder.setPositiveButton(R.string.custom_profile_push_yes,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.pushProfile();
                            activity.finishOk();
                        }
                    });

            builder.setNeutralButton(R.string.custom_profile_push_no,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finishOk();
                        }
                    });

            builder.setNegativeButton(R.string.custom_profile_push_never,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().getPreferences(MODE_PRIVATE).edit()
                                    .putBoolean(PREFERENCES_KEY_PUSH_PROFILE, false).commit();

                            activity.finishOk();
                        }
                    });

            return builder.create();
        }
    }
}
