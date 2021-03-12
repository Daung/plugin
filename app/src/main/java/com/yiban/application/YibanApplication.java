package com.yiban.application;

import android.app.Application;
import android.content.Context;

public class YibanApplication extends Application {

    private static YibanApplication mApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public static Context getInstance() {
        return mApplication.getApplicationContext();
    }
}
