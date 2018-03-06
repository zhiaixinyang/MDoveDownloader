package com.suapp.dcdownloader;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhaojing on 2018/3/6.
 */

public class App extends Application {
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return sAppContext;
    }
}
