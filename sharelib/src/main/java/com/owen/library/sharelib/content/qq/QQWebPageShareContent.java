package com.owen.library.sharelib.content.qq;

import com.owen.library.sharelib.content.ShareContent;

/**
 * QQ默认分享
 */
public class QQWebPageShareContent implements ShareContent {

    public static final int TO_QQ = 1;
    public static final int TO_QZONE = 2;

    private String title;
    private String targetUrl;
    private String summary;
    private String thumbImageUrl;
    private String appName;
    private int shareTarget;

    public QQWebPageShareContent(String title, String targetUrl,
                                 String summary, String thumbImageUrl,
                                 String appName, int shareTarget) {
        this.title = title;
        this.targetUrl = targetUrl;
        this.summary = summary;
        this.thumbImageUrl = thumbImageUrl;
        this.appName = appName;
        this.shareTarget = shareTarget;
    }

    public String getTitle() {
        return title;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getSummary() {
        return summary;
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public String getAppName() {
        return appName;
    }

    public int getShareTarget() {
        return shareTarget;
    }
}
