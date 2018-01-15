package com.owen.library.sharelib;

public class ShareBlock {

    private static ShareBlockConfig sShareBlockConfig;

    public static void init(ShareBlockConfig config) {
        sShareBlockConfig = config;
    }

    public static ShareBlockConfig getShareBlockConfig() {
        return sShareBlockConfig;
    }
}
