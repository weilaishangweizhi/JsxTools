package com.hollysmart.jsxtools;
public class DsbridgeInterface {

    public interface ScanCodeIF{
        void onScanCode(DsbridgeCallBackIF<String> scanCodeIF);
    }

    public interface UploadFileIF{
        void onResult(DsbridgeCallBackIF<String> uploadFileCallBack);
    }

    public interface CaptureBackIF{
        void onBack(boolean captureOnBack);
    }

    public interface RefreshPage{
        void refreshPage();
    }

}














