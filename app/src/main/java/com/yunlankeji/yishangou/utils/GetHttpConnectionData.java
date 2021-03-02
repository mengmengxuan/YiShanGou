package com.yunlankeji.yishangou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetHttpConnectionData {
    String str = null;//网路请求往回的数据

    public static String getData(String url) {//url网路请求的网址
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection hc = null;
        InputStream inputStream = null;
        StringBuffer sb = null;
        BufferedReader br = null;
        try {
            hc = (HttpURLConnection) u.openConnection();
            hc.setRequestMethod("GET");
            inputStream = hc.getInputStream();
            sb = new StringBuffer();
            br = new BufferedReader(new InputStreamReader(inputStream));
            String len = null;
            while ((len = br.readLine()) != null) {
                sb.append(len);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
