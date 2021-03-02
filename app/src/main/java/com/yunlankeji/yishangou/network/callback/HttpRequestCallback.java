package com.yunlankeji.yishangou.network.callback;

import com.yunlankeji.yishangou.network.responsebean.ResponseBean;

/**
 * Created by Snooker on 2020/8/20
 */
public interface HttpRequestCallback {
    void onSuccess(ResponseBean response);

    void onFailure(String msg);

    void onDefeat(String code, String msg);
}
