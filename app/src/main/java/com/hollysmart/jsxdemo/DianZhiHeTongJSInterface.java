package com.hollysmart.jsxdemo;

import android.webkit.JavascriptInterface;

import androidx.fragment.app.FragmentActivity;

import com.hollysmart.jsxtools.Mlog;

public class DianZhiHeTongJSInterface {
    public static final String NAMESPACE = "eContract";

    private FragmentActivity activity;

    public DianZhiHeTongJSInterface(FragmentActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void requestPermission() {
        Mlog.d("获取权限");
    }
}
