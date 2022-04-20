package com.hollysmart.jsxtools.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hollysmart.jsxtools.Mlog;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

/**
 * @Title: LocationUtil.java
 * @Description: 全局定位工具类   onlocationfinish  定位成功回调接口
 * @author: cai
 * @data: 2020年6月4日
 * @version: V1.0
 */
public class LocationUtil implements AMapLocationListener {
    public AMapLocationClient locationClient = null;
    private OnLocationFinish locationFinish;

    private String address;   //详细地址
    private String district;   //区域信息（区）
    private String city;  //市
    private String province;  //省
    private String road;  //路
    private String street;  //街道
    private String aoiName;  //poi 名称
    private String y;  //longitude经度
    private String x;  //latitude纬度

    public LocationUtil(Context context, Activity activity, boolean isHighAccuracy, long timeOut ) {
        //初始化client
        try {
            if (locationClient == null){
                AMapLocationClient.updatePrivacyShow(context, true, true);
                AMapLocationClient.updatePrivacyAgree(context, true);
                locationClient = new AMapLocationClient(context);
                // 设置定位监听
                locationClient.setLocationListener(this);
            }
            //设置定位参数
            locationClient.setLocationOption(getDefaultOption(isHighAccuracy, timeOut));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PermissionX.init((FragmentActivity) activity)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted) {
                            startLocation();
                        } else {
                            if (locationFinish != null){
                                locationFinish.onFinish(null, null, null, null, null);
                            }
                        }
                    }
                });
    }

    //提供给掉用者
    public void setLocationListener(OnLocationFinish listener) {
        if (listener != null) {
            locationFinish = listener;
        }
    }



    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            //解析定位结果
            String result = LocUtil.getLocationStr(loc);
            address = loc.getAddress();
            province = loc.getProvince();
            district = loc.getDistrict();
            city = loc.getCity();
            road = loc.getRoad();
            street = loc.getStreet();
            aoiName = loc.getAoiName();
            y = String.valueOf(loc.getLatitude());
            x = String.valueOf(loc.getLongitude());
            if (address != null && address.length() > 0) {
                locationFinish.onFinish(loc, city, district, x, y);
                closeLocation();
            }
        } else {//失败
            Mlog.d("定位失败");
            loc.setProvince("");
            loc.setAdCode("");
            loc.setCity("");
            locationFinish.onFinish(loc, "", "", "", "");
        }
    }


    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */

    private AMapLocationClientOption getDefaultOption(boolean isHighAccuracy, long timeOut) {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        if (isHighAccuracy){
            //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            mOption.setLocationMode( AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度模式
            mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        }else{
            mOption.setLocationMode( AMapLocationClientOption.AMapLocationMode.Battery_Saving);//低功耗定位模式
        }
        if (timeOut == 0 ){
            mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        }else {
            mOption.setHttpTimeOut(timeOut);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        }
//        mOption.setInterval(20000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol( AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(false); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }


    public void startLocation() {
        locationClient.startLocation();
    }

    //定位成功后掉用   销毁定位服务

    public void closeLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    public interface OnLocationFinish {
        void onFinish(AMapLocation location, String city, String district, String x, String y);
    }
}
