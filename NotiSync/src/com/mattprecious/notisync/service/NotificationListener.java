package com.mattprecious.notisync.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService {
    private static final String TAG = NotificationListener.class.getSimpleName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, sbn.toString());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, sbn.toString());
    }
}
