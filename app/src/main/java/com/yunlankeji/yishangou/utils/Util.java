package com.yunlankeji.yishangou.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by hzg on 16/11/18.
 */

public class Util {
    private static final String TAG = "Util";

    static float defdensity;

    public static String speedMethodNormal(float speed) {
        // 对结果进行格式化（保留小数点后的2位）
        //java.text.DecimalFormat format = new java.text.DecimalFormat("0.0");
        // 对结果进行格式化（不保留小数点后）
        DecimalFormat format1 = new DecimalFormat("0.0");
        String res = "";
        // 原始bit
        double speedIn = speed;
        // 如果是bit那么直接返回bit
        if (speed == 0) {
            return "0 Mbps";
        }
        if (speed < 1024) {
            String r = "Mbps";
            res = format1.format(speedIn / 1024 / 1024 * 8) + " " + r;
        } else {
            // 如果比bit大，那么直接换算成KB
            speedIn = speedIn / 1024 / 1024 * 8;
            if (speedIn < 1024) {
                String r = "Mbps";
                res = format1.format(speedIn) + " " + r;
            } else {
                // 如果比KB大，那么直接换算成MB，当换算成gB的时候
                speedIn = speedIn / 1024 / 1024 * 8;
                if (speedIn < 1024) {
                    String r = "Mbps";
                    res = format1.format(speedIn) + " " + r;
                } else {
                    // 如果比mB大，那么直接换算成gB，当换算成gB的时候
                    speedIn = speedIn / 1024 / 1024 * 8;
                    if (speedIn < 1024) {
                        String r = "Mbps";
                        res = format1.format(speedIn) + " " + r;
                    }
                }
            }
        }
        return res;
    }

    public static int dpToPx(int dp, Context context) {
        if (context == null)
            return (int) (dp * defdensity);
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        if (d == null)
            return (int) (dp * defdensity);
        defdensity = d.density;
        return (int) (dp * d.density);
    }

    /***********************************************************
     * Toast相关
     ***********************************************************/
    private static SoftReference<Toast> sToastRef = null;

    public static void hideToast() {
        if (sToastRef != null) {
            Toast previousToast = sToastRef.get();
            if (previousToast != null) {
                previousToast.cancel();
            }
        }
    }

    public static void showToast(Context context, int s) {
        showToast(context, context.getString(s));
    }

    public static void showToast(Context context, String s) {
        showToast(context, s, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String s, boolean show) {
        if (show) {
            showToast(context, s, Toast.LENGTH_SHORT);
        }
    }

    public static void showToastLong(Context context, int s) {
        showToast(context, context.getString(s), Toast.LENGTH_LONG);
    }

    public static void showToastLong(Context context, String s) {
        showToast(context, s, Toast.LENGTH_LONG);
    }

    private static void showToast(final Context context, final String s, int length) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast toast = Toast.makeText(context, s, length);
            hideToast();
            toast.show();
            sToastRef = new SoftReference<Toast>(toast);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showToast(context, s);
                }
            });
        }
    }

    /**
     * 判断版本号的大小
     * 0代表相等，1代表versionLoacal大于versionCurrent，-1代表versionLoacal小于versionCurrent
     * <p>
     * 2.1.4  2.1.2
     */
    public static int compareVersion(String versionLoacal, String versionCurrent) {
        if (versionLoacal.equals(versionCurrent)) {
            return 0;
        } else {
            return -1;
        }



       /* String[] version1Array = versionLoacal.split("\\.");
        String[] version2Array = versionCurrent.split("\\.");

        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;

        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }

        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }

            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }*/
    }

    /**
     * 获取两位随机数
     *
     * @return
     */
    public static String getRandom() {
        Random random = new Random();
        int ends = random.nextInt(99);
        return String.format("%02d", ends);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] getHtmlByteArray(final String url) {
        URL htmlUrl = null;
        InputStream inStream = null;
        try {
            htmlUrl = new URL(url);
            URLConnection connection = htmlUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = inputStreamToByte(inStream);

        return data;
    }

    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] readFromFile(String fileName, int offset, int len) {
        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "readFromFile: file not found");
            return null;
        }

        if (len == -1) {
            len = (int) file.length();
        }

        Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " + (offset + len));

        if (offset < 0) {
            Log.e(TAG, "readFromFile invalid offset:" + offset);
            return null;
        }
        if (len <= 0) {
            Log.e(TAG, "readFromFile invalid len:" + len);
            return null;
        }
        if (offset + len > (int) file.length()) {
            Log.e(TAG, "readFromFile invalid file len:" + file.length());
            return null;
        }

        byte[] b = null;
        try {
            RandomAccessFile in = new RandomAccessFile(fileName, "r");
            b = new byte[len]; // 创建合适文件大小的数组
            in.seek(offset);
            in.readFully(b);
            in.close();

        } catch (Exception e) {
            Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
            e.printStackTrace();
        }
        return b;
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static int parseInt(final String string, final int def) {
        try {
            return (string == null || string.length() <= 0) ? def : Integer.parseInt(string);

        } catch (Exception e) {
        }
        return def;
    }

    /*
     * format  #.##
     * juli 1223.332434
     * */
    public static String setFormat(String format, String juli) {
        String juli_format;
        DecimalFormat df = new DecimalFormat(format);
//        DecimalFormat df = new java.text.DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        juli_format = df.format(Double.parseDouble(juli));
        return juli_format;
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
     * 获取网络视频的第一帧
     *
     * @param videoUrl
     * @return
     */
    public static Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    /**
     * 获取视频缩略图
     *
     * @param uri
     * @param path   视频路径
     * @param width  宽度
     * @param height 高度
     * @return
     */
    public static Bitmap getVideoThumbnail(Context context, Uri uri, String path, int width, int height) throws IOException {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        if (Build.VERSION.SDK_INT >= 29) {
            bitmap = context.getContentResolver().loadThumbnail(uri, new Size(width, height), null);
        }
        return bitmap;
    }

}
