package com.liao.momo;

import android.app.Application;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by Administrator on 2016-10-27.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ShareSDK.initSDK(this);

    }
}
