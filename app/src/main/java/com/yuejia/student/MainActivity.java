package com.yuejia.student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.owen.library.sharelib.LoginManager;
import com.owen.library.sharelib.ShareManager;
import com.owen.library.sharelib.callback.QQLoginCallback;
import com.owen.library.sharelib.callback.ShareCallback;
import com.owen.library.sharelib.callback.WeiXinLoginCallback;
import com.owen.library.sharelib.content.qq.QQWebPageShareContent;
import com.owen.library.sharelib.content.weixin.WeiXinWebPageShareContent;
import com.owen.library.sharelib.model.QQUserInfo;
import com.owen.library.sharelib.model.WeiXinUserInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_login_with_weixin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithWeiXin();
            }
        });
        findViewById(R.id.btn_weixin_share_webPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weiXinShare();
            }
        });

        findViewById(R.id.btn_login_with_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithQQ();
            }
        });
        findViewById(R.id.btn_qq_share_webPage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qqShare();
            }
        });
    }

    private void loginWithQQ() {
        LoginManager.loginWithQQ(this, new QQLoginCallback() {
            @Override
            public void onSuccess(QQUserInfo userInfo) {
                Log.d(TAG, "loginWithQQ onSuccess: " + userInfo + "Thread=" + Thread.currentThread().getName());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "loginWithQQ onCancel: ");
            }

            @Override
            public void onError() {
                Log.d(TAG, "loginWithQQ onError: ");
            }
        });
    }

    private static final String TAG = "Sayuri";

    private void loginWithWeiXin() {
        LoginManager.loginWithWeiXin(this, new WeiXinLoginCallback() {
            @Override
            public void onSuccess(WeiXinUserInfo userInfo) {
                Log.d(TAG, "onSuccess: " + userInfo.toString());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError() {
                Log.d(TAG, "onError: ");
            }
        });
    }

    private void weiXinShare() {
        final RadioButton rdBtnShareToFriend = (RadioButton) findViewById(R.id.target_friend);

        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final byte[] imageBytes = response.body().bytes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
                        ShareManager.share(MainActivity.this,
                                new WeiXinWebPageShareContent("http://connect.qq.com/",
                                        "我是标题啊，你知道吗",
                                        "这是一条新闻的内容啊，嗯，知道了", bitmap,
                                        rdBtnShareToFriend.isChecked() ? WeiXinWebPageShareContent.TO_FRIEND : WeiXinWebPageShareContent.TO_TIMELINE),
                                new ShareCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "WeiXin onSuccess: ");
                                    }

                                    @Override
                                    public void onCancel() {
                                        Log.d(TAG, "WeiXin onCancel: ");
                                    }

                                    @Override
                                    public void onError(int errorCode, String msg) {
                                        Log.d(TAG, "WeiXin onError: ");
                                    }
                                });
                    }
                });
            }
        });
    }

    private void qqShare() {
        final RadioButton rdBtnShareToQQ = (RadioButton) findViewById(R.id.target_qq);

        ShareManager.share(this, new QQWebPageShareContent("我是标题啊，你知道吗",
                "http://connect.qq.com/",
                "这是一条新闻的内容啊，嗯，知道了",
                "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg",
                getString(R.string.app_name), rdBtnShareToQQ.isChecked() ? QQWebPageShareContent.TO_QQ : QQWebPageShareContent.TO_QZONE),
                new ShareCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "取消了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String msg) {
                Toast.makeText(MainActivity.this, "发生了错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Tencent.onActivityResultData不能注册两次，否则会出现重复回调的bug，下面两者只能写一个
        ShareManager.onQQActivityResult(requestCode, resultCode, data);
//        LoginManager.onQQActivityResult(requestCode, resultCode, data);
    }
}
