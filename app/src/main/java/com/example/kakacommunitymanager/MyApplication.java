package com.example.kakacommunitymanager;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        init();

    }

    private void init() {
        context = getApplicationContext();  //全局获取Context
    }

    public static Context getContext() {
        return context;
    }
}
