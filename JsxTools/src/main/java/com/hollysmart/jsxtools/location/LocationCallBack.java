package com.hollysmart.jsxtools.location;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.hollysmart.jsxtools.Mlog;
import com.hollysmart.jsxtools.dsbridge.CompletionHandler;

import java.util.HashMap;
import java.util.Map;

public class LocationCallBack implements LocationUtil.OnLocationFinish {
    private Context mContext;
    private String type;
    private CompletionHandler<String> handler;
    private GaoDeLatLng gaoDeLatLng;

    public LocationCallBack(Context mContext, String type, CompletionHandler<String> handler) {
        this.mContext = mContext;
        this.type = type;
        this.handler = handler;
        gaoDeLatLng = new GaoDeLatLng();
    }

    @Override
    public void onFinish(AMapLocation location, String city, String district, String x, String y) {
        if (location == null) {
            Mlog.d("未获取到定位权限");
            handler.complete("null-null");
            return;
        }
        Map<String, Object> args = new HashMap<>();
        args.put("address", location.getAddress());
        switch (type) {
            case "gps":
                double[] gpsLatlngs = gaoDeLatLng.GDToGPS(mContext, location.getLatitude(), location.getLongitude());
                args.put("latitude", gpsLatlngs[0]);
                args.put("longitude", gpsLatlngs[1]);
                break;
            case "baidu":
                double[] baiduLatlngs = gaoDeLatLng.GDToBaiDu(mContext, location.getLatitude(), location.getLongitude());
                args.put("latitude", baiduLatlngs[0]);
                args.put("longitude", baiduLatlngs[1]);
                break;
            default:
                args.put("latitude", location.getLatitude());
                args.put("longitude", location.getLongitude());
        }

        args.put("speed", location.getSpeed());
        args.put("accuracy", location.getAccuracy());
        args.put("altitude", location.getAltitude());

        Mlog.d(new Gson().toJson(args));

        handler.complete(new Gson().toJson(args));
    }
}