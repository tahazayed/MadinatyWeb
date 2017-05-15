package com.taha.alrehab.Helpers;


import android.util.Log;

public class ConnectionHelper {
    private static final String TAG = ConnectionHelper.class.getSimpleName();

    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
}
