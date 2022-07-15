package com.hollysmart.jsxdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.hollysmart.jsxtools.CaiJsApi;
import com.hollysmart.jsxtools.JsxInterface;
import com.hollysmart.jsxtools.Mlog;
import com.hollysmart.jsxtools.dsbridge.DWebView;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements JsxInterface.JSXCallBack {

    private static final int REQUEST_CODE_SACN_QRCODE = 999;
    private boolean isInit = true;
    private DWebView dwebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebview();
    }


    private void initWebview() {
        dwebView = findViewById(R.id.dweb);
        DWebView.setWebContentsDebuggingEnabled(true);

        WebSettings webSettings = dwebView.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        webSettings.setTextZoom(100);//设置字体占屏幕宽度

        webSettings.setSupportZoom(true);  //支持放大缩小
        webSettings.setBuiltInZoomControls(true);//支持放大缩小
        webSettings.setDisplayZoomControls(false);//
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 图片大小
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(2);
        }
        //设置支持DomStorage
        webSettings.setDomStorageEnabled(true);
        CaiJsApi caiJsApi = new CaiJsApi(this);
        dwebView.addJavascriptObject(caiJsApi, null);
        dwebView.setWebViewClient(webViewClient);
        dwebView.setWebChromeClient(webChromeClient);
        dwebView.loadUrl("file:///android_asset/js-call-native.html");
//        dwebView.loadUrl("http://test.hollysmart.com.cn:9001/jsxRt/#/");
        getLocPermission();
    }


    private void getLocPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String u) {
            return super.shouldOverrideUrlLoading(view, u);
        }
    };


    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private boolean mVideoFlag = false;
    private Uri mImageUri;

    private final static int FILECHOOSER_RESULTCODE = 10;// 表单的结果回调
    private static final int REQ_CAMERA = FILECHOOSER_RESULTCODE + 1;//拍照
    private final static int REQ_VIDEO = REQ_CAMERA + 1;// 录像
    private final static int REQ_PHOTO = REQ_VIDEO + 1;// 相册

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView webView, int i) {
            super.onProgressChanged(webView, i);
            if (i == 100 && isInit) {
                isInit = false;
            }
        }

        // For Android >= 5.0
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            Mlog.d("调用到了这里1111");
            mUploadCallbackAboveL = valueCallback;
            if (fileChooserParams.isCaptureEnabled()) {
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
//                LogUtil.d(TAG, "acceptTypes=" + acceptTypes);
                for (int i = 0; i < acceptTypes.length; i++) {
                    if (acceptTypes[i].contains("video")) {
                        mVideoFlag = true;
                        break;
                    }
                }
                if (mVideoFlag) {
                    recordVideo();
                    mVideoFlag = false;
                } else {
                    takePhoto();
                }
            } else {
                pickPhoto();
            }
            return true;
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
//            LogUtil.d(TAG, "openFileChooser1");
            Mlog.d("调用到了这里2222");
            mUploadMessage = uploadMsg;
            pickPhoto();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//            LogUtil.d(TAG, "openFileChooser2");
            Mlog.d("调用到了这里3333");
            mUploadMessage = uploadMsg;
            mVideoFlag = acceptType.contains("video");
            if (mVideoFlag) {
                recordVideo();
                mVideoFlag = false;
            } else {
                pickPhoto();
            }
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//            LogUtil.d(TAG, "openFileChooser3");
            Mlog.d("调用到了这里4444");
            mUploadMessage = uploadMsg;
            if (!TextUtils.isEmpty(capture)) {
                mVideoFlag = acceptType.contains("video");
                if (mVideoFlag) {
                    recordVideo();
                    mVideoFlag = false;
                } else {
                    takePhoto();
                }
            } else {
                pickPhoto();
            }
        }


        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
            Mlog.d("定位？");
        }
    };


    /**
     * 拍照
     */
    private void takePhoto() {
        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "images" + File.separator;
        File fileUri = new File(path, SystemClock.currentThreadTimeMillis() + ".jpg");
        if (!fileUri.getParentFile().exists()) {
            fileUri.getParentFile().mkdirs();
        }
        mImageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", fileUri);//通过FileProvider创建一个content类型的Uri
        }
//        LogUtil.d(TAG, "mImageUri=" + mImageUri);
        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intentCamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intentCamera, REQ_CAMERA);
    }


    /**
     * 录像
     */
    private void recordVideo() {
        String path = getExternalFilesDir(Environment.DIRECTORY_MOVIES) + File.separator + "video" + File.separator;
        File fileUri = new File(path, SystemClock.currentThreadTimeMillis() + ".mp4");
        if (!fileUri.getParentFile().exists()) {
            fileUri.getParentFile().mkdirs();
        }
        mImageUri = Uri.fromFile(fileUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mImageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", fileUri);//通过FileProvider创建一个content类型的Uri
        }
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);  // 表示跳转至相机的录视频界面
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);    // MediaStore.EXTRA_VIDEO_QUALITY 表示录制视频的质量，从 0-1，越大表示质量越好，同时视频也越大
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);    // 表示录制完后保存的录制，如果不写，则会保存到默认的路径，在onActivityResult()的回调，通过intent.getData中返回保存的路径
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);   // 设置视频录制的最长时间
        startActivityForResult(intent, REQ_VIDEO);  // 跳转
    }

    //激活相册操作
    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQ_PHOTO);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CaiJsApi.REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    List<ImageItem> resultList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    imagePathIF.getImagePath(resultList);
                }
                break;

            case REQ_CAMERA:
            case REQ_VIDEO:
            case REQ_PHOTO:
                if (null == mUploadMessage && null == mUploadCallbackAboveL)
                    return;
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data);
                } else if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
                break;
            case REQUEST_CODE_SACN_QRCODE:
                if (resultCode == REQUEST_CODE_SACN_QRCODE) {
                    String resultStr = data.getStringExtra("scanResult");
                    scanCodeIF.getScanCodeResult(resultStr);
                }
                break;

            case CaiJsApi.REQUEST_CODE_FOLDER:
                if (resultCode == RESULT_OK && data != null) {
                    Mlog.d("选择相册的返回");
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(MainActivity.this, uri);
                    selectFilePathIF.getFilePath(path);
                }
                break;
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{mImageUri};
            } else {
                String dataString = data.getDataString();
//                LogUtil.d(TAG, "onActivityResultAboveL===dataString=" + dataString);
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
        } else {
            results = new Uri[]{};
            mUploadCallbackAboveL.onReceiveValue(results);
        }
        mUploadCallbackAboveL = null;
    }


    public boolean checkPermission(String[] permissions) {
        boolean granted = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                granted = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
                if (!granted) {
                    break;
                }
            }
        }
        return granted;
    }


    /**
     * 获取相册、相机的图片路径
     *
     * @param imagePathIF
     */
    private JsxInterface.ImagePathIF imagePathIF;

    @Override
    public void setImagePathIF(JsxInterface.ImagePathIF imagePathIF) {
        this.imagePathIF = imagePathIF;
    }

    /**
     * 设置系统文件夹选取文件监听
     * @param selectFilePathIF
     */
    private JsxInterface.SelectFilePathIF selectFilePathIF;
    @Override
    public void setSelectFilePathIF(JsxInterface.SelectFilePathIF selectFilePathIF) {
        this.selectFilePathIF = selectFilePathIF;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CaiJsApi.REQUEST_CODE_FOLDER);
    }

    /**
     * 启动二维码扫描并返回扫描值
     *
     * @param scanCodeIF
     */
    private JsxInterface.ScanCodeIF scanCodeIF;

    @Override
    public void scanCode(JsxInterface.ScanCodeIF scanCodeIF) {
        this.scanCodeIF = scanCodeIF;
    }

    @Override
    public void uploadFile(Object json, JsxInterface.UploadFileIF uploadFileIF) {
    }

    /**
     * 点击了back键
     *
     * @param isCapture
     */
    private boolean captureOnBack = false;

    @Override
    public void onBack(boolean captureOnBack) {
        this.captureOnBack = captureOnBack;
    }

    @Override
    public void refreshPage() {
        Mlog.d("-----------刷新webview----------");
    }

    @Override
    public void openWXProgram(String miniId) {
    }

    @Override
    public void onBackPressed() {
        if (captureOnBack) {
            Mlog.d("启用了拦截系统back,调用了 JS 方法 ");
            dwebView.callHandler("onBack", new Object[]{});
        } else {
            if (dwebView.canGoBack()) {
                dwebView.goBack();// 返回前一个页面
            } else {
                super.onBackPressed();
            }
        }
    }
}