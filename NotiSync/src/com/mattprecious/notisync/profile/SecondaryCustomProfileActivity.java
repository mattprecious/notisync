
package com.mattprecious.notisync.profile;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
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
import com.mattprecious.notisync.fragment.RequestTagsDialogFragment;
import com.mattprecious.notisync.fragment.RequestTagsDialogFragment.OnTagSelectedListener;
import com.mattprecious.notisync.model.SecondaryProfile;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.EditText;

import java.util.Locale;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SecondaryCustomProfileActivity extends Activity implements OnTagSelectedListener {
    private final int ERROR_FLAG_NAME = 1 << 0;
    private final int ERROR_FLAG_TAG = 1 << 1;

    private final int REQUEST_CODE_RINGTONE_PICKER = 1;

    private DbAdapter dbAdapter;
    private SecondaryProfile profile;

    private int errorFlags = 0;

    private EditText nameField;
    private EditText tagField;
    private ImageButton tagRequestButton;
    private CheckBox unconnectedOnlyCheckBox;
    private Button ringtoneSelector;
    private CheckBox vibrateCheckBox;
    private CheckBox lightsCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_custom_secondary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DbAdapter(this);

        if (getIntent().hasExtra("profile")) {
            profile = getIntent().getParcelableExtra("profile");
        } else {
            profile = new SecondaryProfile();
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

        tagRequestButton = (ImageButton) findViewById(R.id.tagRequestButton);
        tagRequestButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new RequestTagsDialogFragment();
                newFragment.show(getSupportFragmentManager());
            }

        });

        unconnectedOnlyCheckBox = (CheckBox) findViewById(R.id.unconnectedOnlyCheckBox);
        unconnectedOnlyCheckBox.setChecked(profile.isUnconnectedOnly());

        ringtoneSelector = (Button) findViewById(R.id.ringtoneSelector);
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
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        getRingtoneUri(profile.getRingtone()));

                startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER);

            }
        });

        vibrateCheckBox = (CheckBox) findViewById(R.id.vibrateCheckBox);
        vibrateCheckBox.setChecked(profile.isVibrate());

        checkForVibrator();

        lightsCheckBox = (CheckBox) findViewById(R.id.lightsCheckBox);
        lightsCheckBox.setChecked(profile.isLed());

        updateRingtoneSelector();
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

        menu.removeItem(R.id.menu_push);

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
                        setResult(RESULT_OK);
                        finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RINGTONE_PICKER:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    if (uri == null) {
                        profile.setRingtone(null);
                    } else {
                        profile.setRingtone(uri.toString());
                    }

                    updateRingtoneSelector();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void checkForVibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (!((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator()) {
                vibrateCheckBox.setVisibility(View.GONE);
            }
        }
    }

    private void updateRingtoneSelector() {
        Uri ringtoneUri = getRingtoneUri(profile.getRingtone());

        String ringtoneName = null;
        if (ringtoneUri == null) {
            ringtoneName = getString(R.string.ringtone_silent);
        } else {
            ringtoneName = RingtoneManager
                    .getRingtone(this, getRingtoneUri(profile.getRingtone())).getTitle(this);
        }

        ringtoneSelector.setText(ringtoneName);
    }

    private void setError(int flag) {
        errorFlags |= flag;
    }

    private void removeError(int flag) {
        errorFlags &= ~flag;
    }

    @Override
    public void onTagSelected(String profileName, String tag) {
        tagField.setText(tag);
        validateTag();

        if (nameField.getText().length() == 0) {
            nameField.setText(profileName);
            validateName();
        }
    }

    private boolean save() {
        boolean result = false;

        profile.setName(nameField.getText().toString());
        profile.setTag(tagField.getText().toString());
        profile.setUnconnectedOnly(unconnectedOnlyCheckBox.isChecked());
        profile.setVibrate(vibrateCheckBox.isChecked());
        profile.setLed(lightsCheckBox.isChecked());

        dbAdapter.openWritable();
        if (profile.getId() != 0) {
            result = dbAdapter.updateSecondaryProfile(profile);
        } else {
            result = dbAdapter.insertSecondaryProfile(profile);
        }
        dbAdapter.close();

        return result;
    }

    private boolean delete() {
        dbAdapter.openWritable();
        boolean result = dbAdapter.deleteSecondaryProfile(profile);
        dbAdapter.close();

        return result;
    }

    private Uri getRingtoneUri(String ringtone) {
        return (ringtone == null) ? null : Uri.parse(ringtone);
    }

    private void validate() {
        validateName();
        validateTag();
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
            String tag = tagField.getText()
                    .toString();
            dbAdapter.openReadable();
            SecondaryProfile tagProfile = dbAdapter.getSecondaryProfileByTag(tag);
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
}
