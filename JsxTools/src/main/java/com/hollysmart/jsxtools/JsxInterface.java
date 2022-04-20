package com.hollysmart.jsxtools;

import com.lqr.imagepicker.bean.ImageItem;

import java.util.List;

public class JsxInterface {
    public interface JSXCallBack {

        /**
         * 获取相册、相机的图片路径
         *
         * @param imagePathIF
         */
        void setImagePathIF(ImagePathIF imagePathIF);

        /**
         * 获取扫码结果
         *
         * @param scanCodeIF
         */
        void scanCode(ScanCodeIF scanCodeIF);


        /**
         * 上传文件，获取上传进度及上传结果
         *
         * @param json
         * @param uploadFileIF
         */
        void uploadFile(Object json, UploadFileIF uploadFileIF);


        /**
         * 点击了back键
         */
        void onBack(boolean captureOnBack);

        /**
         * 刷新界面
         */
        void refreshPage();

    }


    /**
     * 获取相册、相机的图片路径
     */
    public interface ImagePathIF {
        void getImagePath(List<ImageItem> datas);
    }

    /**
     * 获取扫码结果
     */
    public interface ScanCodeIF {
        void getScanCodeResult(String result);
    }


    /**
     * 上传文件，获取上传进度及上传结果
     */
    public interface UploadFileIF {
        void getProgressData(int progressData);

        void getResult(boolean isOk, String result);
    }

    /**
     * 上传文件，获取上传进度及上传结果
     */
    public interface UpLoadImageIF {
        void getImageRemoteUrl(String remoteUrl);
    }


}
