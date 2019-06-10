package com.rockzhang.simplewebview;

import android.app.Application;
import android.content.Context;


public class BrowserApp extends Application {
    private static Context mAppContext;
    private static BrowserApp m_singleInstance;

    public static BrowserApp getInstance()
    {
        return m_singleInstance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        m_singleInstance = this;
        mAppContext = m_singleInstance;

    }

    public Context getApplicationContext()
    {
        return mAppContext;
    }

}
