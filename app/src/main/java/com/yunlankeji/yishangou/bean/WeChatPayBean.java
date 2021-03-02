package com.yunlankeji.yishangou.bean;

import java.util.List;

/**
 * Create by Snooker on 2020/11/19
 * Describe:
 */
public class WeChatPayBean {

    public int code;
    public String orderNumber;
    public WeChatPayData data;
    public List<String> ids;
    public String resMsg;

    public static class WeChatPayData {
        public String appId;
        public String nonceStr;
        public String packageMsg;
        public String partnerId;
        public String prepayId;
        public String sign;
        public String timeStamp;

    }
}
