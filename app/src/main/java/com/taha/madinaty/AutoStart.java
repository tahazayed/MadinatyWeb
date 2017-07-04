package com.taha.madinaty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.taha.madinaty.BackgroundServices.NotificationsService;

public class AutoStart extends BroadcastReceiver {
    public void onReceive(Context _context, Intent _intent) {
        try {

            Intent intent = new Intent(_context, NotificationsService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startService(intent);
        } catch (Exception e) {
            Log.e(AutoStart.class.getSimpleName(), e.getMessage());
        }
    }
}