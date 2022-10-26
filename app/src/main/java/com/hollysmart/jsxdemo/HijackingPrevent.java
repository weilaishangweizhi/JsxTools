package com.hollysmart.jsxdemo;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author : HHotHeart
 * @date : 2021/8/24 20:43
 * @desc : 界面防劫持工具类，通过延时通知的发送和取消实现
 */
public class HijackingPrevent {

    public final static String sDES = "正在后台运行，请注意了！";

    /**
     * 退出APP的标识
     */
    private boolean isExit = false;
    /**
     * 延时事件
     */
    private Runnable runnable;
    /**
     * 延时事件发送和取消
     */
    private Handler handler;

    /**
     * 创建单例
     */
    private HijackingPrevent() {
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isExit()) {
                    isExit = false;
                    Toast.makeText(MyApp.getContext(), sDES, Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static HijackingPrevent getInstance() {
        return Holder.S_HIJACKING_PROVENT;
    }

    /**
     * Holder初始化单例
     */
    private static class Holder {
        private static final HijackingPrevent S_HIJACKING_PROVENT = new HijackingPrevent();
    }

    /**
     * 退出activity时，延时通知
     */
    public synchronized void delayNotify(Activity activity) {
        // 不需要通知，则返回
        if (!isNeedNotify(activity)) {
            return;
        }
        setExit(true);
        // 先移除已有的
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1);

    }

    /**
     * 进入当前app activity时，移除通知
     */
    public synchronized void removeNotify() {
        if (isExit()) {
            setExit(false);
            handler.removeCallbacks(runnable);
        }
    }

    /**
     * 判断是否需要通知Toast
     */
    public synchronized boolean isNeedNotify(Activity activity) {
        if (activity == null) {
            return false;
        }
        String actName = activity.getClass().getName();
        if (TextUtils.isEmpty(actName)) {
            return false;
        }
        //除了申请权限的activity，其它都需要延时通知
        return !actName.contains("UtilsTransActivity");
    }

    /**
     * 是否退出app
     *
     * @return
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * 设置app退出与否标识
     *
     * @param isExit
     */
    public void setExit(boolean isExit) {
        this.isExit = isExit;
    }
}