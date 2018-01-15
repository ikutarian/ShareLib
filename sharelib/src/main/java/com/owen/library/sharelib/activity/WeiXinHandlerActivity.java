package com.owen.library.sharelib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.owen.library.sharelib.ShareBlock;
import com.owen.library.sharelib.api.WeiXinApiService;
import com.owen.library.sharelib.callback.ShareCallback;
import com.owen.library.sharelib.callback.WeiXinLoginCallback;
import com.owen.library.sharelib.model.WeiXinAccessToken;
import com.owen.library.sharelib.model.WeiXinUserInfo;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WeiXinHandlerActivity extends Activity implements IWXAPIEventHandler {

    public static ShareCallback sShareCallback;
    public static WeiXinLoginCallback sLoginCallback;
    private IWXAPI mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WXAPIFactory.createWXAPI(this, ShareBlock.getShareBlockConfig().getWeiXinAppId(), true);
        mApi.registerApp(ShareBlock.getShareBlockConfig().getWeiXinAppId());
        mApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mApi.handleIntent(intent, this);
    }

    public void onReq(BaseReq req) {}

    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX) {
            // 分享
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                if (sShareCallback != null) {
                    sShareCallback.onSuccess();
                }
            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                if (sShareCallback != null) {
                    sShareCallback.onCancel();
                }
            } else {
                if (sShareCallback != null) {
                    sShareCallback.onError(resp.errCode, resp.errStr);
                }
            }
        } else if (resp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            // 登录
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                SendAuth.Resp newResp = (SendAuth.Resp) resp;
                String code = newResp.code;

                final WeiXinApiService weiXinApiService = getWeiXinApiService();
                weiXinApiService.getAccessToken(ShareBlock.getShareBlockConfig().getWeiXinAppId(),
                        ShareBlock.getShareBlockConfig().getWeiXinSecret(), code, "authorization_code")
                        .flatMap(new Func1<WeiXinAccessToken, Observable<WeiXinUserInfo>>() {
                            @Override
                            public Observable<WeiXinUserInfo> call(WeiXinAccessToken weChatAccessToken) {
                                return weiXinApiService
                                        .getWechatUserInfo(weChatAccessToken.getAccess_token(), weChatAccessToken.getOpenid());
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<WeiXinUserInfo>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (sLoginCallback != null) {
                                    sLoginCallback.onError();
                                }
                            }

                            @Override
                            public void onNext(WeiXinUserInfo userInfo) {
                                if (sLoginCallback != null) {
                                    sLoginCallback.onSuccess(userInfo);
                                }
                            }
                        });
            } else if (resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
                if (sLoginCallback != null) {
                    sLoginCallback.onCancel();
                }
            } else {
                if (sLoginCallback != null) {
                    sLoginCallback.onError();
                }
            }
        }

        finish();
    }

    private WeiXinApiService getWeiXinApiService() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        final long DEFAULT_TIMEOUT = 30;
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .baseUrl("https://api.weixin.qq.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(WeiXinApiService.class);
    }
}
