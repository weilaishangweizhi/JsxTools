# JsxTools
jsx工具

## 地图功能
需要在主工程中配置AndroidManifest
'''
<uses-permission android:name="android.permission.INTERNET" />

<meta-data android:name="com.amap.api.v2.apikey" android:value="d59f4bbe7c0144df356ed9e8b3d5441b"/>
<service android:name="com.amap.api.location.APSService"/>
'''
## 扫一扫功能

需要在主工程中配置AndroidManifest
'''
<activity android:name="com.hollysmart.zxingqrcodemodule.ScanCodeActivity" />
<activity
    android:name="com.hollysmart.zxingqrcodemodule.GenerateCodeActivity"
    android:screenOrientation="portrait" />
'''
