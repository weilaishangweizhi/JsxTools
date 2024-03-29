# JsxTools

## Gradle引用

```
allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

```
    implementation 'com.github.weilaishangweizhi:JsxTools:tag'//tag替换最新版本号
```

## 初始化

- **Application中配置**

        JsxTools.getInstance().init(this);

- **地图功能**

        需要在主工程中配置AndroidManifest

```
<!--用于进行网络定位-->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
<!--用于访问GPS定位-->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
<!--用于获取运营商信息，用于支持提供运营商信息相关的接口-->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
<!--用于访问wifi网络信息，wifi信息会用于进行网络定位-->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
<!--用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
<!--用于访问网络，网络定位需要上网-->
<uses-permission android:name="android.permission.INTERNET"></uses-permission>
<!--用于读取手机当前的状态-->
<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
<!--用于写入缓存数据到扩展存储卡-->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
<!--用于申请调用A-GPS模块-->
<uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"></uses-permission>
<!--如果设置了target >= 28 如果需要启动后台定位则必须声明这个权限-->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<!--如果您的应用需要后台定位权限，且有可能运行在Android Q设备上,并且设置了target>28，必须增加这个权限声明-->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION"/>


<meta-data android:name="com.amap.api.v2.apikey" 
    android:value="高德key"/>
<service android:name="com.amap.api.location.APSService"/>
```

- **拍照及相册功能**

      需要配置FileProvider

```
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileProvider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths"/>
</provider>
```

在res目录新建xml文件夹，创建provider_paths.xml文件如下：

```
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <root-path
        name="root"
        path="" />
    <external-path
        name="external_files"
        path="." />
    <cache-path
        name="cache-documents"
        path="documents" />

    <external-path name="beta_external_path" path="Download/"/>
    <!--/storage/emulated/0/Android/data/${applicationId}/files/apk/-->
    <external-path name="beta_external_files_path" path="Android/data/"/>
</paths>
```

## 页面引用

```
     dwebView.addJavascriptObject(dsbridgeJSAPI, null);
     private void initJSX(){
        dsbridgeJSAPI = new DsbridgeJSAPI(this);
        dsbridgeJSAPI.setScanCodeIF(new DsbridgeInterface.ScanCodeIF() {
            @Override
            public void onScanCode(DsbridgeCallBackIF<String> scanCodeIF) {
                Mlog.d("调用了扫一扫");
                scanCodeInterFace = scanCodeIF;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PermissionX.init(DsbridgeWebFragment.this)
                                .permissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                                .request(new RequestCallback() {
                                    @Override
                                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                                        if (allGranted){
                                            Intent intent = new Intent(getContext(), ScanCodeActivity.class);
                                            startActivityForResult(intent, REQUEST_CODE_SACN_QRCODE);
                                        }
                                    }
                                });
                    }
                });
            }
        });
        dsbridgeJSAPI.setUploadFileIF(new DsbridgeInterface.UploadFileIF() {
            @Override
            public void onResult(DsbridgeCallBackIF<String> uploadFileCallBack) {
                    uploadFileCallBack.onResult(200, "测试通过");
            }
        });
        dsbridgeJSAPI.setCaptureBackIF(new DsbridgeInterface.CaptureBackIF() {
            @Override
            public void onBack(boolean captureOnBack) {
                onBack = captureOnBack;
            }
        });
        dsbridgeJSAPI.setRefreshPage(new DsbridgeInterface.RefreshPage() {
            @Override
            public void refreshPage() {
                dwebView.reload();
                Toast.makeText(getContext(), "刷新了页面", Toast.LENGTH_SHORT).show();
            }
        });

    }
```

```
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Mlog.d("fragment中的onActivityResult");
        //需要在中实现此方法   必写
        dsbridgeJSAPI.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SACN_QRCODE:
                if (resultCode == REQUEST_CODE_SACN_QRCODE){
                    String resultStr = data.getStringExtra("scanResult");
                    scanCodeInterFace.onResult(DsbridgeCallBackIF.CALLBACK_SUCCEED, resultStr);
                }else {
                    scanCodeInterFace.onResult(DsbridgeCallBackIF.CALLBACK_CANCEL, null);
                }
                break;
        }
    }
```

## 功能介绍（新）

参考JS原生功能.docx文档

## 功能介绍（旧）

- **js调用说明参考**：

[DSBridge-Android/readme-chs.md at master · wendux/DSBridge-Android · GitHub](https://github.com/wendux/DSBridge-Android/blob/master/readme-chs.md)

- **二维码扫描**

        方法名：scanCode

        方法类型：异步Api

        参数： 无

        返回：String  

- **打开本地相机**

        方法名：takePhote

        方法类型：异步Api

        参数： 无

        返回： {"tempImagePath":"文件路径"}  

- **打开本地相册**

        方法名：chooseImage

        方法类型：异步Api

        参数： 无

返回：{"tempFilePaths":"文件路径"}

- **文件上传**

      方法名：uploadFile

      方法类型：异步Api

      参数：{
     "formData":{
         "id":"WU_FILE_6551382593836148",
         "name":"6551382593836148.jpg",
         "type":"image/jpeg",
         "lastModifiedDate":"2020-06-28T08:19:16.103Z",
         "size":42784
     },
     "timeOut":300000,
     "url":"http://test.hollysmart.com.cn:9001/sztran/net/controller.ashx?action=uploadimage&encode=utf-8",
     "filePath":"/storage/emulated/0/DCIM/Camera/IMG_20200628_102446.jpg",
     "name":"upfile"
 }

    返回： {"code":0, "data":"上传接口返回值"}  

- **发送短信**
  
  方法名：sharedSms
  
  方法类型：同步Api
  
  参数： {"number":"123456","msg_content":"内容"}  
  
  返回：无

- **拨打电话**
  
  方法名：makeCalls
  
  方法类型：同步Api
  
  参数： {"phoneNum":"123456"}  
  
  返回：无

- **关闭窗口**
  
  方法名：closeWindow
  
  方法类型：同步Api
  
  参数： 无
  
  返回：无

- **捕获系统back键**
  
  方法名：onBack
  
  方法类型：同步Api
  
  参数： 无
  
  返回：无
  
  说明：调用捕获Back键后，back事件由h5处理。h5需要提供onBack方法供原生系统调用。

- **停止捕获系统back键**
  
  方法名：onBack
  
  方法类型：同步Api
  
  参数： 无
  
  返回：无

- **重新加载页面**
  
  方法名：refreshPage
  
  方法类型：同步Api
  
  参数： 无
  
  返回：无

- **下载资源到手机（系统浏览器下载）**
  
  方法名：downloadFile
  
  方法类型：同步Api
  
  参数： {"filePath":""}
  
  返回：无

- **添加系统联系人**
  
  方法名：addContact
  
  方法类型：同步Api
  
  参数：{"name":"","phoneNumber":"" }
  
  返回：无

- **保存数据**
  
  方法名：saveJsonStr
  
  方法类型：同步Api
  
  参数：{"key":"","value":"" }
  
  返回：boolean

- **获取数据**
  
  方法名：getJsonStr
  
  方法类型：同步Api
  
  参数：String  key
  
  返回：String  value

- **删除数据**
  
  方法名：removeJsonStr
  
  方法类型：同步Api
  
  参数：String  key
  
  返回：无

- **获取定位信息**
  
  方法名：getLocation
  
  方法类型：异步Api
  
  参数：{"type":"gps","isHigtAccuracy":true,"timeOut":3000 }
  
  Type: gaode、baidu、gps  默认gaode
  
  返回：{"altitude":0.0,"address":"北京市海淀区万柳东路9号靠近至高美术馆","latitude":39.959591959178155,"accuracy":29.0,"speed":0.0,"longitude":116.29647260239173}

- **打开系统文件夹**
  
  方法名：openFolder
  
  方法类型：异步Api
  
  参数：无
  
  返回：{”filePath“：”文件地址“}
