package com.hollysmart.jsxtools.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

public class Utils {
    private Context mContext;

    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 调用系统界面，给指定的号码发送短信，并附带短信内容
     *
     * @param number 可以传“”，单个手机号，多个手机号（英文“,”拼接）
     * @param body
     */
    public void sendSmsWithBody(String number, String body) {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.setData(Uri.parse("smsto:" + number));
            sendIntent.putExtra("sms_body", body);
            mContext.startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(mContext, "无法唤起发送短信页面", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拨打电话
     *
     * @param phoneNum
     */
    public void makeCalls(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "无法唤起拨打电话页面", Toast.LENGTH_SHORT).show();
        }
    }

}
