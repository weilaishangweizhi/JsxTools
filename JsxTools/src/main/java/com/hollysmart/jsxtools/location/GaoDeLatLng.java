package com.hollysmart.jsxtools.location;

import android.content.Context;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;

public class GaoDeLatLng {
    /**
     * 将高德坐标转化成 GPS坐标
     * @param mContext
     * @param lat
     * @param lng
     * @return
     */
    public double[] GDToGPS(Context mContext, double lat, double lng) {
        // x = 2 * x1 - x2;
        DPoint LatLng1 = new DPoint( lat, lng ); //原高德坐标点
        CoordinateConverter converter = new CoordinateConverter( mContext );
        converter.from( CoordinateConverter.CoordType.GPS );
        try {
            converter.coord( LatLng1 );
            DPoint LatLng2 = converter.convert();
            double mlng = 2 * LatLng1.getLongitude() - LatLng2.getLongitude();
            double mlat = 2 * LatLng1.getLatitude() - LatLng2.getLatitude();
            double[] latlngs = {mlat, mlng};
            return latlngs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将高德坐标转化成 百度坐标
     * @param mContext
     * @param lat
     * @param lng
     * @return
     */
    public double[] GDToBaiDu(Context mContext, double lat, double lng){
        // x = 2 * x1 - x2;
        DPoint LatLng1 = new DPoint( lat, lng ); //原高德坐标点
        CoordinateConverter converter = new CoordinateConverter( mContext );
        converter.from( CoordinateConverter.CoordType.BAIDU );
        try {
            converter.coord( LatLng1 );
            DPoint LatLng2 = converter.convert();
            double mlng = 2 * LatLng1.getLongitude() - LatLng2.getLongitude();
            double mlat = 2 * LatLng1.getLatitude() - LatLng2.getLatitude();
            double[] latlngs = {mlat, mlng};
            return latlngs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
