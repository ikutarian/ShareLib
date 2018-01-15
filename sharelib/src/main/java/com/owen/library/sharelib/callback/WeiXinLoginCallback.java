package com.owen.library.sharelib.callback;


import com.owen.library.sharelib.model.WeiXinUserInfo;

public interface WeiXinLoginCallback {
    void onSuccess(WeiXinUserInfo userInfo);
    void onCancel();
    void onError();
}
