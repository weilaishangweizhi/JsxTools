package com.hollysmart.jsxtools;

import android.app.Application;

import com.hollysmart.jsxtools.util.FileUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

public class JsxTools {
    private JsxTools() {
    }

    private static class JsxToolsInstance {
        private static final JsxTools INSTANCE = new JsxTools();
    }

    public static JsxTools getInstance() {
        return JsxToolsInstance.INSTANCE;
    }

    private Application application;
    public Application getApplication() {
        return application;
    }

    public JsxTools setApplication(Application application) {
        this.application = application;
        return getInstance();
    }


    public JsxTools initFileUtil(Application application){
        FileUtils.setAUTHORITY(application.getPackageName() + ".fileProvider");
        return getInstance();
    }

    public void initX5(Application application) {
        /* 设置允许移动网络下进行内核下载。默认不下载，会导致部分一直用移动网络的用户无法使用x5内核 */
        QbSdk.setDownloadWithoutWifi(true);
        /* SDK内核初始化周期回调，包括 下载、安装、加载 */
        QbSdk.setTbsListener(new TbsListener() {
            /**
             * @param stateCode 110: 表示当前服务器认为该环境下不需要下载；200下载成功
             */
            @Override
            public void onDownloadFinish(int stateCode) {
            }

            /**
             * @param i 200、232安装成功
             */
            @Override
            public void onInstallFinish(int i) {
            }

            /**
             * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
             * @param progress 0 - 100
             */
            @Override
            public void onDownloadProgress(int progress) {
                Mlog.d("Core Downloading: " + progress);
            }
        });

        /* 此过程包括X5内核的下载、预初始化，接入方不需要接管处理x5的初始化流程，希望无感接入 */
        QbSdk.initX5Environment(application, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖wifi网络下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * 内核下发请求发起有24小时间隔，卸载重装、调整系统时间24小时后都可重置
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {
            }
        });

    }


}
