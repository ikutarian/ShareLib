package com.owen.library.sharelib;


import android.app.Activity;
import android.content.Intent;

import com.owen.library.sharelib.activity.WeiXinHandlerActivity;
import com.owen.library.sharelib.callback.QQLoginCallback;
import com.owen.library.sharelib.callback.WeiXinLoginCallback;
import com.owen.library.sharelib.model.QQUserInfo;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

public class LoginManager {

    private static IUiListener sQQLoginCallBack;

    public static void loginWithWeiXin(Activity activity, WeiXinLoginCallback loginCallback) {
        WeiXinHandlerActivity.sLoginCallback = loginCallback;

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = String.valueOf(System.currentTimeMillis());

        IWXAPI api = WXAPIFactory.createWXAPI(activity, ShareBlock.getShareBlockConfig().getWeiXinAppId(), true);
        api.registerApp(ShareBlock.getShareBlockConfig().getWeiXinAppId());
        api.sendReq(req);
    }

    public static void loginWithQQ(final Activity activity, final QQLoginCallback loginCallback) {
        final Tencent tencent = Tencent.createInstance(ShareBlock.getShareBlockConfig().getQQAppId(),
                activity.getApplicationContext());

        sQQLoginCallBack = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                try {
                    JSONObject jsonResp = (JSONObject) response;

                    final String token = jsonResp.getString(Constants.PARAM_ACCESS_TOKEN);
                    final String expires = jsonResp.getString(Constants.PARAM_EXPIRES_IN);
                    final String openId = jsonResp.getString(Constants.PARAM_OPEN_ID);
                    tencent.setAccessToken(token, expires);
                    tencent.setOpenId(openId);

                    UserInfo userInfo = new UserInfo(activity, tencent.getQQToken());
                    userInfo.getUserInfo(new IUiListener() {
                        @Override
                        public void onComplete(Object resp) {
                            try {
                                JSONObject jsonObj = (JSONObject)resp;
                                String nickName = jsonObj.getString("nickname");
                                int sex = ("男".equals(jsonObj.getString("gender")) ? 1 : 0);
                                String headImageUrl = jsonObj.getString("figureurl_qq_2");

                                if (loginCallback != null) {
                                    loginCallback.onSuccess(new QQUserInfo(openId, nickName, sex, headImageUrl));
                                }
                            } catch (Exception e) {
                                if (loginCallback != null) {
                                    loginCallback.onError();
                                }
                            }
                        }

                        @Override
                        public void onError(UiError uiError) {
                            if (loginCallback != null) {
                                loginCallback.onError();
                            }
                        }

                        @Override
                        public void onCancel() {
                            if (loginCallback != null) {
                                loginCallback.onCancel();
                            }
                        }
                    });
                } catch (Exception e) {
                    if (loginCallback != null) {
                        loginCallback.onError();
                    }
                }
            }

            @Override
            public void onError(UiError error) {
                if (loginCallback != null) {
                    loginCallback.onError();
                }
            }

            @Override
            public void onCancel() {
                if (loginCallback != null) {
                    loginCallback.onCancel();
                }
            }
        };

        tencent.login(activity, "all", sQQLoginCallBack);
    }

    /**
     * 在调用QQ的API的Activity中的onActivityResult()方法，需要调用这个方法
     * 注意：方法 Tencent.onActivityResultData 不能调用超过两次。因此，在同一个页面中，不能即分享又登录
     */
    public static void onQQActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, sQQLoginCallBack);
    }
}
