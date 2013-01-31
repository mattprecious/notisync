
package com.mattprecious.notisync.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mattprecious.notisync.service.PrimaryService;
import com.mattprecious.notisync.service.SecondaryService;
import com.mattprecious.notisync.util.Preferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Preferences.getStartOnBoot(context)) {
            if (Preferences.isPrimary(context)) {
                context.startService(new Intent(context, PrimaryService.class));
            } else {
                context.startService(new Intent(context, SecondaryService.class));
            }
        }
    }

}
