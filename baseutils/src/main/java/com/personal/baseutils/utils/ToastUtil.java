package com.personal.baseutils.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/1/18.
 */
public class ToastUtil {

    static Toast mToast;
    public static void show(Context context, int info) {
        if(mToast == null) {
            mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(info);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void show(Context context, String info) {
            if(mToast == null) {
                mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(info);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
    }


}
