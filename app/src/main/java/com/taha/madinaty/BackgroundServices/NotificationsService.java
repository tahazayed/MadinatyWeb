package com.taha.madinaty.BackgroundServices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.taha.madinaty.BusinessEntities.MadinatyNotification;
import com.taha.madinaty.DB.UserDBHandler;
import com.taha.madinaty.JSON.MadinatyNotificationsJSONHandler;
import com.taha.madinaty.MainActivity;
import com.taha.madinaty.R;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class NotificationsService extends Service implements MadinatyNotificationsJSONHandler.MadinatyNotificationsJSONHandlerClient {

    private static final String TAG = NotificationsService.class.getSimpleName();
    public static boolean isRunning = false;
    static String userId;
    private static long UPDATE_INTERVAL = 150 * 60 * 1000;  //default
    private static Timer timer = new Timer();
    private boolean IsDebug = true;

    public NotificationsService(Context applicationContext) {
        super();
        if (IsDebug) Log.d(TAG, "Constructor");
    }

    public NotificationsService() {
    }

    @Override
    public void onCreate() {
        try {
            if (IsDebug) Log.d(TAG, "Service onCreate");

            isRunning = true;
            UserDBHandler db = new UserDBHandler(getApplicationContext());
            userId = db.getUserId();
            if (userId.isEmpty()) {
                userId = UUID.randomUUID().toString();
                db.updateUser(userId);
            }
            db.close();
            // Create a new console logger
            Logger logger = new Logger() {

                @Override
                public void log(String message, LogLevel level) {
                    if (IsDebug) Log.d(TAG, "NotificationsHub Log" + message);
                }
            };
            // Connect to the server
            HubConnection conn = new HubConnection(getString(R.string.NotificationsHUB), "", true, logger);

            // Create the hub proxy
            HubProxy proxy = conn.createHubProxy("NotificationsHub");

            proxy.subscribe(new Object() {
                @SuppressWarnings("unused")
                public void updateNotifications() {
                    if (IsDebug) Log.d(TAG, "UpdateNotifications called");
                    doServiceWork();
                }
            });
            // Start the connection
            conn.start()
                    .done(new Action<Void>() {

                        @Override
                        public void run(Void obj) throws Exception {
                            if (IsDebug) Log.d(TAG, "NotificationsHub Connected");
                        }
                    }).onError(new ErrorCallback() {

                @Override
                public void onError(Throwable throwable) {
                    if (IsDebug) Log.d(TAG, "NotificationsHub Error");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            super.onStartCommand(intent, flags, startId);
            if (IsDebug) Log.d(TAG, "Service onStartCommand");

            timer.scheduleAtFixedRate(

                    new TimerTask() {

                        public void run() {

                            doServiceWork();

                        }
                    }, 1000, UPDATE_INTERVAL);
            if (IsDebug) Log.d(TAG, "Timer started....");

            return Service.START_STICKY;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return Service.START_REDELIVER_INTENT;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        if (IsDebug) Log.d(TAG, "Service onBind");
        return null;
    }

    private void doServiceWork() {

        try {
            new MadinatyNotificationsJSONHandler(NotificationsService.this).execute(getString(R.string.NotificationAPI) + userId);
            if (IsDebug) Log.d(TAG, "StoriesJSONHandler invoked...");

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onDestroy() {

        try {
            isRunning = false;
            super.onDestroy();
            if (IsDebug) Log.i(TAG, "ondestroy!");
            Intent broadcastIntent = new Intent("com.taha.madinaty.BackgroundServices.ActivityRecognition.RestartSensor");
            sendBroadcast(broadcastIntent);
            if (timer != null) timer.cancel();
            if (IsDebug) Log.d(TAG, "Timer stopped...");

            if (IsDebug) Log.d(TAG, "Service onDestroy");

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onMadinatyNotificationsJSONHandlerClientResult(List<MadinatyNotification> list) {
        try {
            if (IsDebug)
                Log.d(TAG, "onMadinatyNotificationsJSONHandlerJSONHandlerClientResult invoked..." + list.size());


            final Random rand = new Random();
            rand.setSeed(100);
            if (list.size() > 0) {
                //NotificationManagerCompat.from(this).cancelAll();
                NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                Resources res = getApplicationContext().getResources();
                for (MadinatyNotification oAlrehabNotification : list) {
                    int msgId = rand.nextInt();
                    Intent notificationIntent = new Intent(this, MainActivity.class);

                    notificationIntent.putExtra("Type", (Integer.toString(oAlrehabNotification.get_type())));
                    notificationIntent.putExtra("Id", (Integer.toString(oAlrehabNotification.get_id())));

                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                            msgId, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);


                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    builder
                            //.addAction(R.mipmap.ic_launcher, oAlrehabNotification.get_title(), contentIntent)
                            .setContentIntent(contentIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                            .setTicker(oAlrehabNotification.get_title())
                            .setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(oAlrehabNotification.get_title())
                            //.setContentText(oAlrehabNotification.get_title())
                            .setContentText(oAlrehabNotification.get_body())
                            //.setExtras(extras)
                            .setOnlyAlertOnce(false)
                            .setGroup("Madinaty")
                            .setGroupSummary(false)
                            .setCategory("news");


                    Notification n = builder.build();

                    n.defaults |= Notification.DEFAULT_ALL;
                    nm.notify(msgId, n);
                }

            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}