package com.personal.baseutils.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间格式转换器
 *
 * Created by Kelvin_Wang on 15/8/3.
 */
public class TimeTransform {
    public static final int SECOND = 60;
    public static final int HOUR = 3600;
    public static final int DAY = 86400;
    public static final int WEEK = 604800;

    Calendar currentTime;

    public TimeTransform(){
        currentTime=new GregorianCalendar();
        currentTime.setTime(new Date());
    }
    public TimeTransform(long timestamp){
        currentTime=new GregorianCalendar();
        currentTime.setTime(new Date(timestamp));
    }
    public TimeTransform(int year, int month, int day){
        currentTime=new GregorianCalendar(year,month,day);
    }

    public int getDay(){
        return currentTime.get(Calendar.DATE);
    }
    public int getMonth(){
        return currentTime.get(Calendar.MONTH)+1;
    }
    public int getYear(){
        return currentTime.get(Calendar.YEAR);
    }
    public long getTimestamp(){
        return currentTime.getTime().getTime();
    }

    /**
     * 格式化输出日期
     * 年:y 月:M 日:d 时:h(12制)/H(24值)分:m 秒:s 毫秒:S
     * @param formatString
     */
    public String toString(String formatString){
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        String date = format.format(currentTime.getTime());
        return date;
    }


    /**
     * 格式化解析日期文本
     * 年:y	月:M	 日:d 时:h(12制)/H(24值)	分:m	 秒:s 毫秒:S
     * @param formatString
     */
    public TimeTransform parse(String formatString,String content){
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        try {
            currentTime.setTime(format.parse(content));
            return this;
        } catch (ParseException e) {
            return null;
        }
    }

    // 将时间戳转换为yyyy年MM月dd日
    public static String getDateToString(long time) {
           Date d = new Date(time);
           SimpleDateFormat sf = null;
           sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }


    public String getMonth(String time){
        String month = null;
        switch (time) {
            case "01":
                month = "一月";
                break;
            case "02":
                month = "二月";
                break;
            case "03":
                month = "三月";
                break;
            case "04":
                month = "四月";
                break;
            case "05":
                month = "五月";
                break;
            case "06":
                month = "六月";
                break;
            case "07":
                month = "七月";
                break;
            case "08":
                month = "八月";
                break;
            case "09":
                month = "九月";
                break;
            case "10":
                month = "十月";
                break;
            case "11":
                month = "十一月";
                break;
            case "12":
                month = "十二月";
                break;
        }
        return month;
    }

    public interface DateFormat{
        public String format(TimeTransform date, long delta, long delta2, long timestamp);
    }
    public String toString(DateFormat format,long timestamp){
        long delta = (System.currentTimeMillis()/1000 - timestamp);
        String time = Utils.convertDate(timestamp+"","yyyy-MM-dd");
        long timestamp2 = Long.parseLong(Utils.convertTime(time,"yyyy-MM-dd"));
        long delta2 = (System.currentTimeMillis()/1000 - timestamp2);
        return format.format(this,delta,delta2,timestamp);
    }
}
