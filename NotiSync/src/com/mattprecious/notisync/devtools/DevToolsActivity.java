
package com.mattprecious.notisync.devtools;

import android.os.Bundle;

import org.holoeverywhere.app.Activity;

public class DevToolsActivity extends Activity {
    public static final String ACTION_RECEIVE_MESSAGE =
            "com.mattprecious.notisync.activity.DevToolsActivity.ACTION_RECEIVE_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DevToolsFragment()).commit();
    }

}
