package com.hollysmart.jsxtools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.google.gson.Gson;
import com.hollysmart.jsxtools.dsbridge.CompletionHandler;
import com.hollysmart.jsxtools.location.GaoDeLatLng;
import com.hollysmart.jsxtools.location.LocationCallBack;
import com.hollysmart.jsxtools.location.LocationUtil;
import com.hollysmart.jsxtools.util.ACache;
import com.hollysmart.jsxtools.util.FileUtils;
import com.hollysmart.jsxtools.util.Utils;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

public class DsbridgeJSAPI {
    public static final int REQUEST_CODE_PHOTO = 1000;   //拍照
    public static final int REQUEST_CODE_IMAGE = 1001;   //相册回调
    public static final int REQUEST_CODE_FOLDER = 1002; //选取文件回调

    private Utils utils;

    private Activity activity;
    private Fragment fragment;

    public DsbridgeJSAPI(Activity activity) {
        this.activity = activity;
    }

    public DsbridgeJSAPI(Fragment fragment) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
    }


    private DsbridgeCallBackIF<List<ImageItem>> takePhotoCallBack; //拍照
    private DsbridgeCallBackIF<List<ImageItem>> chooseImageCallBack; //选择照片
    private DsbridgeCallBackIF<String> openFoderCallBack; //选择文件

    //需要在页面中根据需求设置回调
    private DsbridgeInterface.ScanCodeIF scanCodeIF; //扫描二维码
    private DsbridgeInterface.UploadFileIF uploadFileIF; //文件上传
    private DsbridgeInterface.CaptureBackIF captureBackIF; //捕获back键
    private DsbridgeInterface.RefreshPage refreshPage;//刷新页面

    public void setScanCodeIF(DsbridgeInterface.ScanCodeIF scanCodeIF) {
        this.scanCodeIF = scanCodeIF;
    }
    public void setUploadFileIF(DsbridgeInterface.UploadFileIF uploadFileIF) {
        this.uploadFileIF = uploadFileIF;
    }
    public void setCaptureBackIF(DsbridgeInterface.CaptureBackIF captureBackIF) {
        this.captureBackIF = captureBackIF;
    }
    public void setRefreshPage(DsbridgeInterface.RefreshPage refreshPage) {
        this.refreshPage = refreshPage;
    }

    /**
     * 1.二维码扫描
     *
     * @param json    null
     * @param handler {"code":200, "result":"扫描内容"}
     */
    @JavascriptInterface
    public void scanCode(Object json, final CompletionHandler<String> handler) {
        Mlog.d("调用了扫一扫页面");
        if (scanCodeIF != null) {
            scanCodeIF.onScanCode((code, s) -> {
                Map<String, Object> args = new HashMap<>();
                args.put("result", s);
                args.put("code", code);
                handler.complete(new Gson().toJson(args));
            });
        }
    }

    /**
     * 2.打开本地相机  异步API
     *
     * @param json
     * @param handler
     */
    @JavascriptInterface
    public void takePhoto(Object json, final CompletionHandler<String> handler) {
        Mlog.d("调用了相机接口");
        takePhotoCallBack = (code, imageItems) -> {
            String paths = imageItems.remove(0).path;
            Map<String, Object> args = new HashMap<>();
            args.put("tempImagePath", paths);
            args.put("code", code);
            Mlog.d("调用了相机接口 " + new Gson().toJson(args));
            handler.complete(new Gson().toJson(args));
        };

        activity.runOnUiThread(() -> {
            if (fragment == null) {
                ImagePicker.picker().cameraPick(activity, REQUEST_CODE_PHOTO);
            } else {
                ImagePicker.picker().cameraPick(fragment, REQUEST_CODE_PHOTO);
            }
        });
    }

    /**
     * 3.打开本地相册包含相机  异步API
     *
     * @param json  {"count": 9}    count：最大选择图片数量
     * @param handler
     */
    @JavascriptInterface
    public void chooseImage(Object json, final CompletionHandler<String> handler) {
        Mlog.d("调用了相册接口：" + json);
        try {
            JSONObject jsonObject = new JSONObject(valueOf(json));
            int count = 9;
            if (jsonObject.has("count")) {
                count = jsonObject.getInt("count");
            }

            chooseImageCallBack = (code, imageItems) -> {
                Map<String, Object> args = new HashMap<>();
                List<String> paths = new ArrayList<>();
                if (imageItems != null && imageItems.size() > 0) {
                    for (ImageItem bean : imageItems) {
                        paths.add(bean.path);
                    }
                }
                args.put("code", code);
                args.put("tempFilePaths", paths);
                Mlog.d("调用了相册接口 " + new Gson().toJson(args));
                handler.complete(new Gson().toJson(args));
            };
            int finalCount = count;
            activity.runOnUiThread(() -> {
                if (fragment == null) {
                    ImagePicker.picker().enableMultiMode(finalCount).showCamera(true)
                            .pick(activity, REQUEST_CODE_IMAGE);
                } else {
                    ImagePicker.picker().enableMultiMode(finalCount).showCamera(true)
                            .pick(fragment, REQUEST_CODE_IMAGE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 4.打开系统文件夹功能
     *
     * @param json
     * @param handler
     */
    @JavascriptInterface
    public void openFolder(Object json, final CompletionHandler<String> handler) {
        Mlog.d("调用了打开系统文件夹功能");
        activity.runOnUiThread(() -> {
            openFoderCallBack = (code, s) -> {
                Map<String, Object> args = new HashMap<>();
                args.put("filePath", s);
                args.put("code", code);
                handler.complete(new Gson().toJson(args));
            };
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if (fragment == null) {
                activity.startActivityForResult(intent, REQUEST_CODE_FOLDER);
            } else {
                fragment.startActivityForResult(intent, REQUEST_CODE_FOLDER);
            }
        });
    }

    /**
     * 5.上传文件  异步API
     * 此接口参数需要根据项目组的情况自行定义参数
     *
     * @param json
     * @param handler
     */
    @JavascriptInterface
    public void uploadFile(Object json, final CompletionHandler<String> handler) {
        Mlog.d("调用了上传文件json:" + json);
        if (uploadFileIF != null) {
            uploadFileIF.onResult(new DsbridgeCallBackIF<String>() {
                @Override
                public void onResult(int code, String s) {
                    Map<String, Object> args = new HashMap<>();
                    args.put("data", s);
                    args.put("code", code);
                    handler.complete(new Gson().toJson(args));
                }
            });
        }

    }

    /**
     * 6.分享到短信  异步API
     *
     * @param json {"number":"123456","msg_content":"内容"}
     */
    @JavascriptInterface
    public void sharedSms(Object json) {
        Mlog.d("调用了分享到短信");
        Mlog.d("js 传过来的 json" + json);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(valueOf(json));
                    String msg_content = "";
                    String number = "";
                    if (jsonObject.has("msg_content")) {
                        msg_content = jsonObject.getString("msg_content");
                    }
                    if (jsonObject.has("number")) {
                        number = jsonObject.getString("number");
                    }
                    if (utils == null){
                        utils = new Utils(activity);
                    }
                    utils.sendSmsWithBody(number, msg_content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 7.拨打电话
     * @param json  {"phoneNum":"123456"}
     */
    @JavascriptInterface
    public void makeCalls(Object json) {
        Mlog.d("调用了拨打电话");
        Mlog.d("js 传过来的 json" + json);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(valueOf(json));
                    String phoneNum = "";
                    if (jsonObject.has("phoneNum")) {
                        phoneNum = jsonObject.getString("phoneNum");
                    }
                    if (utils == null){
                        utils = new Utils(activity);
                    }
                    utils.makeCalls(phoneNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 8.关闭窗口
     *
     * @param json
     */
    @JavascriptInterface
    public String closeWindow(Object json) {
        Mlog.d("调用了关闭窗口:" + json);
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                }
            });
        }
        return "调用成功";
    }

    /**
     * 9.捕获Back键
     *
     * @param json
     */
    @JavascriptInterface
    public void onBack(Object json) {
        Mlog.d("捕获Back键");
        captureBackIF.onBack(true);
    }

    /**
     * 10.停止捕获Back键监听
     *
     * @param json
     */
    @JavascriptInterface
    public void stopOnBack(Object json) {
        Mlog.d("调用了 停止捕获Back键监听");
        captureBackIF.onBack(false);
    }


    /**
     * 11.重新加载页面
     *
     * @param json
     */
    @JavascriptInterface
    public void refreshPage(Object json) {
        Mlog.d("调用了 重新加载页面");
        refreshPage.refreshPage();
    }

    /***
     * 12.从网络资源下载到手机
     */
    @JavascriptInterface
    public void downloadFile(Object msg) {
        Mlog.d("downloadFile");
        String filePath = "";
        try {
            JSONObject object = new JSONObject(msg.toString());
            filePath = object.getString("filePath");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(activity.getApplicationContext(), "文件路径为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        Mlog.d("文件路径========" + filePath);
        Uri uri = Uri.parse(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }

    /**
     * 13.添加联系人
     *
     * @param msg {"name":"", "phoneNumber":""}
     */
    @JavascriptInterface
    public void addContact(Object msg) {
        Mlog.d("添加系统联系人" + msg);
        String name = "";
        String phoneNumber = "";
        try {
            JSONObject object = new JSONObject(msg.toString());
            name = object.getString("name");
            phoneNumber = object.getString("phoneNumber");
            Intent addIntent = new Intent(Intent.ACTION_INSERT,
                    Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
            addIntent.setType("vnd.android.cursor.dir/person");
            addIntent.setType("vnd.android.cursor.dir/contact");
            addIntent.setType("vnd.android.cursor.dir/raw_contact");
            addIntent.putExtra(ContactsContract.Intents.Insert.NAME, name); //名称：
            addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);// 电话：
            activity.startActivity(addIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 14.保存数据
     *
     * @param json {"key":"","value":""}
     * @return
     */
    @JavascriptInterface
    public boolean saveJsonStr(Object json) {
        Mlog.d("保存数据：" + json);
        Map<String, String> args = new Gson().fromJson(valueOf(json), Map.class);
        ACache.get(activity.getApplicationContext()).put(args.get("key"), args.get("value"));
        return true;
    }

    /**
     * 15.获取数据
     *
     * @param key
     */
    @JavascriptInterface
    public String getJsonStr(Object key) {
        String content = ACache.get(activity.getApplicationContext()).getAsString(valueOf(key));
        Mlog.d("获取数据：" + key + "+" + content);
        return content;
    }

    /**
     * 16.删除数据
     *
     * @param key
     */
    @JavascriptInterface
    public void removeJsonStr(Object key) {
        Mlog.d("删除数据：" + key);
        ACache.get(activity.getApplicationContext()).remove(valueOf(key));
    }


    /**
     * 17.获取定位信息   异步API
     *
     * @param json {"type":"gps","isHighAccuracy":true,"timeOut":3000}
     * @param handler {"altitude":0.0,"address":"北京市海淀区万柳东路9号靠近至高美术馆","latitude":39.959591959178155,"accuracy":29.0,"speed":0.0,"longitude":116.29647260239173}
     */
    @JavascriptInterface
    public void getLocation(Object json, final CompletionHandler<String> handler) {
        Mlog.d("获取定位信息 异步API" + json);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONObject jsonObject = new JSONObject(valueOf(json));
                    boolean isHigh = true;
                    long timeOut = 0;
                    if (jsonObject.has("isHighAccuracy")) {
                        isHigh = jsonObject.getBoolean("isHighAccuracy");
                    }
                    if (jsonObject.has("timeOut")) {
                        timeOut = jsonObject.getLong("timeOut");
                    }
                    String type = "";
                    if (jsonObject.has("type"))
                        type = jsonObject.getString("type");
                    LocationUtil locationUtil = new LocationUtil(activity, isHigh, timeOut);
                    locationUtil.setLocationListener(new LocationCallBack(activity, type, handler));
                } catch (JSONException e) {
                    handler.complete("请确认参数是否正确（例：{\"isHighAccuracy\":true,\"timeOut\":3000}）");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 18.设置防截屏
     * @param onoff  true 开启防截屏  false关闭防截屏
     */
    @JavascriptInterface
    public void setScreenShot(Object onoff){
        Mlog.d("调用了设置截图开关：" + onoff);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Boolean.valueOf(onoff.toString())){
                    Mlog.d("开启防截屏");
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                }else {
                    Mlog.d("关闭防截屏");
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                }
            }
        });
    }




    /**
     * 需要在activity或fragment的onActivityResult中调用次方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DsbridgeJSAPI.REQUEST_CODE_PHOTO:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    List<ImageItem> resultList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    takePhotoCallBack.onResult(DsbridgeCallBackIF.CALLBACK_SUCCEED, resultList);
                } else {
                    takePhotoCallBack.onResult(DsbridgeCallBackIF.CALLBACK_CANCEL, null);
                }
                break;
            case DsbridgeJSAPI.REQUEST_CODE_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    List<ImageItem> resultList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    chooseImageCallBack.onResult(DsbridgeCallBackIF.CALLBACK_SUCCEED, resultList);
                } else {
                    chooseImageCallBack.onResult(DsbridgeCallBackIF.CALLBACK_CANCEL, null);
                }
                break;
            case DsbridgeJSAPI.REQUEST_CODE_FOLDER:
                if (requestCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(activity, uri);
                    openFoderCallBack.onResult(DsbridgeCallBackIF.CALLBACK_SUCCEED, path);
                } else {
                    openFoderCallBack.onResult(DsbridgeCallBackIF.CALLBACK_CANCEL, null);
                }
                break;

        }
    }
}














