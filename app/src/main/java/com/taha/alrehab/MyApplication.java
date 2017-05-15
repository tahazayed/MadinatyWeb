package com.taha.alrehab;

/**
 * Created by taha on 10/24/16.
 */

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

}