package com.yunlankeji.yishangou.network;

import com.yunlankeji.yishangou.globle.Global;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Time: 2019/2/19 0019
 * Author:LiYong
 * Description:
 */
public class BaseInterceptor implements Interceptor {

    private Map<String, String> headers;

    public BaseInterceptor() {
        headers = new HashMap<>();
//        headers.put("token", Global.token);
//        headers.put("phone", Global.phone);

        //   headers.put("APPID",Api.appId);
        //    headers.put("ENV","0");
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
        return chain.proceed(builder.build());

    }
}