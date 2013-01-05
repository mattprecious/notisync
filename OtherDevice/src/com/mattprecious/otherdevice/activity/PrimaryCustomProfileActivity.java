
package com.mattprecious.otherdevice.activity;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.EditText;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.db.DbAdapter;
import com.mattprecious.otherdevice.fragment.PackagePickerFragment;
import com.mattprecious.otherdevice.fragment.PrimaryCustomProfilesFragment;
import com.mattprecious.otherdevice.model.PrimaryProfile;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class PrimaryCustomProfileActivity extends Activity implements
        PackagePickerFragment.OnPackageSelectedListener {
    private final int ERROR_FLAG_NAME = 1 << 0;
    private final int ERROR_FLAG_TAG = 1 << 1;
    private final int ERROR_FLAG_PACKAGE = 1 << 2;

    private DbAdapter dbAdapter;
    private PrimaryProfile profile;

    private int errorFlags = 0;

    @InjectView
    private EditText nameField;
    @InjectView
    private EditText tagField;
    @InjectView
    private EditText packageField;
    @InjectView
    private ImageButton packageButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primary_custom_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DbAdapter(this);

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
                validatePackage();
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
                    Crouton.showText(this, "Please fix the errors", Style.ALERT);
                } else {
                    if (save()) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Crouton.showText(this, "An error has occured", Style.ALERT);
                    }
                }

                return true;
            case R.id.menu_discard:
                setResult(RESULT_CANCELED);
                finish();

                return true;
            case R.id.menu_delete:
                if (delete()) {
                    setResult(PrimaryCustomProfilesFragment.RESULT_CODE_PROFILE_DELETED,
                            getIntent());
                    finish();
                } else {
                    Crouton.showText(this, "An error has occured", Style.ALERT);
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
            nameField.setError("Must not be empty");
            setError(ERROR_FLAG_NAME);
        } else {
            nameField.setError(null);
            removeError(ERROR_FLAG_NAME);
        }
    }

    private void validateTag() {
        if (tagField.getText().length() == 0) {
            tagField.setError("Must not be empty");
            setError(ERROR_FLAG_TAG);
        } else {
            dbAdapter.openReadable();
            PrimaryProfile tagProfile = dbAdapter.getPrimaryProfileByTag(tagField.getText()
                    .toString());
            dbAdapter.close();

            if (tagProfile != null && tagProfile.getId() != profile.getId()) {
                tagField.setError("Must be unique");
                setError(ERROR_FLAG_TAG);
            } else {
                tagField.setError(null);
                removeError(ERROR_FLAG_TAG);
            }
        }
    }

    private void validatePackage() {
        if (packageField.getText().length() == 0) {
            packageField.setError("Must not be empty");
            setError(ERROR_FLAG_PACKAGE);
        } else {
            packageField.setError(null);
            removeError(ERROR_FLAG_PACKAGE);
        }
    }
}
