package com.owen.library.sharelib.content.weixin;

import android.graphics.Bitmap;

import com.owen.library.sharelib.content.ShareContent;

public class WeiXinWebPageShareContent implements ShareContent {

    public static final int TO_FRIEND = 1;
    public static final int TO_TIMELINE = 2;
    public static final int TO_FAVORITE = 3;

    private String url;
    private String title;
    private String description;
    private Bitmap thumbBitmap;
    private int shareTarget;

    public WeiXinWebPageShareContent(String url, String title, String description, Bitmap thumbBitmap, int shareTarget) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.thumbBitmap = thumbBitmap;
        this.shareTarget = shareTarget;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    public int getShareTarget() {
        return shareTarget;
    }
}
