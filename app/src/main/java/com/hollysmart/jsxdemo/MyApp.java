package com.hollysmart.jsxdemo;

import android.app.Application;
import android.content.Context;

import com.hollysmart.jsxtools.JsxTools;

public class MyApp extends Application {

    private static Context mContext;//上下文
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        JsxTools.getInstance().setApplication(this).initFileUtil(this);
        registerActivityLifecycleCallbacks(new ActivityLifeCycleImpl());
    }
    public static Context getContext() {
        return mContext;
    }
}
