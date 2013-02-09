
package com.mattprecious.notisync.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mattprecious.notisync.service.BluetoothFixService;

import java.util.Calendar;

public class AlarmHelper {

    public static void scheduleBluetoothFixAlarm(Context context) {
        String[] pieces = Preferences.getBluetoothFixTime(context).split(":");

        int hour = Integer.parseInt(pieces[0]);
        int minute = Integer.parseInt(pieces[1]);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        PendingIntent intent = getBluetoothFixIntent(context);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(intent);
        alarmManager.setRepeating(AlarmManager.RTC, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, intent);
    }

    public static void cancelBluetoothFixAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getBluetoothFixIntent(context));
    }

    private static PendingIntent getBluetoothFixIntent(Context context) {
        Intent intent = new Intent(context, BluetoothFixService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        return pendingIntent;
    }
}
