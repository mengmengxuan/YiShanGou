package com.yunlankeji.yishangou.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 16323 on 2018/9/6.
 */

public class UpdateAppManager {
    // 外存sdcard存放路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/yishangou/apk/";
    // 下载应用存放全路径
    private static final String FILE_NAME = FILE_PATH + "AutoUpdate.apk";
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 1;
    //Log日志打印标签
    private static final String TAG = "Update_log";
    private Context context;
    //获取新版APK的默认地址
    private String apk_path;
    // 下载应用的进度条
    private ProgressDialog progressDialog;
    //新版本号和描述语言
    private int serverVersionCode;
    int index;

    public UpdateAppManager(Context context, int index) {
        this.context = context;
        this.index = index;
    }

    /**
     * 获取当前版本号
     */
    private int getCurrentVersion() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            Log.e(TAG, "当前版本名和版本号" + info.versionName + "--" + info.versionCode);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取当前版本号出错");
            return 0;
        }
    }

    /**
     * 从服务器获得更新信息
     */
    public void getUpdateMsg() {
        //    installApp();
//        requestUpdate();
    }

//    private void requestUpdate() {
//        LogUtil.d(UpdateAppManager.this, "FILE_NAME==" + FILE_NAME);
//        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestUpdate();
//        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
//            @Override
//            public void onSuccess(ResponseBean response) {
//                LogUtil.d(UpdateAppManager.this, "onSuccess: 请求更新成功");
//                LogUtil.d(UpdateAppManager.this, "版本更新：" + JSON.toJSONString(response.data));
//
//                Data data = (Data) response.data;
//                try {
//                    serverVersionCode = Integer.parseInt(data.propertyIndex);  // 服务器版本号
//
//                    apk_path = data.propertyValue;
//                    if (serverVersionCode > getCurrentVersion() && !TextUtils.isEmpty(apk_path)) {
//                        showNoticeDialog();
//                    } else {
//                        if (index == 2) {
//                            ToastUtil.show("已是最新版本");
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(String msg) {
//            }
//
//            @Override
//            public void onDefeat(String code, String msg) {
//            }
//        });
//    }

    /**
     * 显示提示更新对话框
     */
    private void showNoticeDialog() {

        /*ShowUpdateDialog showUpdateDialog = new ShowUpdateDialog(context);
        showUpdateDialog.setCanceledOnTouchOutside(true);
        showUpdateDialog.setIsForceUpdate(true);
        showUpdateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return true;
            }
        });
        showUpdateDialog.setDesc("检测到应用有新的版本,请立即更新!");
        showUpdateDialog.setOnChooseUpdateListenner(new ShowUpdateDialog.OnChooseUpdateListenner() {
            @Override
            public void chooseUpdate() {
                showDownloadDialog();
            }
        });
        showUpdateDialog.show();*/
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //设置对话框左上角的图标
        //设置对话框标题
        builder.setTitle("版本更新");
        //设置对话框的消息内容
        builder.setMessage("检测到应用有新的版本,请立即更新!");
        //积极按钮，立即更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk，获取apk链接
                showDownloadDialog();
            }
        });
        //消极按钮，稍后再说
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消更新，进入主界面
                dialog.dismiss();
            }
        });

        //弹出对话框之后，如果点击返回键，进入主界面
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        //一定要记得show
        builder.show();
    }

    /**
     * 显示下载进度对话框
     */
    public void showDownloadDialog() {
        Log.e(TAG, "-------showDownloadDialog-------");
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在下载...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        new downloadAsyncTask().execute();

    }

    /**
     * 下载新版本应用
     */
    private class downloadAsyncTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "执行至--onPreExecute");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Log.e(TAG, "执行至--doInBackground");
            URL url;
            HttpURLConnection connection = null;
            InputStream in = null;
            FileOutputStream out = null;
            try {
                Log.e(TAG, "--doInBackground  11111111111111");
                url = new URL(apk_path);
                connection = (HttpURLConnection) url.openConnection();
                in = connection.getInputStream();
                long fileLength = connection.getContentLength();
                Log.e(TAG, "--doInBackground  222222222222");
                File file_path = new File(FILE_PATH);
                if (!file_path.exists()) {
                    file_path.mkdirs();
                }
                Log.e(TAG, "--doInBackground  333333333333 file_path==" + file_path);
                out = new FileOutputStream(new File(FILE_NAME));//为指定的文件路径创建文件输出流
                Log.e(TAG, "--doInBackground  4444444444 out==" + out);
                byte[] buffer = new byte[1024 * 1024];
                Log.e(TAG, "--doInBackground  555555555");
                int len = 0;
                long readLength = 0;

                Log.e(TAG, "执行至--readLength = 0");

                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);//从buffer的第0位开始读取len长度的字节到输出流
                    readLength += len;
                    int curProgress = (int) (((float) readLength / fileLength) * 100);
                    Log.e(TAG, "当前下载进度：" + curProgress);
                    publishProgress(curProgress);
                    if (readLength >= fileLength) {
                        Log.e(TAG, "执行至--readLength >= fileLength");
                        break;
                    }
                }
                out.flush();
                return INSTALL_TOKEN;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.e(TAG, "异步更新进度接收到的值：" + values[0]);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            progressDialog.dismiss();//关闭进度条
            //     setPermission(FILE_NAME);
            //安装应用
            installApp();
        }
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        setPermission(FILE_NAME);
        File appFile = new File(FILE_NAME);
        if (!appFile.exists()) {
            return;
        }
        Log.e(TAG, "FILE_NAME==" + FILE_NAME);
        // 跳转到新版本应用安装页面

        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        Log.e(TAG, "SDK_INT==" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.e(TAG, "SDK_INT==11111111");
            File file = (new File(FILE_NAME));
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.yunlankeji.yishangou.provider", file);
            Log.e(TAG, "apkUri==" + apkUri);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            Log.e(TAG, "SDK_INT==22222222");
            intent.setDataAndType(Uri.fromFile(new File(FILE_NAME)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 提升读写权限
     *
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static void setPermission(String filePath) {
        String command = "chmod " + "777" + " " + filePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
