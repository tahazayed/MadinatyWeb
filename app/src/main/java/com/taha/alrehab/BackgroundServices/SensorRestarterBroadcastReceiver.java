package com.taha.alrehab.BackgroundServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by taha on 10/24/16.
 */

public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), NotificationsService.class.getSimpleName() + " Stopped");
            context.startService(new Intent(context, NotificationsService.class));
        } catch (Exception e) {
            Log.e(SensorRestarterBroadcastReceiver.class.getSimpleName(), e.getMessage());
        }
    }
}
