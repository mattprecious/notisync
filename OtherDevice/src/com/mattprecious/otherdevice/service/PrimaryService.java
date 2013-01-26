
package com.mattprecious.otherdevice.service;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mattprecious.otherdevice.R;
import com.mattprecious.otherdevice.activity.OtherDevice;
import com.mattprecious.otherdevice.bluetooth.BluetoothService;
import com.mattprecious.otherdevice.message.BaseMessage;
import com.mattprecious.otherdevice.message.ClearMessage;
import com.mattprecious.otherdevice.message.PhoneCallMessage;
import com.mattprecious.otherdevice.message.TextMessage;
import com.mattprecious.otherdevice.util.Constants;
import com.mattprecious.otherdevice.util.ContactHelper;
import com.mattprecious.otherdevice.util.Preferences;

public class PrimaryService extends Service {
    @SuppressWarnings("unused")
    private final static String TAG = "PrimaryService";

    private static boolean running = false;

    public static final int MESSAGE_SHUTTING_DOWN = 1;

    private final int NOTIFICATION_ID_RUNNING = 1;

    private final Joiner notificationJoiner = Joiner.on(", ").skipNulls();
    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private LocalBroadcastManager broadcastManager;
    private NotificationManager notificationManager;
    private Map<String, BluetoothService> bluetoothServices = Maps.newHashMap();
    private PrimaryHandler handler;
    private Timer timer;

    private Bundle lastNotificationBundle;

    @Override
    public void onCreate() {
        super.onCreate();

        broadcastManager = LocalBroadcastManager.getInstance(this);

        running = true;
        broadcastManager.sendBroadcast(new Intent(Constants.ACTION_PRIMARY_SERVICE_STARTED));

        if (bluetoothAdapter == null) {
            stopSelf();
            return;
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = buildRunningNotification();
        startForeground(NOTIFICATION_ID_RUNNING, notification);

        handler = new PrimaryHandler(this);

        broadcastManager.registerReceiver(reconnectReceiver, new IntentFilter(
                Constants.ACTION_RECONNECT));
        broadcastManager.registerReceiver(updateDevicesReceiver, new IntentFilter(
                Constants.ACTION_UPDATE_DEVICES));
        broadcastManager.registerReceiver(timerReceiver, new IntentFilter(
                Constants.ACTION_UPDATE_TIMER));
        broadcastManager.registerReceiver(sendMessageReceiver, new IntentFilter(
                Constants.ACTION_SEND_MESSAGE));

        registerReceiver(bluetoothStateReceiver, new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        registerReceiver(phoneReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));

        updateTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        running = false;
        broadcastManager.sendBroadcast(new Intent(Constants.ACTION_PRIMARY_SERVICE_STOPPED));

        if (timer != null) {
            timer.cancel();
        }

        try {
            broadcastManager.unregisterReceiver(reconnectReceiver);
            broadcastManager.unregisterReceiver(updateDevicesReceiver);
            broadcastManager.unregisterReceiver(timerReceiver);
            broadcastManager.unregisterReceiver(sendMessageReceiver);

            unregisterReceiver(bluetoothStateReceiver);
            unregisterReceiver(smsReceiver);
            unregisterReceiver(phoneReceiver);
        } catch (IllegalArgumentException e) {

        }

        for (BluetoothService service : bluetoothServices.values()) {
            service.stop();
        }

        synchronized (bluetoothServices) {
            bluetoothServices.clear();
        }

        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isRunning() {
        return running;
    }

    private synchronized void updateTimer() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                connectDevices();
            }
        }, 0, Preferences.getPrimaryReconnectDelay(this) * 60000);
    }

    private void connectDevices() {
        if (bluetoothAdapter.isEnabled()) {
            synchronized (bluetoothServices) {
                for (String address : Preferences.getDevices(this)) {
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    BluetoothService service = bluetoothServices.get(address);

                    if (service == null) {
                        service = new BluetoothService(this, handler, false);
                        bluetoothServices.put(address, service);
                    } else {
                        int state = service.getState();
                        if (state == BluetoothService.STATE_CONNECTED
                                || state == BluetoothService.STATE_CONNECTING) {
                            continue;
                        } else if (state == BluetoothService.STATE_LISTEN) {
                            service.connect(device);
                            continue;
                        }

                        service.stop();
                    }

                    service.start();
                    service.connect(device);
                }
            }
        } else {
            clearServices();
        }

        updateRunningNotification();
    }

    private void clearServices() {
        synchronized (bluetoothServices) {
            for (String address : bluetoothServices.keySet()) {
                BluetoothService service = bluetoothServices.get(address);
                if (service != null) {
                    service.stop();
                }
            }
            bluetoothServices.clear();
        }
    }

    private synchronized void updateRunningNotification() {
        if (!running) {
            return;
        }

        Notification notification = buildRunningNotification(true);

        if (notification != null) {
            notificationManager.notify(NOTIFICATION_ID_RUNNING, notification);
        }
    }

    private Notification buildRunningNotification() {
        return buildRunningNotification(false);
    }

    private Notification buildRunningNotification(boolean nullIfNoChange) {
        Bundle notificationBundle = new Bundle();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MIN);
        builder.setSmallIcon(R.drawable.ic_stat_logo);
        builder.setContentTitle(getString(R.string.app_name));

        Intent intent = new Intent(this, OtherDevice.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);

        String contentText = null;
        Set<String> connectedNames = Sets.newHashSet();

        if (bluetoothAdapter.isEnabled()) {
            synchronized (bluetoothServices) {
                for (String address : bluetoothServices.keySet()) {
                    BluetoothService service = bluetoothServices.get(address);
                    if (service != null && service.getState() == BluetoothService.STATE_CONNECTED) {
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                        connectedNames.add(device.getName());
                    }
                }
            }

            if (connectedNames.size() != Preferences.getDevices(this).size()) {
                PendingIntent reconnectIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                        Constants.ACTION_RECONNECT), 0);
                builder.addAction(R.drawable.ic_action_refresh,
                        getString(R.string.noti_action_connect), reconnectIntent);

                notificationBundle.putBoolean("connect_action", true);
            }

            if (connectedNames.size() > 0) {
                contentText = getString(R.string.noti_connected_to,
                        notificationJoiner.join(connectedNames));
            } else {
                contentText = getString(R.string.noti_not_connected);
            }
        } else {
            contentText = getString(R.string.noti_bt_not_enabled);

            PendingIntent bluetoothIntent = PendingIntent.getActivity(this, 0, new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
            builder.addAction(R.drawable.ic_action_bluetooth,
                    getString(R.string.noti_action_enable_bt), bluetoothIntent);

            notificationBundle.putBoolean("bt_enable_action", true);
        }

        builder.setContentText(contentText);
        notificationBundle.putString("text", contentText);

        if (nullIfNoChange && bundleEquals(notificationBundle, lastNotificationBundle)) {
            return null;
        }

        lastNotificationBundle = notificationBundle;

        return builder.build();
    }

    public void sendMessage(String message) {
        synchronized (bluetoothServices) {
            for (BluetoothService service : bluetoothServices.values()) {
                service.write(message.getBytes());
            }
        }
    }

    private boolean bundleEquals(Bundle a, Bundle b) {
        if (!a.containsKey("text") || !a.containsKey("bt_enable_action") || !b.containsKey("text")
                || !b.containsKey("bt_enable_action")) {
            return false;
        }

        if (!a.getString("text").equals(b.getString("text"))) {
            return false;
        }

        if (a.getBoolean("bt_enable_action") == b.getBoolean("bt_enable_action")) {
            return false;
        }

        return true;
    }

    private static class PrimaryHandler extends Handler {
        private final WeakReference<PrimaryService> weakService;

        public PrimaryHandler(PrimaryService service) {
            weakService = new WeakReference<PrimaryService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            PrimaryService service = weakService.get();
            switch (msg.what) {
                case BluetoothService.MESSAGE_CONNECTED:
                case BluetoothService.MESSAGE_DISCONNECTED:
                    service.updateRunningNotification();
                    break;
            }
        }
    };

    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_ON);
            if (state == BluetoothAdapter.STATE_ON) {
                connectDevices();
            } else if (state == BluetoothAdapter.STATE_TURNING_OFF
                    || state == BluetoothAdapter.STATE_OFF) {
                clearServices();
            }

            updateRunningNotification();
        }
    };

    private final BroadcastReceiver reconnectReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            connectDevices();
        }
    };

    private final BroadcastReceiver updateDevicesReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            clearServices();
            connectDevices();
        }
    };

    private final BroadcastReceiver timerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimer();
        }

    };

    private final BroadcastReceiver sendMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            sendMessage(message);
        }
    };

    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Preferences.getPrimaryTextMessageEnabled(context)) {
                return;
            }

            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    String number = msgs[i].getOriginatingAddress();
                    String message = msgs[i].getMessageBody();

                    String name = ContactHelper.getNameByNumber(context, number);

                    TextMessage textMessage = new TextMessage.Builder().name(name).number(number)
                            .message(message).build();
                    sendMessage(BaseMessage.toJsonString(textMessage));
                }
            }

        }
    };

    private final BroadcastReceiver phoneReceiver = new BroadcastReceiver() {
        private PhoneCallMessage incomingCallMessage;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Preferences.getPrimaryPhoneCallEnabled(context)) {
                return;
            }

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String name = ContactHelper.getNameByNumber(context, phoneNumber);

            BaseMessage message = null;
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                incomingCallMessage = new PhoneCallMessage.Builder().name(name).number(phoneNumber)
                        .type(PhoneCallMessage.Type.INCOMING).build();
                message = incomingCallMessage;
            } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                if (incomingCallMessage != null) {
                    message = new ClearMessage(incomingCallMessage);
                }

                incomingCallMessage = null;
            } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                if (incomingCallMessage != null) {
                    message = new PhoneCallMessage.Builder().name(incomingCallMessage.name)
                            .number(incomingCallMessage.number).type(PhoneCallMessage.Type.MISSED)
                            .build();

                    incomingCallMessage = null;
                }
            }

            if (message != null) {
                sendMessage(BaseMessage.toJsonString(message));
            }

        }

    };

}
