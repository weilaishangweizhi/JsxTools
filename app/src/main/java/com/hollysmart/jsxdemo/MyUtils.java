package com.hollysmart.jsxdemo;

import android.app.Activity;
import android.widget.Toast;

public class MyUtils {
    /**
     * 显示toast
     */
    public static void showToast(final Activity ctx, final String msg) {
        // 判断是在子线程，还是主线程
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
        } else {
            // 子线程
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}