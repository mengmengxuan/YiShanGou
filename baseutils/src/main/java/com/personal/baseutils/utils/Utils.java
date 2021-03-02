package com.personal.baseutils.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.SafeKeyGenerator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.EmptySignature;
import com.personal.baseutils.R;
import com.personal.baseutils.glide.DataCacheKey;
import com.personal.baseutils.widget.CornerTransform;
import com.personal.baseutils.widget.GlideCircleTransform;
import com.personal.baseutils.widget.GlideRoundTransform;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@SuppressLint("WrongConstant")
public class Utils {

    public static long lastClickTime = 0;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        Log.e("###############", "lastClickTime==" + lastClickTime + ";timeD==" + timeD);
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 验证手机格式
     */
    public static boolean isMobile(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }
    /**
     * 验证身份证格式
     */
    public static boolean isIdCard(String idCard) {
        String telRegex = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        if (TextUtils.isEmpty(idCard)) return false;
        else return idCard.matches(telRegex);
    }


//    public static String UrlWithHttp(String url) {
//        String picUrl;
//        if (null==url||TextUtils.isEmpty(url)) {
//            picUrl = "123";
//        } else {
//            if (url.startsWith("http")) {
//                picUrl = url;
//            } else {
//                picUrl = Api.PIC_URL + url;
//            }
//        }
//        return picUrl;
//    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public static float px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale + 0.5f);
    }


    /**
     * 检查是否有可用网络
     *
     * @param context 上下文环境
     * @return 有可用网络返回true 否则返回false
     */
    public static boolean isHasNet(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();// 获取联网状态网络
        if (info == null || !info.isAvailable()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param context
     * @return int
     * @throws
     * @author chenzheng
     * @Description: 获取屏幕宽度
     * @since 2014-5-9
     */
    public static int getScreenW(Context context) {
        return getScreenSize(context, true);
    }

    /**
     * @param context
     * @return int
     * @throws
     * @author chenzheng
     * @Description: 获取屏幕高度
     * @since 2014-5-9
     */
    public static int getScreenH(Context context) {
        return getScreenSize(context, false);
    }

    private static int getScreenSize(Context context, boolean isWidth) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return isWidth ? dm.widthPixels : dm.heightPixels;
    }

    /**
     * 将字符串转换成浮点型，如果格式错误，将转成0
     *
     * @param str
     * @return
     */
    public static float toFloat(String str) {
        if (Utils.isEmpty(str)) {
            return 0;
        }

        str = str.trim();
        float i = 0;
        try {
            i = Float.parseFloat(str);
        } catch (Exception e) {

        }
        return i;
    }

    public static boolean isEmpty(String paramString) {
        return (paramString == null) || (paramString.trim().length() <= 0);
    }

    public static String toZero(String paramString) {
        return isEmpty(paramString) ? "0" : paramString;
    }


    /*
     * format  #.##
     * juli 1223.332434
     * */
    public static String setFormat(String format, String juli) {
        String juli_format;
        DecimalFormat df = new DecimalFormat(format);
        df.setRoundingMode(RoundingMode.HALF_UP);
        juli_format = df.format(Double.parseDouble(juli));
        return juli_format;
    }


    /**
     * @param timeStamp
     * @param format    yyyy-MM-dd HH:mm:ss
     * @return String
     * @throws
     * @author chenzheng
     * @Description: 将时间戳转化为时间字符串
     * @since 2014-5-26
     */
    @SuppressLint("SimpleDateFormat")
    public static String convertDate(String timeStamp, String format) {
        if (TextUtils.isEmpty(timeStamp)) {
            return "";
        }
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        long lcc_time = Long.valueOf(timeStamp);
        String newDate = sdf.format(new Date(lcc_time * 1000L));
        return newDate;
    }

    /**
     * @param format yyyy-MM-dd HH:mm:ss
     * @return String
     * @throws
     * @author liyong
     * @Description: 将时间字符串转化为时间戳
     * @since 2015-11-11
     */
    public static String convertTime(String selectTime, String format) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date day = null;
        try {
            day = sdf.parse(selectTime);
            long l = day.getTime();
            String time = String.valueOf(l);
            re_time = time.substring(0, 10);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return re_time;
    }

    /**
     * @param selectTime // 时间字符串
     *                   yyyy-MM-dd HH:mm:ss
     * @return String
     * @throws
     * @author liyong
     * @Description: 将时间字符串转化为距当前时间多少分钟/小时/天
     * @since 2015-11-11
     */
    public static String recentDate(String selectTime) {
        String re_time;

        //    long timestamp = Long.parseLong(Utils.convertTime(selectTime, "yyyy-MM-dd HH:mm:ss"));
        long timestamp = Long.parseLong(selectTime);
        TimeTransform transform = new TimeTransform(timestamp);
        re_time = transform.toString(new RecentDateFormat(), timestamp);
        return re_time;
    }

    /**
     * @return String
     * @throws
     * @author liyong
     * @Description: 获取系统当前时间的日期等
     * @since 2015-11-11
     */
    @SuppressLint("WrongConstant")
    public static String currentDate(String Time) {

        Calendar c = Calendar.getInstance();
        if (Time.equals("year")) {
            int year = c.get(Calendar.YEAR);
            String currentYear = String.valueOf(year);
            return currentYear;
        } else if (Time.equals("month")) {
            int month = c.get(Calendar.MONTH) + 1;
            String currentMonth = String.valueOf(month);
            return currentMonth;
        } else if (Time.equals("day2")) {  //  一个月中的第几天
            int day2 = c.get(Calendar.DAY_OF_MONTH);
            String currentDay = String.valueOf(day2);
            return currentDay;
        } else if (Time.equals("hour")) {  // 24小时制下的小时数，午夜表示为0
            int hour = c.get(Calendar.HOUR_OF_DAY);
            String currentHour = String.valueOf(hour);
            return currentHour;
        } else if (Time.equals("data")) {  // 当前日期 ，返回0 为星期天
            int hour = c.get(Calendar.DAY_OF_WEEK) - 1;
            String currentData = String.valueOf(hour);
            return currentData;
        } else {
            int minute = c.get(Calendar.MINUTE);
            String currentMinute = String.valueOf(minute);
            return currentMinute;
        }
    }

    /**
     * @return String
     * @throws
     * @author liyong
     * @Description: 当前日期 ，返回0 为星期天
     * @since 2015-11-11
     */
    @SuppressLint("WrongConstant")
    public static int currentWeek() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }


    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }


    /**
     * 压缩图片 按尺寸压缩图片
     * 尺寸压缩，导致file变小
     */
    public static void compressPicture(String srcPath, String desPath) {
        FileOutputStream fos = null;
        BitmapFactory.Options op = new BitmapFactory.Options();

        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);
        op.inJustDecodeBounds = false;

        // 缩放图片的尺寸
        float w = op.outWidth;
        float h = op.outHeight;
        float hh = 800f;//
        float ww = 800f;//
        // 最长宽度或高度1024
        float be = 1.0f;
        if (w > h && w > ww) {
            be = (float) (w / ww);
        } else if (w < h && h > hh) {
            be = (float) (h / hh);
        }
        if (be <= 0) {
            be = 1.0f;
        }
        Log.e("xxbe", be + "");
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, op);
        int desWidth = (int) (w / be);
        int desHeight = (int) (h / be);
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);
        try {
            fos = new FileOutputStream(desPath);
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveBitmap2file(Bitmap bmp, String filename) {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        BufferedOutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }

    public static Bitmap convertToBitmap(String path, int w, int h) {

        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        Log.e("@@@@@@@@@#######", "opts=" + opts);
        Log.e("@@@@@@@@@#######", "width=" + width);
        Log.e("@@@@@@@@@#######", "height=" + height);
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        Log.e("@@@@@@@@@#######", "scaleWidth=" + scaleWidth);
        Log.e("@@@@@@@@@#######", "scaleHeight=" + scaleHeight);
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        //      opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        Log.e("@@@@@@@@@#######", "weak.get()=" + weak.get());
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    /**
     * @param uri：图片的本地url地址
     * @return Bitmap；
     */
    public Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    public static Bitmap returnBitMap(final String src) {

        try {
            Log.d("FileUtil", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return makeRoundCorner(myBitmap);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }
//Log.i(TAG, "ps:"+ left +", "+ top +", "+ right +", "+ bottom);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 质量压缩方法
     * 尺寸不变，file大小压缩
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            //       Log.e("@@@@@@@@!!!!!!!!!!#####","开始压缩！！！！！");
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        //     Log.e("@@@@@@@@!!!!!!!!!!#####", "压缩后图片的大小" + (bitmap.getByteCount() / 1024 / 1024)+ "M");
        return bitmap;


    }


    /**
     * 保存照片
     * @param mBitmap
     * @return
     */
//    public static String saveBitmap(Bitmap mBitmap, File f) {
//        String sdStatus = Environment.getExternalStorageState();
//        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//            //      Toast.makeText(this,"内存卡异常，请检查内存卡插入是否正确",Toast.LENGTH_SHORT).show();
//            return "";
//        }
////        String path=System.currentTimeMillis()+".jpg";
////        File f = new File(Environment.getExternalStorageDirectory()+"takeout/cache",path);
//        //  createFile(savePath);
//        try {
//            FileOutputStream fOut = null;
//            fOut = new FileOutputStream(f);
//            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//            fOut.flush();
//            fOut.close();
//            return f.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 得到应用程序的版本名称
     */
    public static String getVersionName(Context context) {
        // 用来管理手机的APK
        PackageManager pm = context.getPackageManager();
        try {
            // 得到知道APK的功能清单文件
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode(Context context) {
        // 用来管理手机的APK
        PackageManager pm = context.getPackageManager();
        try {
            // 得到知道APK的功能清单文件
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 改变svg图片颜色
     */
    public static void setSVGColor(Context context, ImageView imageView, int resId, int color) {
        if (context != null) {
            VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(context.getResources(), resId, null);
            //你需要改变的颜色
            if (color != 0) {
                vectorDrawableCompat.setTint(context.getResources().getColor(color));
            }
            imageView.setImageDrawable(vectorDrawableCompat);
        }
    }


    public static void setPercentage(Context context, View view, double sizeW, double sizeH) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
        params.width = (int) (getScreenW(context) * sizeW);
        params.height = (int) (getScreenW(context) * sizeH);
        view.setLayoutParams(params);
    }

    public static void setPercentageHeight(Context context, View view, double sizeH) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
        params.width = (int) (getScreenW(context));
        params.height = (int) (getScreenH(context) * sizeH);
        view.setLayoutParams(params);
    }

    public static void setDialogFullScreen(Activity activity, Dialog dialog) {
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog
                .getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * 使textview具体字变灰
     *
     * @param text
     * @param string
     */
    public static void setBeforeRed(TextView text, String string) {
        ColorStateList redColors = ColorStateList.valueOf(0xfffa2429);
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(text
                .getText().toString());
        // style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
        // size 为0 即采用原始的正常的 size大小
        spanBuilder.setSpan(new TextAppearanceSpan(null, 0, 0,
                redColors, null), 0, text.getText().toString().indexOf(string), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        text.setText(spanBuilder);
    }

    public static void setImage(Context context, View view, double sizeW, double sizeH) {
        if (null != context) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
            params.width = (int) (getScreenW(context) * sizeW);
            params.height = (int) (getScreenW(context) * sizeH);
            Log.e("####xxxx####", "width==" + params.width);
            Log.e("####xxxx####", "height==" + params.height);
            view.setLayoutParams(params);
        }
    }

//    public static void LoadPicUrl(Context context, String url, ImageView imageView, int w, int h) {
//        //    String picUrl = UrlWithHttp(url);
//        String picUrl = url;
//        Glide.with(context).load(picUrl).dontAnimate().diskCacheStrategy(DiskCacheStrategy
//                .SOURCE).override(w, h).fitCenter().into(imageView);
//    }

    /**
     * 获取时间间隔
     *
     * @param startTime 传入的时间格式必须类似于“yyyy-MM-dd HH:mm:ss”这样的格式
     * @return
     **/

    public static String getTimeDiff(String startTime) {
        if (startTime.length() != 19) {
            return startTime;
        }
        String result = null;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            Date d1 = (Date) sd.parse(startTime, pos);
            // Date().getTime()减去以前的时间距离1970年的时间间隔d1.getTime()得出的就是以前的时间与现在时间的时间间隔
            long time = new Date().getTime() - d1.getTime();// 得出的时间间隔是毫秒\
            if (time / 1000 <= 0) {
                result = "刚刚";
            } else if (time / 1000 < 60) {
                // 如果时间间隔小于60秒则显示多少秒前
                result = "刚刚";
            } else if (time / 3600000 < 24) {
                // 如果时间间隔小于24小时则显示多少小时前
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                result = sdf.format(d1.getTime());

            } else if (time / 86400000 < 2) {
                // 如果时间间隔小于2天则显示昨天
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                result = sdf.format(d1.getTime());
                result = "昨天" +
                        result;
            }
            //            else if (time / 86400000 < 7) {
//                // 如果时间间隔小于3天则显示前天
//                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                result = sdf.format(d1.getTime());
//                result = getWeek(startTime) +
//                        result;
//            }
//            else if (time / 86400000 < 30) {
//                // 如果时间间隔小于30天则显示多少天前
//                SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
//                result = sdf.format(d1.getTime());
//            }
            else {
//                // 大于1年，显示年月日时间
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
//                result = sdf.format(d1.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
                result = sdf.format(d1.getTime());
            }
        } catch (Exception e) {
            return startTime;
        }
        return result;
    }

    public static String getDifferentTime(long startTime, long endTime) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff;
        String flag;
        if (startTime < endTime) {
            diff = endTime - startTime;
            flag = "前";
        } else {
            diff = startTime - endTime;
            flag = "后";
        }
        if (diff <= 3 * 60) {
            return "";
        } else {
            diff = Math.abs(System.currentTimeMillis() / 1000 - endTime);
            if (diff >= 0 && diff <= 3 * 60) {
                return convertDate(endTime + "", "HH:mm");
            } else if (diff > 3 * 60 && diff <= 60 * 60 * 24) {
                if (convertDate(System.currentTimeMillis() / 1000 + "", "dd").equals(convertDate
                        (endTime + "", "dd"))) {
                    return convertDate(endTime + "", "HH:mm");
                } else {
                    return "昨天 " + convertDate(endTime + "", "HH:mm");
                }
            } else if (diff > 60 * 60 * 24 && diff <= 60 * 60 * 24 * 2) {
                return "前天 " + convertDate(endTime + "", "HH:mm");
            } else if (diff > 60 * 60 * 24 * 2 && diff <= 60 * 60 * 24 * 7) {
                return getWeek(convertDate(endTime + "", "")) + convertDate(endTime + "",
                        "HH:mm");
            } else if (diff > 60 * 60 * 24 * 7 && diff <= 60 * 60 * 24 * 30) {
                return convertDate(endTime + "", "MM-dd HH:mm");
            } else {
                return convertDate(endTime + "", "yyyy-MM-dd HH:mm");
            }
        }
//        else if (diff > 3 * 60 && diff <= 60 * 60 * 24) {
//            if (convertDate(System.currentTimeMillis() / 1000 + "", "dd").equals(convertDate
//                    (endTime + "", "dd"))) {
//                Log.i("@@@@Day",convertDate(System.currentTimeMillis() / 1000 + "", "dd")+",
// "+convertDate
//                        (endTime + "", "dd"));
//                return convertDate(endTime + "", "HH:mm");
//            } else {
//                return "昨天 " + convertDate(endTime + "", "HH:mm");
//            }
//        } else if (diff > 60 * 60 * 24 && diff <= 60 * 60 * 24 * 2) {
//            return "昨天 " + convertDate(endTime + "", "HH:mm");
//        } else if (diff > 60 * 60 * 24 * 2 && diff <= 60 * 60 * 24 * 7) {
//            return getWeek(convertDate(endTime + "", "")) + convertDate(endTime + "", "HH:mm");
//        } else if (diff > 60 * 60 * 24 * 7 && diff <= 60 * 60 * 24 * 30) {
//            return convertDate(endTime + "", "MM-dd HH:mm");
//        } else {
//            return convertDate(endTime + "", "yyyy-MM-dd HH:mm");
//        }
//        day=diff/(60*60*24);
//        Log.i("@@@Time",day+"天"+flag);
//        hour=(diff%(60*60*24))/(60*60);
//        Log.i("@@@Time",hour+"小时"+flag);
//        min=(diff%(60*60)/60);
//        Log.i("@@@Time",min+"分钟"+flag);
//        sec=diff%60;
//        Log.i("@@@Time",sec+"秒"+flag);
//        if(day!=0)return day+"天"+flag;
//        if(hour!=0)return hour+"小时"+flag;
//        if(min!=0)return min+"分钟"+flag;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 设置的需要判断的时间  //格式如2012-09-08
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */

//  String pTime = "2012-03-12";
    public static String getWeek(String pTime) {
        String Week = "星期";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "天";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "六";
        }
        return Week;
    }

    /**
     * 显示软件盘
     */
    public static void showSoftInput(final Context context, final EditText mMessageEt) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if (imm != null) {
            try {
                ((Activity) context).getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                                .INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            mMessageEt.requestFocus();
                            imm.showSoftInput(mMessageEt, 0);
                        }
                    }
                }, 100);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 获取软键盘状态
     *
     * @param context
     * @return
     */
    public static boolean isShowSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        //获取状态信息
        return imm.isActive(view);//true 打开
    }

    /**
     * 判断软键盘是否弹出
     */
    public static boolean isShowKeyboard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if (imm.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
            imm.showSoftInput(v, 0);
            return true;
            //软键盘已弹出
        } else {
            return false;
            //软键盘未弹出
        }
    }

    /**
     * 隐藏软件盘
     */
    public static void hideSoftInput(final Context context, final View voiceIv) {
        ((Activity) context).getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(voiceIv.getWindowToken(), 0);
                }
            }
        }, 100);
    }

    /**
     * 保存照片
     *
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Bitmap mBitmap, File f) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            //      Toast.makeText(this,"内存卡异常，请检查内存卡插入是否正确",Toast.LENGTH_SHORT).show();
            return "";
        }
//        String path=System.currentTimeMillis()+".jpg";
//        File f = new File(Environment.getExternalStorageDirectory()+"takeout/cache",path);
        //  createFile(savePath);
        try {
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            return f.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除文件
     *
     * @param path 文件地址
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static final int CropImage = 3;

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public static void startPhotoZoom(Activity context, Uri uri, int w, int h) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        if (android.os.Build.MODEL.contains("HUAWEI")) {//华为特殊处理 不然会显示圆
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", w);
        intent.putExtra("outputY", h);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", true);
        String path = Environment.getExternalStorageDirectory() + "/zhiliaoLive/cut/" + "cut.jpg";
        Log.e("qqqqqqwwwww", "path==" + path);
        Uri uritempFile = Uri.parse("file://" + "/" + path);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        context.startActivityForResult(intent, CropImage);
    }


    public static String getHtmlReplace(String text) {
        return text.replace("\r", "&nbsp;").replace("\n", "<br />")
                .replace("[生气]", "<img src=\"angry\"/>").replace("[酷]", "<img src=\"cool\"/>")
                .replace("[哭]", "<img src=\"cry\"/>").replace("[高兴]", "<img src=\"happy\"/>")
                .replace("[吻]", "<img src=\"kiss\"/>").replace("[呆呆]", "<img src=\"logy\"/>")
                .replace("[可爱]", "<img src=\"lovely\"/>").replace("[震惊]", "<img src=\"shock\"/>");
    }

    /**
     * 二维码图片存储根目录
     *
     * @param context
     * @return
     */
    public static String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }
        return context.getFilesDir().getAbsolutePath();
    }


    public static void calculateTag(final View tag, final TextView title, final String text, final String name) {

        ViewTreeObserver observer = tag.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                SpannableString spannableString = new SpannableString(text);
                LeadingMarginSpan.Standard what = new LeadingMarginSpan.Standard(tag.getWidth(), 0);
                spannableString.setSpan(what, 0, spannableString.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                if (!Utils.isEmpty(name)) {
                    //   Log.e("########---#####", "name==" + name);  // FFE497 chat_name
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFE497"));
                    spannableString.setSpan(colorSpan, 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

//                    ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor("#F88BCC"));
//                    spannableString.setSpan(colorSpan2, name.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                title.setText(spannableString);
                tag.getViewTreeObserver().removeOnPreDrawListener(
                        this);
                return false;
            }
        });
    }


    /**
     * 生成一个startNum 到 endNum之间的随机数(不包含endNum的随机数)
     *
     * @param startNum
     * @param endNum
     * @return
     */
    public static int getRandomNum(int startNum, int endNum) {
        if (endNum > startNum) {
            Random random = new Random();
            return random.nextInt(endNum - startNum) + startNum;
        }
        return 0;
    }

//    /**
//     * 获取需要播放的动画资源
//     */
//    public static int[] getRes(Context context,String source) {
//        TypedArray typedArray = context.getResources().obtainTypedArray(R.array.hdc);
//        int len = typedArray.length();
//        int[] resId = new int[len];
//        for (int i = 0; i < len; i++) {
//            resId[i] = typedArray.getResourceId(i, -1);
//        }
//        typedArray.recycle();
//        return resId;
//    }

    /**
     * @param mobile
     * @return
     */
    public static String isHideMobile(String mobile) {
        if (Utils.isEmpty(mobile)) {
            mobile = "";
        } else {
            mobile = mobile.substring(0, 3) + "****" + mobile.substring(8, 11);
        }
        return mobile;
    }

    /**
     * @param card
     * @return
     */
    public static String isHideCard(String card) {
        card = "****  ****  ****  " + card.substring(card.length() - 4, card.length());
        return card;
    }

    /**
     * 得到圆形的bitmap对象
     *
     * @param bitmap
     * @return
     */
    public static Bitmap getOvalBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 圆角图片
     *
     * @param radius
     * @param source
     * @return
     */
    public static Bitmap roundCrop(float radius, Bitmap source) {

        if (source == null) return null;

        float radiusNew = Resources.getSystem().getDisplayMetrics().density * radius;

        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radiusNew, radiusNew, paint);
        return result;
    }

    public static File getCacheFile2(String id, Context context) {

        DataCacheKey dataCacheKey = new DataCacheKey(new GlideUrl(id), EmptySignature.obtain());

        SafeKeyGenerator safeKeyGenerator = new SafeKeyGenerator();

        String safeKey = safeKeyGenerator.getSafeKey(dataCacheKey);
        try {
            int cacheSize = 100 * 1000 * 1000;
            DiskLruCache diskLruCache = DiskLruCache.open(new File(context.getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR), 1, 1, cacheSize);
            DiskLruCache.Value value = diskLruCache.get(safeKey);
            if (value != null) {
                return value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void saveBytesToFile(String filePath, byte[] data) {
        File file = new File(filePath);

        BufferedOutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(new FileOutputStream(file));
            outStream.write(data);
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
