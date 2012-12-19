package com.mattprecious.otherdevice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mattprecious.otherdevice.service.PrimaryService;
import com.mattprecious.otherdevice.service.SecondaryService;
import com.mattprecious.otherdevice.util.Preferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = Preferences.getType(context);
        if (Preferences.getBootOnStart(context)) {
            if (Preferences.TYPE_PRIMARY.equals(type)) {
                context.startService(new Intent(context, PrimaryService.class));
            } else {
                context.startService(new Intent(context, SecondaryService.class));
            }
        }
    }

}
