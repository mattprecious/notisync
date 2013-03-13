
package com.mattprecious.notisync;

import com.google.analytics.tracking.android.GoogleAnalytics;

import org.holoeverywhere.app.Application;

public class MyApplication extends Application {

    public MyApplication() {
        GoogleAnalytics.getInstance(this).setDebug(BuildConfig.DEBUG);
    }
}
