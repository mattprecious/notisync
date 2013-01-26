
package com.mattprecious.otherdevice.service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.mattprecious.otherdevice.db.DbAdapter;
import com.mattprecious.otherdevice.message.BaseMessage;
import com.mattprecious.otherdevice.message.CustomMessage;
import com.mattprecious.otherdevice.message.GtalkMessage;
import com.mattprecious.otherdevice.model.PrimaryProfile;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.Preferences;

public class NotificationService extends AccessibilityService {
    private final static String TAG = "NotificationService";

    private final static List<String> gtalkPackageNames = Arrays.asList(new String[] {
            "com.google.android.talk", "com.google.android.apps.gtalkservice",
            "com.google.android.gsf",
    });

    // TODO: Locale issues? This pattern isn't really global...
    private final Pattern gtalkPattern = Pattern.compile("(.*): (.*)");

    private static boolean running = false;

    private LocalBroadcastManager broadcastManager;
    private DbAdapter dbAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        broadcastManager = LocalBroadcastManager.getInstance(this);
        dbAdapter = new DbAdapter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        running = false;
    }

    public static boolean isRunning() {
        return running;
    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "Service connected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAcessibilityEvent()");
        if (!Preferences.isPrimary(this)) {
            Log.d(TAG, "not primary mode");
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            String packageName = (String) event.getPackageName();
            Notification notification = (Notification) event.getParcelableData();
            if (notification == null) {
                Log.d(TAG, "notification is null");
                return;
            }

            CharSequence tickerText = notification.tickerText;

            if (packageName == null) {
                return;
            }

            // handle gtalk messages
            if (gtalkPackageNames.contains(packageName)) {
                if (!Preferences.getPrimaryGtalkEnabled(this)) {
                    return;
                }

                if (tickerText == null) {
                    Log.e(TAG, "gtalk ticker text is null");
                    return;
                }

                Matcher matcher = gtalkPattern.matcher(tickerText);
                if (matcher.matches()) {
                    String sender = matcher.group(1);
                    String message = matcher.group(2);

                    GtalkMessage gtalkMessage = new GtalkMessage.Builder().sender(sender)
                            .message(message).build();

                    sendMessage(gtalkMessage);
                } else {
                    Log.d(TAG, "Pattern does not match: " + tickerText);
                }
                return;
            } else {
                dbAdapter.openReadable();
                PrimaryProfile profile = dbAdapter.getPrimaryProfileByPackage(packageName);
                dbAdapter.close();

                if (profile != null && profile.isEnabled()) {
                    String message = notification.tickerText == null ? null
                            : notification.tickerText.toString();
                    CustomMessage customMessage = new CustomMessage.Builder().tag(profile.getTag())
                            .title("Custom Message").message(message).build();

                    sendMessage(customMessage);
                }
            }

            Log.d(TAG, "packageName: " + packageName);
            // Log.d(TAG, notification.tickerText);
        }

    }

    @Override
    public void onInterrupt() {
        // TODO Auto-generated method stub
    }

    private void sendMessage(BaseMessage message) {
        Intent intent = new Intent(Constants.ACTION_SEND_MESSAGE);
        intent.putExtra("message", BaseMessage.toJsonString(message));
        broadcastManager.sendBroadcast(intent);
    }

}
