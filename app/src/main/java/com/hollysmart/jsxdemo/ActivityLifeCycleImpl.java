package com.hollysmart.jsxdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hollysmart.jsxtools.Mlog;


public class ActivityLifeCycleImpl implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ActivityLifeCycleImpl";
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Mlog.d(TAG, "onActivityCreated" + activity.getClass().getName());
        // 移除通知
        HijackingPrevent.getInstance().removeNotify();
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        Mlog.d(TAG, "onActivityResumed");
        // 移除通知
        HijackingPrevent.getInstance().removeNotify();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Mlog.d(TAG, "onActivityPaused");
        // 延时通知
        HijackingPrevent.getInstance().delayNotify(activity);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Mlog.d(TAG, "onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}