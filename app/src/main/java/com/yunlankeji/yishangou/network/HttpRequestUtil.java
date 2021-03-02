package com.yunlankeji.yishangou.network;

import android.util.Log;

import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.bean.WeChatPayBean;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.personal.baseutils.utils.Utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * 网络请求工具
 */
public class HttpRequestUtil {

    public static void httpRequestForData(Call<ResponseBean<Data>> call, final HttpRequestCallback callback) {
        if (Utils.isHasNet(BaseApplication.getAppContext())) {
            call.enqueue(new Callback<ResponseBean<Data>>() {
                @Override
                public void onResponse(Call<ResponseBean<Data>> call, retrofit2.Response<ResponseBean<Data>> response) {
                    if (response.body() != null) {
                        if (response.body().code.equals("200")) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onDefeat(response.body().code, response.body().message);
                            //     ToastUtil.show(BaseApplication.context,response.body().message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBean<Data>> call, Throwable t) {
                    Log.e("##########", "----------onFailure--------");
                    //      callback.onFailure(t.getCause().toString());
                }
            });
        } else {
            callback.onFailure("nonet");
        }

    }

    public static void httpRequest(Call<ResponseBean> call, final HttpRequestCallback callback) {
        if (Utils.isHasNet(BaseApplication.getAppContext())) {
            call.enqueue(new Callback<ResponseBean>() {
                @Override
                public void onResponse(Call<ResponseBean> call, retrofit2.Response<ResponseBean> response) {
                    if (response.body() != null) {
                        if (response.body().code.equals("200") || response.body().code.equals("202")) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onDefeat(response.body().code, response.body().message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBean> call, Throwable t) {
                    //       callback.onFailure(t.getCause().toString());
                }
            });
        } else {
            callback.onFailure("nonet");
        }

    }

    public static void httpRequestForList(Call<ResponseBean<List<Data>>> call, final HttpRequestCallback callback) {
        if (Utils.isHasNet(BaseApplication.getAppContext())) {
            call.enqueue(new Callback<ResponseBean<List<Data>>>() {
                @Override
                public void onResponse(Call<ResponseBean<List<Data>>> call, retrofit2.Response<ResponseBean<List<Data>>> response) {
                    if (response.body() != null) {
                        if (response.body().code.equals("200")) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onDefeat(response.body().code, response.body().message);
                            //      ToastUtil.show(BaseApplication.context,response.body().message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBean<List<Data>>> call, Throwable t) {
                    //                callback.onFailure(t.getCause().toString());
                }
            });
        } else {
            callback.onFailure("nonet");
        }
    }

    public static void httpRequestWeChatPay(Call<ResponseBean<WeChatPayBean>> call,
                                            final HttpRequestCallback callback) {
        if (Utils.isHasNet(BaseApplication.getAppContext())) {
            call.enqueue(new Callback<ResponseBean<WeChatPayBean>>() {
                @Override
                public void onResponse(Call<ResponseBean<WeChatPayBean>> call, retrofit2.Response<ResponseBean<WeChatPayBean>> response) {
                    if (response.body() != null) {
                        if (response.body().code.equals("200")) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onDefeat(response.body().code, response.body().message);
                            //     ToastUtil.show(BaseApplication.context,response.body().message);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBean<WeChatPayBean>> call, Throwable t) {
                    Log.e("##########", "----------onFailure--------");
                    //      callback.onFailure(t.getCause().toString());
                }
            });
        } else {
            callback.onFailure("nonet");
        }

    }

}
