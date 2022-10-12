package com.hollysmart.jsxtools;

public interface DsbridgeCallBackIF<T> {
    public static final int CALLBACK_SUCCEED = 200;
    public static final int CALLBACK_CANCEL = -100;
    public static final int CALLBACK_FAILED = -101;

    void onResult(int code, T t);
}