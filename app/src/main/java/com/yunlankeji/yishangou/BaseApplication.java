package com.yunlankeji.yishangou;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.personal.baseutils.utils.SPUtils;
import com.personal.baseutils.utils.Utils;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.Api;
import com.yunlankeji.yishangou.network.NetWorkManager;

/**
 * Create by Snooker on 2020/8/20
 * Describe:
 */
public class BaseApplication extends Application {
    private static Context context;

    private static Handler sHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sHandler = new Handler();

        NetWorkManager.getInstance().init();

        UMConfigure.init(this, "5ce8ddc5570df3437e0008ef", "0", UMConfigure.DEVICE_TYPE_PHONE, "");
        // PlatformConfig.setWeixin(Api.WECHAT.APP_ID, Api.WECHAT.APP_SECRET);
        PlatformConfig.setWeixin("wxd0c6ec88177d3afd", "05e0e8aa1c7bce6cf9038aa0cf9d38e4");

    }

    public static Handler getHandler() {
        return sHandler;
    }

    public static Context getAppContext() {
        return context;
    }
}
