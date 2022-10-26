package com.hollysmart.jsxdemo;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hollysmart.jsxtools.DsbridgeCallBackIF;
import com.hollysmart.jsxtools.DsbridgeInterface;
import com.hollysmart.jsxtools.DsbridgeJSAPI;
import com.hollysmart.jsxtools.Mlog;
import com.hollysmart.jsxtools.dsbridge.DWebView;
import com.hollysmart.zxingqrcodemodule.ScanCodeActivity;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

public class DsbridgeWebFragment extends Fragment {
    private DWebView dwebView;
    private DsbridgeJSAPI dsbridgeJSAPI;
    private final int REQUEST_CODE_SACN_QRCODE = 999;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dsbridge, container, false);
        initWebview(view);
        return view;
    }

    private void initWebview(View view) {
        dwebView = view.findViewById(R.id.dweb);
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
        initJSX();
        dwebView.addJavascriptObject(dsbridgeJSAPI, null);
        dwebView.setWebViewClient(webViewClient);
        dwebView.setWebChromeClient(webChromeClient);
        dwebView.loadUrl("file:///android_asset/js-call-native.html");
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    };

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    };

    private DsbridgeCallBackIF<String> scanCodeInterFace;
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
}
