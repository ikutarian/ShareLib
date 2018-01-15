package com.owen.library.sharelib.callback;


import com.owen.library.sharelib.model.QQUserInfo;

public interface QQLoginCallback {
    void onSuccess(QQUserInfo userInfo);
    void onCancel();
    void onError();
}
