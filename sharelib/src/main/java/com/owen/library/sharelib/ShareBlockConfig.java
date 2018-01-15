package com.owen.library.sharelib;


public class ShareBlockConfig {

    private String mWeiXinAppId;
    private String mWeiXinSecret;
    private String mQQAppId;

    public String getWeiXinAppId() {
        return mWeiXinAppId;
    }

    public void setWeiXinAppId(String weiXinAppId) {
        mWeiXinAppId = weiXinAppId;
    }

    public String getWeiXinSecret() {
        return mWeiXinSecret;
    }

    public void setWeiXinSecret(String weiXinSecret) {
        mWeiXinSecret = weiXinSecret;
    }

    public String getQQAppId() {
        return mQQAppId;
    }

    public void setQQAppId(String QQAppId) {
        mQQAppId = QQAppId;
    }

    public static class Builder {

        private String mWeiXinAppId;
        private String mWeiXinSecret;
        private String mQQAppId;

        public Builder weiXin(String appId, String secret) {
            mWeiXinAppId = appId;
            mWeiXinSecret = secret;
            return this;
        }

        public Builder qq(String qqAppId) {
            mQQAppId = qqAppId;
            return this;
        }

        public ShareBlockConfig build() {
            ShareBlockConfig config = new ShareBlockConfig();
            config.setWeiXinAppId(mWeiXinAppId);
            config.setQQAppId(mQQAppId);
            config.setWeiXinSecret(mWeiXinSecret);
            return config;
        }
    }
}
