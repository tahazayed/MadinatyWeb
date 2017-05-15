package com.taha.alrehab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.taha.alrehab.BackgroundServices.NotificationsService;
import com.taha.alrehab.Helpers.ConnectionHelper;

//import android.webkit.WebChromeClient;
//import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    private final Handler mHideHandler = new Handler();
    protected WebView browser = null;
    protected GestureDetector gestureDetector;
    // Create Preference to check if application is going to be called first
    // time.
    SharedPreferences appPref;
    boolean isFirstTime = true;
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            try {
                // Delayed removal of status and navigation bar

                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        //| View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gestureDetector = new GestureDetector(this, this);
        this.gestureDetector.setOnDoubleTapListener(this);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            browser = (WebView) findViewById(R.id.webView);

            mContentView = browser;


            browser = (WebView) findViewById(R.id.webView);
            browser.clearHistory();

            CookieManager.getInstance().acceptCookie();
            WebSettings webSettings = browser.getSettings();

            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.getAllowContentAccess();
            webSettings.setAppCacheEnabled(false);
            webSettings.setDisplayZoomControls(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webSettings.setLoadWithOverviewMode(true);
            browser.setBackgroundColor(0x229240);


            browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            browser.setKeepScreenOn(false);


            WebViewClientImpl webViewClient = new WebViewClientImpl(this);
            browser.setWebViewClient(webViewClient);


            // if (isConnectingToInternet(getApplicationContext())) {
            if (ConnectionHelper.isOnline()) {
                browser.loadUrl(getString(R.string.SiteURL));

                Intent CurrIntent = getIntent();
                if (CurrIntent.hasExtra("Type") && CurrIntent.hasExtra("Id")) {
                    Bundle extras = getIntent().getExtras();
                    int type = 0;
                    String id = "";
                    if (!extras.getString("Type").equals(null)) {
                        type = Integer.parseInt(extras.getString("Type"));
                    }
                    if (!extras.getString("Id").equals(null)) {
                        id = extras.getString("Id");
                    }
                    String url = getString(R.string.SiteURL);
                    switch (type) {
                        case 1:
                            url += "News/newsDetails.html#/?storyId=" + id;
                            break;
                        case 2:
                            url += "Events/eventsDetails.html#/?eventId=" + id;
                            break;
                    }
                    RefreshPage();
                    browser.loadUrl(url);
                }

                //check is service running or not as it starts at boot
                if (!NotificationsService.isRunning) {
                    try {
                        Intent intent = new Intent(this, NotificationsService.class);
                        startService(intent);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "no internet", Toast.LENGTH_LONG).show();
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                            System.exit(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        try {
            // Get preference value to know that is it first time application is
            // being called.
            appPref = getSharedPreferences("isFirstTime", 0);
            isFirstTime = appPref.getBoolean("isFirstTime", true);

            if (isFirstTime) {
                // Create explicit intent which will be used to call Our application
                // when some one clicked on short cut
                Intent shortcutIntent = new Intent(getApplicationContext(),
                        MainActivity.class);
                shortcutIntent.setAction(Intent.ACTION_MAIN);
                Intent intent = new Intent();

                // Create Implicit intent and assign Shortcut Application Name, Icon
                intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
                intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(
                                getApplicationContext(), R.mipmap.ic_launcher));
                intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                getApplicationContext().sendBroadcast(intent);

                // Set preference to inform that we have created shortcut on
                // Homescreen
                SharedPreferences.Editor editor = appPref.edit();
                editor.putBoolean("isFirstTime", false);
                editor.commit();
            }
            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void RefreshPage() {
        try {
            browser = (WebView) findViewById(R.id.webView);
            browser.reload();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        try {
            super.onPostCreate(savedInstanceState);
            hide();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onBackPressed() {
        browser = (WebView) findViewById(R.id.webView);
        if (browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        RefreshPage();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        RefreshPage();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        RefreshPage();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        RefreshPage();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        RefreshPage();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        RefreshPage();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        RefreshPage();
        return true;
    }

    private boolean isConnectingToInternet(Context applicationContext) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            Toast.makeText(getApplicationContext(), "no internet", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;

    }

    @Override
    public void onStart() {
        try {
            super.onStart();

            // ATTENTION: This was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client.connect();
            Action viewAction = Action.newAction(
                    Action.TYPE_VIEW, // TODO: choose an action type.
                    "Main Page", // TODO: Define a title for the content shown.
                    // TODO: If you have web page content that matches this app activity's content,
                    // make sure this auto-generated web page URL is correct.
                    // Otherwise, set the URL to null.
                    Uri.parse("http://host/path"),
                    // TODO: Make sure this auto-generated app deep link URI is correct.
                    Uri.parse("android-app://com.taha.alrehab/http/host/path")
            );
            AppIndex.AppIndexApi.start(client, viewAction);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.taha.alrehab/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class WebViewClientImpl extends WebViewClient {

        private Activity activity = null;

        public WebViewClientImpl(Activity activity) {
            this.activity = activity;

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (url.startsWith("tel:") || url.startsWith("sms:") || url.startsWith("smsto:") || url.startsWith("mms:") || url.startsWith("mmsto:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                webView.reload();
                return true;
            }
            if (url.contains(getString(R.string.SiteDomain))) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
            return true;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);


        }


    }
}
