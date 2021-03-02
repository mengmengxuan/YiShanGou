package com.yunlankeji.yishangou.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.yunlankeji.yishangou.BaseApplication;
import com.personal.baseutils.utils.Base64DES;
import com.yunlankeji.yishangou.activity.login.LoginActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Create by Snooker at 2020 2020/6/17 11:29
 * Describe:
 */
public class ConstantUtil {
    public static long lastClickTime = 0;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        LogUtil.e("###############", "lastClickTime==" + lastClickTime + ";timeD==" + timeD);
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
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

    public static int dip2px(float dpValue) {
        float scale = BaseApplication.getAppContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 计算出合适的缩放倍数
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
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
        long lcc_time = Long.parseLong(timeStamp);
        String newDate = sdf.format(new Date(lcc_time * 1000L));
        return newDate;
    }

    //退出登录并清空用户数据
    public static void goLoginAndClearUserInfo(Activity context) {
        SPUtils.clear(context);//清除本地存储的用户信息
        ActivityUtils.finishAllActivities();//将所有的Activity出栈
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        //添加跳转动画，跳转时就不会显得
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 全透状态栏
     */
    public static void setStatusBarFullTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //虚拟键盘也透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static void setStatusBar(Context context, View root) {
        root.setPadding(0, getStatusBarHeight(context), 0, 0);
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static String getImagePath() {
        File path = new File(Environment.getExternalStorageDirectory() + "/yishangou/cache_images/");
//        File path = new File(Environment.getExternalStorageDirectory() + "/waterApp/cut/");
        if (!path.exists()) {
            path.mkdirs();
        }
        return path.getPath();
    }

    public static String getBase64Str(File file) {
        String base64Str = "";
        base64Str = Base64DES.encodeToString(file);
        return base64Str;
    }

 /*   //退出登录并清空用户数据
    public static void goLoginAndClearUserInfo(Activity context) {
        SPUtils.clear(context);//清除本地存储的用户信息
        ActivityUtils.finishAllActivities();//将所有的Activity出栈
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        //添加跳转动画，跳转时就不会显得
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }*/

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

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
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    public static boolean copy(Activity activity, String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param view 需要截取图片的view
     *             传入线性或相对布局就截取里面的所有内容
     * @return 截图
     */
    public static Bitmap getBitmap(Activity activity, View view) throws Exception {

        View screenView = activity.getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //获取屏幕整张图片
        Bitmap bitmap = screenView.getDrawingCache();

        if (bitmap != null) {

            //需要截取的长和宽
            int outWidth = view.getWidth();
            int outHeight = view.getHeight();

            //获取需要截图部分的在屏幕上的坐标(view的左上角坐标）
            int[] viewLocationArray = new int[2];
            view.getLocationOnScreen(viewLocationArray);

            //从屏幕整张图片中截取指定区域
            bitmap = Bitmap.createBitmap(bitmap, viewLocationArray[0], viewLocationArray[1], outWidth, outHeight);
            //    Toast.makeText(activity, "截图成功", Toast.LENGTH_SHORT).show();
            view.setDrawingCacheEnabled(false);  //禁用DrawingCahce否则会影响性能
        }

        return bitmap;
    }

    //保存图片到系统图库
    public static void onSaveBitmap(final Bitmap mBitmap, Activity activity) {
        //将Bitmap保存图片到指定的路径/sdcard/Boohee/下，文件名以当前系统时间命名,但是这种方法保存的图片没有加入到系统图库中
        File appDir = new File(Environment.getExternalStorageDirectory(), "易闪购");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(activity, "保存图片成功", Toast.LENGTH_SHORT).show();
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        } catch (Exception e) {
            e.printStackTrace();
        }

        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
    }

    /**
     * 截取scrollview的屏幕
     *
     * @param scrollView
     * @return
     */
    public static Bitmap getScrollViewBitmap(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }

        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/poster.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 截图listView的屏幕
     **/
    public static Bitmap getListViewBitmap(ListView listView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
        }

        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/screen_test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*
     * format  #.##
     * juli 1223.332434
     * */
    public static String setFormat(String format, String juli) {
        String juli_format;
        DecimalFormat df = new java.text.DecimalFormat(format);
//        DecimalFormat df = new java.text.DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        juli_format = df.format(Double.parseDouble(juli));
        return juli_format;
    }
}
