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

package com.mattprecious.notisync.devtools;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.message.BaseMessage;
import com.mattprecious.notisync.util.Preferences;

public class DevToolsActivity extends SherlockFragmentActivity {
    public static final String ACTION_SEND_MESSAGE =
            "com.mattprecious.notisync.activity.DevToolsActivity.ACTION_SEND_MESSAGE";
    public static final String ACTION_RECEIVE_MESSAGE =
            "com.mattprecious.notisync.activity.DevToolsActivity.ACTION_RECEIVE_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DevToolsFragment()).commit();
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

    public void sendMessage(BaseMessage message) {
        Intent intent;
        if (Preferences.isPrimary(this)) {
            intent = new Intent(ACTION_SEND_MESSAGE);
        } else {
            intent = new Intent(ACTION_RECEIVE_MESSAGE);
        }

        intent.putExtra("message", BaseMessage.toJsonString(message));

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
