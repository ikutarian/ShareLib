package com.owen.library.sharelib.callback;


public interface ShareCallback {
    void onSuccess();
    void onCancel();
    void onError(int errorCode, String msg);
}
