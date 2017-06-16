package com.kiwi.camera;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by jerikc on 16/4/14.
 */
public class KwApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
//        CrashReport.initCrashReport(getApplicationContext(), "32c53e1250", true);
    }
}
