package com.hollysmart.jsxtools;

import com.lqr.imagepicker.bean.ImageItem;

import java.util.List;

public class DsbridgeInterface {

    public interface OpenWXProgramIF{
        /**
         * 打开微信小程序
         * @param miniId 微信小程序的miniId
         */
        void openWXProgram(String miniId);
    }

    public interface ScanCodeIF{
        /**
         * 获取扫码结果
         * @param scanCodeIF
         */
        void onScanCode(DsbridgeCallBackIF<String> scanCodeIF);
    }


    public interface TakeAndChoosePhotoIF{
        /**
         * 获取相册、相机的图片路径
         * @param photoPathIF
         */
        void onPhotos(DsbridgeCallBackIF<List<ImageItem>> photoPathIF);
    }



}














