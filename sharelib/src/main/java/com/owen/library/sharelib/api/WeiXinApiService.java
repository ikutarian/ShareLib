package com.owen.library.sharelib.api;


import com.owen.library.sharelib.model.WeiXinAccessToken;
import com.owen.library.sharelib.model.WeiXinUserInfo;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface WeiXinApiService {

    /**
     * 获取AccessToken
     */
    @GET("sns/oauth2/access_token")
    Observable<WeiXinAccessToken> getAccessToken(@Query("appid") String appId,
                                                 @Query("secret") String secret,
                                                 @Query("code") String code,
                                                 @Query("grant_type") String grantType);

    /**
     * 获取用户个人信息
     */
    @GET("sns/userinfo")
    Observable<WeiXinUserInfo> getWechatUserInfo(@Query("access_token") String accessToken,
                                                 @Query("openid") String openId);
}
