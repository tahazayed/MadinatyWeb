package com.taha.alrehab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.taha.alrehab.Helpers.ConnectionHelper;

public class SplashScreen extends Activity {
    private static final String TAG = SplashScreen.class.getSimpleName();
    //Set waktu lama splashscreen
    private static int splashInterval = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);


            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.splashscreen);

            new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // do something after 1s
                }

                @Override
                public void onFinish() {
                    if (ConnectionHelper.isOnline()) {
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);


                                //jeda selesai Splashscreen
                                this.finish();
                            }

                            private void finish() {
                                // TODO Auto-generated method stub

                            }
                        }, splashInterval);

                    } else {
                        Toast.makeText(getApplicationContext(), "no internet", Toast.LENGTH_LONG).show();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                                    System.exit(0);
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        };
                        thread.start();
                    }
                }

            }.start();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}