package com.personal.baseutils.utils;

import android.text.TextUtils;

import com.personal.baseutils.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ZLDateUtil {

    public static final String PATTERN = "yyyy-MM-dd";
    private static String mYear; // 当前年
    private static String mMonth; // 月
    private static String mDay;
    private static String mWeek;

    private static final String TAG = "CXDateUtil";

    public static final int NEXT7DAY = 1;
    public static final int LAST7DAY = -1;

    /**
     * 获取当天日期
     * @return
     */
    public static String getToday() {
        String date = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat(PATTERN);
        date = sdf.format(calendar.getTimeInMillis());
        return date;
    }

    /**
     * 获取天日期
     * @return
     */
    public static String getNDay(int n) {
        String date = "";
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DATE, n);
        SimpleDateFormat sdf=new SimpleDateFormat(PATTERN);
        date = sdf.format(calendar.getTimeInMillis());
        return date;
    }

    /**
     * 获取指定时间的日期
     * @param timeMillis
     * @return
     */
    public static String getDate(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        SimpleDateFormat sdf=new SimpleDateFormat(PATTERN);
        String date = sdf.format(calendar.getTime());
        return date;
    }
    /**
     * 设置指定时间的日期字符串 得到日期对象
     * @param curDate
     * @return
     */
    public static Calendar setDateStr(String curDate) {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = now.getTime();
        try {
            date = sdf.parse(curDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        now.setTime(date);
        return now;
    }

    /**
     * 获取指定时间的 时分
     * @param timeMillis
     * @return
     */
    public static String getTime(long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String time = formatter.format(timeMillis);
        return time;
    }

    public static void main(String[] args) {
        getToday();
    }


    /**
     * 获取日期差
     *
     * @param startStr
     * @param endStr
     * @return
     */
    public static String getHourSpace(String startStr, String endStr, int type)
    {

        if (TextUtils.isEmpty(startStr) || TextUtils.isEmpty(endStr)) {
            return "";
        }
        long a1 = toTimestamp(endStr, type);
        long b1 = toTimestamp(startStr, type);

        return  getHourSpace(a1,b1);

    }


    public static String getHourSpace(long a ,long b){

        long a1 = a;
        long b1 = b;
        long temp;
        if (a1 < b1) {
            temp = a1;
            a1 = b1;
            b1 = temp;
        }
        long ab = a1 - b1;

        long h = ab / 1000 / 60 / 60;

        long m = ab / 1000 / 60 % 60;

        StringBuffer sb = new StringBuffer();

        if (h == 0) {

            if(m ==0 ){
                sb.append("刚刚");
            }else {
                sb.append(m).append("分前");
            }

        } else if (m == 0) {
            sb.append(h).append("时前");
        } else {
            sb.append(h).append("时").append(m).append("分前");
        }
        return sb.toString();

    }

    /**
     * 间隔天数
     * @param to
     * @param from
     * @return
     */
    public static int getDistaceDay(long to , long from){

        if(to > from){

        }else{

            long temp = to;

            to = from;

            from = temp;

        }

        long day = (to-from)/(24l*60l*60l*1000l);

        return (int)day;
    }


    public static long toTimestamp(String data, int type)
    {
        String format = "yyyy-MM-dd HH:mm:ss";
        if (type == 1) {
            format = "yyyy-MM-dd HH:mm:ss";
        } else if (type == 2) {
            format = "MM-dd HH:mm";
        } else if (type == 3) {
            format = "HH:mm";
        } else if (type == 4) {
            format = "yyyy-MM-dd";
        } else if (type == 5) {
            format = "yyyy-MM-dd HH:mm";
        } else if (type == 6) {
            format = "yyyy年MM月dd日 HH:mm";
        } else if (type == 7) {
            format = "yyyy-M-d";
        } else if (type == 8) {
            format = "yyyy年MM月";
        } else if (type == 9) {
            format = "MM-dd HH:mm";
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);

        try {
            Date e = formatter.parse(data);
            return e.getTime();
        } catch (ParseException var5) {
            var5.printStackTrace();
            return 0L;
        }
    }


    public static String toData(long time, int type)
    {
        if (time <= 0L) {
            return "";
        } else {
            String format = "yyyy-MM-dd HH:mm:ss";
            switch (type) {
                case 1:
                    format = "yyyy-MM-dd HH:mm:ss";
                    break;
                case 2:
                    format = "MM-dd HH:mm";
                    break;
                case 3:
                    format = "HH:mm";
                    break;
                case 4:
                    format = "yyyy-MM-dd";
                    break;
                case 5:
                    format = "yyyy-MM-dd HH:mm";
                    break;
                case 6:
                    format = "yyyy年MM月dd日 HH:mm";
                    break;
                case 7:
                    format = "MM月dd日 HH:mm";
                    break;
                case 8:
                    format = "HH:mm:ss";
                    break;
                case 9:
                    format = "yyyy年MM月dd日";
                    break;
                case 10:
                    format = "yyyy/MM/dd HH:mm";
                    break;
                case 11:
                    format = "yyyy/MM/dd";
                    break;
                case 12:
                    format = "MM-dd";
                    break;
                case 13:
                    format = "yyyy/MM/dd HH:mm:ss";
                    break;
                case 14:
                    format = "MM月dd日";
                    break;
                case 15:
                    format = "dd";
                    break;
                case 16:
                    format = "yyyyMMddHHmmss";
                    break;
                case 17:
                    format = "yyyy-MM";
                    break;
                case 18:
                    format = "yyyy";
                    break;
                case 19:
                    format = "yy/MM/dd";
                    break;
                case 20:
                    format = "MM/dd HH:mm";
                    break;
            }
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(new Date(time));
        }
    }

    /*
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+"毫秒");
        }
        return sb.toString();
    }

}
