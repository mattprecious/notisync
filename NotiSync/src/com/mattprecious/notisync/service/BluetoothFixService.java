
package com.mattprecious.notisync.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class BluetoothFixService extends Service {

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private Timer selfDestructTimer;

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        if (bluetoothAdapter.isEnabled()) {
            // if we're still running after 1 minute, then bail
            selfDestructTimer = new Timer();
            selfDestructTimer.schedule(selfDestructTask, 60000);

            bluetoothAdapter.disable();
        } else {
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (selfDestructTimer != null) {
            selfDestructTimer.cancel();
        }

        try {
            unregisterReceiver(bluetoothReceiver);
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_ON);
            if (state == BluetoothAdapter.STATE_OFF) {
                bluetoothAdapter.enable();
                stopSelf();
            }
        }
    };

    private TimerTask selfDestructTask = new TimerTask() {

        @Override
        public void run() {
            stopSelf();
        }
    };

}
