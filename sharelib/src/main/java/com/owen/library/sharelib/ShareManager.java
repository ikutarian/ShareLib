package com.owen.library.sharelib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.owen.library.sharelib.activity.WeiXinHandlerActivity;
import com.owen.library.sharelib.callback.ShareCallback;
import com.owen.library.sharelib.content.ShareContent;
import com.owen.library.sharelib.content.qq.QQWebPageShareContent;
import com.owen.library.sharelib.content.weixin.WeiXinWebPageShareContent;
import com.owen.library.sharelib.util.BitmapUtil;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

/**
 * QQ、微信分享
 */
public class ShareManager {

    /**
     * 因为QQ的文档中说需要在Activity的onActivityResult()方法中调用Tencent.onActivityResultData(requestCode,resultCode,data,listener)
     * 所以，使用Activity实例的HashCode为键，listener为值，把它们保存起来
     */
    private static IUiListener sQQCallBack;

    public static void share(Activity activity, ShareContent shareContent, final ShareCallback shareCallback) {
        if (shareContent instanceof QQWebPageShareContent) {
            QQWebPageShareContent webPageShareContent = (QQWebPageShareContent) shareContent;

            Tencent tencent = Tencent.createInstance(ShareBlock.getShareBlockConfig().getQQAppId(),
                    activity.getApplicationContext());

            Bundle params = new Bundle();
            if (webPageShareContent.getShareTarget() == QQWebPageShareContent.TO_QQ) {
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, webPageShareContent.getTitle());
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webPageShareContent.getTargetUrl());
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, webPageShareContent.getSummary());
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, webPageShareContent.getThumbImageUrl());
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, webPageShareContent.getAppName());
            } else {
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_APP);
                params.putString(QzoneShare.SHARE_TO_QQ_TITLE, webPageShareContent.getTitle());
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, webPageShareContent.getTargetUrl());
                params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, webPageShareContent.getSummary());
                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add(webPageShareContent.getThumbImageUrl());
                params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            }

            sQQCallBack = new IUiListener() {
                @Override
                public void onComplete(Object response) {
                    if (shareCallback != null) {
                        shareCallback.onSuccess();
                    }
                }

                @Override
                public void onError(UiError error) {
                    if (shareCallback != null) {
                        shareCallback.onError(error.errorCode, error.errorMessage);
                    }
                }

                @Override
                public void onCancel() {
                    if (shareCallback != null) {
                        shareCallback.onCancel();
                    }
                }
            };

            if (webPageShareContent.getShareTarget() == QQWebPageShareContent.TO_QQ) {
                tencent.shareToQQ(activity, params, sQQCallBack);
            } else {
                tencent.shareToQzone(activity, params, sQQCallBack);
            }
        } else if (shareContent instanceof WeiXinWebPageShareContent) {
            WeiXinHandlerActivity.sShareCallback = shareCallback;

            WeiXinWebPageShareContent webPageShareContent = (WeiXinWebPageShareContent) shareContent;

            WXWebpageObject webPage = new WXWebpageObject();
            webPage.webpageUrl = webPageShareContent.getUrl();

            WXMediaMessage msg = new WXMediaMessage(webPage);
            msg.title = webPageShareContent.getTitle();
            msg.description = webPageShareContent.getDescription();
            msg.thumbData = BitmapUtil.bitmap2ByteArray(webPageShareContent.getThumbBitmap(), true);

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            if (webPageShareContent.getShareTarget() == WeiXinWebPageShareContent.TO_FRIEND) {
                req.scene = SendMessageToWX.Req.WXSceneSession;
            } else if (webPageShareContent.getShareTarget() == WeiXinWebPageShareContent.TO_TIMELINE) {
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
            } else if (webPageShareContent.getShareTarget() == WeiXinWebPageShareContent.TO_FAVORITE) {
                req.scene = SendMessageToWX.Req.WXSceneFavorite;
            } else {
                req.scene = SendMessageToWX.Req.WXSceneSession;
            }

            IWXAPI api = WXAPIFactory.createWXAPI(activity, ShareBlock.getShareBlockConfig().getWeiXinAppId(), true);
            api.registerApp(ShareBlock.getShareBlockConfig().getWeiXinAppId());
            api.sendReq(req);
        }
    }

    /**
     * 在调用QQ的API的Activity中的onActivityResult()方法，需要调用这个方法
     */
    public static void onQQActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, sQQCallBack);
    }
}
