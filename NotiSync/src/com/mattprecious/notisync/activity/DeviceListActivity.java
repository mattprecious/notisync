
package com.mattprecious.notisync.activity;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.mattprecious.notisync.fragment.DeviceListFragment;

public class DeviceListActivity extends SherlockFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DeviceListFragment()).commit();
    }
}
