package com.yunlankeji.yishangou.utils;

import android.util.Log;

/**
 * Create by Snooker at 2020 2020/6/17 11:12
 * Describe:日志打印工具
 */
public class LogUtil {

    private static int VERBOSE = 1;
    private static int DEBUG = 2;
    private static int INFO = 3;
    private static int WARN = 4;
    private static int ERROR = 5;
    private static int NOTHING = 6;
    //规定每段显示的长度
    private static int LOG_MAX_LENGTH = 2000;

    public static void e(String tag, String msg) {
        if (NOTHING > ERROR) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                //剩下的文本还是大于规定长度则继续重复截取并输出
                if (strLength > end) {
                    Log.e(tag, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAX_LENGTH;
                } else {
                    Log.e(tag, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void d(String tag, String msg) {
        if (NOTHING > DEBUG) {
            if (msg != null) {
                int strLength = msg.length();
                int start = 0;
                int end = LOG_MAX_LENGTH;
                for (int i = 0; i < 100; i++) {
                    //剩下的文本还是大于规定长度则继续重复截取并输出
                    if (strLength > end) {
                        Log.d(tag, msg.substring(start, end));
                        start = end;
                        end = end + LOG_MAX_LENGTH;
                    } else {
                        Log.d(tag, msg.substring(start, strLength));
                        break;
                    }
                }
            }
        }
    }

    public static void v(String tag, String msg) {
        if (NOTHING > VERBOSE) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                //剩下的文本还是大于规定长度则继续重复截取并输出
                if (strLength > end) {
                    Log.v(tag, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAX_LENGTH;
                } else {
                    Log.v(tag, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void i(String tag, String msg) {
        if (NOTHING > INFO) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                //剩下的文本还是大于规定长度则继续重复截取并输出
                if (strLength > end) {
                    Log.i(tag, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAX_LENGTH;
                } else {
                    Log.i(tag, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void w(String tag, String msg) {
        if (NOTHING > WARN) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAX_LENGTH;
            for (int i = 0; i < 100; i++) {
                //剩下的文本还是大于规定长度则继续重复截取并输出
                if (strLength > end) {
                    Log.w(tag, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAX_LENGTH;
                } else {
                    Log.w(tag, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }
}
