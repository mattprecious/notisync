/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.profile;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.R;
import com.mattprecious.notisync.util.Preferences;

public class StandardProfileActivity extends SherlockFragmentActivity {
    public static final String EXTRA_TYPE = "type";

    public static enum ProfileType {
        TEXT,
        PHONE,
        GTALK,
    }

    private StandardProfileFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() == null || !getIntent().hasExtra(EXTRA_TYPE)) {
            throw new IllegalArgumentException(
                    String.format("Must pass %s as an extra", EXTRA_TYPE));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProfileType type = (ProfileType) getIntent().getExtras().getSerializable(EXTRA_TYPE);
        switch (type) {
            case TEXT:
                setTitle(R.string.profile_text_messages);
                if (Preferences.isPrimary(this)) {
                    fragment = new EmptyFragment();
                } else {
                    fragment = new TextMessageFragment();
                }

                break;
            case PHONE:
                setTitle(R.string.profile_phone_calls);
                if (Preferences.isPrimary(this)) {
                    fragment = new EmptyFragment();
                } else {
                    fragment = new PhoneCallFragment();
                }

                break;
            case GTALK:
                setTitle(R.string.profile_gtalk);
                if (Preferences.isPrimary(this)) {
                    fragment = new EmptyFragment();
                } else {
                    fragment = new GtalkFragment();
                }

                break;
        }

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment)
                .commit();
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
    public void onBackPressed() {
        if (fragment instanceof EmptyFragment) {
            // result defaults to canceled, but we don't want to show that
            // message
            setResult(-2);
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (fragment instanceof EmptyFragment) {
            return false;
        } else {
            MenuInflater inflater = getSupportMenuInflater();
            inflater.inflate(R.menu.standard_profile, menu);

            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_save:
                if (save()) {
                    setResult(RESULT_OK);
                    finish();
                }

                return true;
            case R.id.menu_discard:
                setResult(RESULT_CANCELED);
                finish();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean save() {
        return fragment.onSave();
    }
}
