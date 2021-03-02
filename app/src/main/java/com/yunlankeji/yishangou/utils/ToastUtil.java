package com.yunlankeji.yishangou.utils;

import android.widget.Toast;

import com.yunlankeji.yishangou.BaseApplication;

public class ToastUtil {

    private static final boolean DEBUG = true;//弹出网络请求的Toast

    public static void showShort(String msg) {
        Toast toast = Toast.makeText(BaseApplication.getAppContext(), null, Toast.LENGTH_SHORT);
        toast.setText(msg);
        toast.show();
    }

    public static void showLong(String msg) {
        Toast toast = Toast.makeText(BaseApplication.getAppContext(), null, Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.show();
    }

    public static void showShortForNet(String msg) {
        if (DEBUG) {
            Toast toast = Toast.makeText(BaseApplication.getAppContext(), null, Toast.LENGTH_SHORT);
            toast.setText(msg);
            toast.show();
        }
    }

    public static void showLongForNet(String msg) {
        if (DEBUG) {
            Toast toast = Toast.makeText(BaseApplication.getAppContext(), null, Toast.LENGTH_LONG);
            toast.setText(msg);
            toast.show();
        }
    }
}
