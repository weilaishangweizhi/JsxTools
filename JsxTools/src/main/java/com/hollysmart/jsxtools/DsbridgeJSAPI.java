package com.hollysmart.jsxtools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.hollysmart.jsxtools.dsbridge.CompletionHandler;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;

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

    private Context mContext;
    private Activity activity;
    private Fragment fragment;

    private DsbridgeInterface.OpenWXProgramIF openWXProgramIF; //打开微信小程序
    private DsbridgeInterface.ScanCodeIF scanCodeIF; //扫描二维码
    private DsbridgeCallBackIF<List<ImageItem>> takePhotoCallBack; //拍照
    private DsbridgeCallBackIF<List<ImageItem>> chooseImageCallBack; //选择照片

    public DsbridgeJSAPI(Activity activity) {
        this.mContext = activity;
        this.activity = activity;
    }

    public DsbridgeJSAPI(Fragment fragment) {
        this.mContext = fragment.getContext();
        this.fragment = fragment;
    }

    /**
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

        }
    }

    /**
     * 打开微信小程序
     *
     * @param id 微信小程序的miniId
     * @return
     */
    @JavascriptInterface
    public String openWXProgram(Object id) {
        Mlog.d("打开微信小程序：" + id);
        if (openWXProgramIF != null)
            openWXProgramIF.openWXProgram(String.valueOf(id));
        return "调用成功";
    }

    public void setOpenWXProgramIF(DsbridgeInterface.OpenWXProgramIF openWXProgramIF) {
        this.openWXProgramIF = openWXProgramIF;
    }


    /**
     * 二维码扫描
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

    public void setScanCodeIF(DsbridgeInterface.ScanCodeIF scanCodeIF) {
        this.scanCodeIF = scanCodeIF;
    }

    /**
     * 打开本地相机  异步API
     *
     * @param json
     * @param handler
     */
    @JavascriptInterface
    public void takePhoto(Object json, final CompletionHandler<String> handler) {
        Mlog.d("调用了相机接口");
        takePhotoCallBack = new DsbridgeCallBackIF<List<ImageItem>>() {
            @Override
            public void onResult(int code, List<ImageItem> imageItems) {
                String paths = imageItems.remove(0).path;
                Map<String, Object> args = new HashMap<>();
                args.put("tempImagePath", paths);
                args.put("code", code);
                Mlog.d("调用了相机接口 " + new Gson().toJson(args));
                handler.complete(new Gson().toJson(args));
            }
        };
        if (fragment != null) {
            ImagePicker.picker().cameraPick(fragment, REQUEST_CODE_PHOTO);
        } else {
            ImagePicker.picker().cameraPick(activity, REQUEST_CODE_PHOTO);
        }
    }


    /**
     * 打开本地相册包含相机  异步API
     *
     * @param json
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

            chooseImageCallBack = new DsbridgeCallBackIF<List<ImageItem>>() {
                @Override
                public void onResult(int code, List<ImageItem> imageItems) {
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
                }
            };
            if (fragment == null) {
                ImagePicker.picker().enableMultiMode(count).showCamera(true)
                        .pick(activity, REQUEST_CODE_IMAGE);
            } else {
                ImagePicker.picker().enableMultiMode(count).showCamera(true)
                        .pick(fragment, REQUEST_CODE_IMAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}














