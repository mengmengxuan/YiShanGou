package com.yunlankeji.yishangou.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Create by Snooker on 2020/7/20
 * Describe:获取屏幕宽高的工具类
 */
public class ScreenUtil {
    /**
     * 获取屏幕宽高，建议采用
     *
     * @param context
     */
    public static int[] getScreenHW(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int[] HW = new int[]{width, height};
        return HW;
    }

    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenW(Context context) {
        return getScreenHW(context)[0];
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenH(Context context) {
        return getScreenHW(context)[1];
    }
}
