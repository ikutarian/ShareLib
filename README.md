## 配置工作

### 1. 在build.gradle中配置QQ的key

```
defaultConfig {
    manifestPlaceholders = ["tencentAppId": "tencent123456"]   // tencent+你的AppId
}
```

### 2. 在java代码中配置常量

建议在 `Application` 类中初始化

```java
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
```

### 3. 微信的  WXEntryActivity 配置

假设包名为`com.ok.share`，那么在  `com.ok.share.wxapi` 下新建一个 `WXEntryActivity` ，并继承 `WeiXinHandlerActivity`

```java
public class WXEntryActivity extends WeiXinHandlerActivity {
}
```

最后在 AndroidManifest.xml 文件中注册这个 Activity

```xml
<application>
	<activity 
            android:theme="@android:style/Theme.Translucent.NoTitleBar"
            android:name=".wxapi.WXEntryActivity"
            android:exported="true"/>
</application>
```

## 使用

### 微信登录

```java
LoginManager.loginWithWeiXin(this, new WeiXinLoginCallback() {
            @Override
            public void onSuccess(WeiXinUserInfo userInfo) {
               	// ...
            }

            @Override
            public void onCancel() {
                // ...
            }

            @Override
            public void onError() {
                // ...
            }
        });
```

### QQ登录

```java
LoginManager.loginWithQQ(this, new QQLoginCallback() {
		@Override
		public void onSuccess(QQUserInfo userInfo) {
			// ...
		}

		@Override
		public void onCancel() {
			// ...
		}

		@Override
		public void onError() {
			// ...
		}
	});
```

同时，还需要在 `Activity` 的 `onActivityResult()` 方法中加入

```java
LoginManager.onQQActivityResult(requestCode, resultCode, data);
```

### 微信分享

```java
ShareManager.share(MainActivity.this,
		new WeiXinWebPageShareContent("http://connect.qq.com/",
				"我是标题啊，你知道吗",
				"这是一条新闻的内容啊，嗯，知道了", thumbImageBitmap,
				WeiXinWebPageShareContent.TO_FRIEND /* 发送到朋友圈 */),
		new ShareCallback() {
			@Override
			public void onSuccess() {
				// ...
			}

			@Override
			public void onCancel() {
				// ...
			}

			@Override
			public void onError(int errorCode, String msg) {
				// ...
			}
		});
```

### QQ分享

```java
ShareManager.share(this, new QQWebPageShareContent("我是标题啊，你知道吗",
		"http://connect.qq.com/",
		"这是一条新闻的内容啊，嗯，知道了",
		"http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg",
		getString(R.string.app_name), QQWebPageShareContent.TO_QZONE /* 分享到QQ空间 */), new ShareCallback() {
	@Override
	public void onSuccess() {
		// ...
	}

	@Override
	public void onCancel() {
		// ...
	}

	@Override
	public void onError(int errorCode, String msg) {
		// ...
	}
});
```

同时，还需要在 `Activity` 的 `onActivityResult()` 方法中加入

```java
ShareManager.onQQActivityResult(requestCode, resultCode, data);
```

## 混淆

```
-keep public class com.owen.library.sharelib.model.** { *; }
```

## 注意事项

由于QQ SDK 自身的原因，在 `Activity` 的 `onActivityResult()` 方法中

```java
LoginManager.onQQActivityResult(requestCode, resultCode, data);
```

和

```
ShareManager.onQQActivityResult(requestCode, resultCode, data);
```

两者只能写一个。也就是说一个Activity 中，不能既分享又登录。否则会造成重复回调的bug。

## 本库使用的依赖

```
com.android.support:support-v4:24.2.1
com.squareup.retrofit2:retrofit:2.1.0
com.squareup.retrofit2:converter-gson:2.1.0
com.google.code.gson:gson:2.7
com.squareup.okhttp3:okhttp:3.4.1
io.reactivex:rxjava:1.2.0
io.reactivex:rxandroid:1.2.1
com.squareup.retrofit2:adapter-rxjava:2.1.0
```