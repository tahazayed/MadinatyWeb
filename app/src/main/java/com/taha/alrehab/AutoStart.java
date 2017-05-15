package com.taha.alrehab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.taha.alrehab.BackgroundServices.NotificationsService;

public class AutoStart extends BroadcastReceiver {
    public void onReceive(Context arg0, Intent arg1) {
        try {
            Intent intent = new Intent(arg0, NotificationsService.class);
            arg0.startService(intent);
        } catch (Exception e) {
            Log.e(AutoStart.class.getSimpleName(), e.getMessage());
        }
    }
}