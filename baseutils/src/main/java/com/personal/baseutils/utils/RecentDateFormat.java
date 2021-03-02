package com.personal.baseutils.utils;

import android.util.Log;

/**
 * 时间格式文本解析
 * <p>
 * Created by zecker on 15/9/11.
 */
public class RecentDateFormat implements TimeTransform.DateFormat {

    @Override
    public String format(TimeTransform date, long delta, long delta2, long timestamp) {
        String time = Utils.convertDate(timestamp + "", "HH:mm");  // 后台获取的用户操作事物的时间
        if (delta > 0) {
            if (delta / TimeTransform.SECOND < 1) {
                return delta + "秒前";
            } else if (delta / TimeTransform.HOUR < 1) {
                return delta / TimeTransform.SECOND + "分钟前";
            } else if (delta / TimeTransform.HOUR < 24) {
                return delta / TimeTransform.HOUR + "小时前";
            } else if (delta2 / TimeTransform.DAY < 1) {
                return "今天" + time;
            } else if (delta2 / TimeTransform.DAY < 2) {
                return "昨天" + time;
            } else if (delta2 / TimeTransform.DAY < 3) {
                return "前天" + time;
            }
//            else if (delta / TimeTransform.DAY < 7) {
//                return delta / TimeTransform.DAY + "天前" + time;
//            }
            else if (delta / TimeTransform.DAY < 366) {
                return Utils.convertDate(timestamp + "", "MM月dd日 HH:mm");
            } else {
                return Utils.convertDate(timestamp + "", "yyyy-MM-dd HH:mm");
            }
        } else {
            Log.e("+++++++++++", "delta2222222222");
//            delta = - delta;
//            if (delta / TimeTransform.SECOND < 1){
//                return delta +"秒后";
//            }else if (delta / TimeTransform.HOUR < 1){
//                return delta / TimeTransform.SECOND+"分钟后";
//            }else if (delta / TimeTransform.DAY > -2 && new TimeTransform().getDay() == date.getDay()){
//                return delta / TimeTransform.HOUR+"小时后";
//            }else if (delta / TimeTransform.DAY > -3 && new TimeTransform().getDay() == new TimeTransform(date.getTimestamp()-TimeTransform.DAY).getDay()){
//                return "明天"+date.toString("HH:mm");
//            }else if (delta / TimeTransform.DAY > -4 && new TimeTransform().getDay() == new TimeTransform(date.getTimestamp()-TimeTransform.DAY*2).getDay()){
//                return "后天"+date.toString("HH:mm");
//            }else{
            return Utils.convertDate(timestamp + "", "yyyy-MM-dd HH:mm:ss");
//            }
        }
    }
}
