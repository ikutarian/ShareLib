package com.yuejia.student;

import android.app.Application;

import com.owen.library.sharelib.ShareBlock;
import com.owen.library.sharelib.ShareBlockConfig;

public class AppApplication extends Application {

    private static final String WX_APP_ID = "123456";
    private static final String WX_SECRET = "123456";
    private static final String QQ_APP_ID = "123456";

    @Override
    public void onCreate() {
        super.onCreate();

        ShareBlock.init(new ShareBlockConfig.Builder()
                        .weiXin(WX_APP_ID, WX_SECRET)
                        .qq(QQ_APP_ID)
                        .build());
    }
}
