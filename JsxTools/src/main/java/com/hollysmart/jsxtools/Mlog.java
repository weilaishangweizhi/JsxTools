package com.hollysmart.jsxtools;

import android.util.Log;

public class Mlog {
	public static boolean OPENLOG = true;
	public static String TAG = "JsxTools";

	public static void d(String log) {
		log(TAG, log);
	}

	public static void d(String TAG, String log) {
		log(TAG, log);
	}

	private static void log(String TAG, String log) {
		if (TAG == null) TAG = "";
		if (OPENLOG) Log.i(TAG, log);
	}
}
