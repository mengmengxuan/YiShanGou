package com.yunlankeji.yishangou.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

/**
 * Create by Snooker on 2020/11/3
 * Describe:定位服务
 */
public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "channel_id";
    private static final CharSequence CHANNEL_NAME = "channel_name";
    private Context context;

    private Timer timer;

    int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID).build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      /*  timer = new Timer(true);
        timer.schedule(timerTask, 5000, 5000); //延时1000ms后执行，1000ms执行一次
*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Global.isRider != null) {
                    if ("1".equals(Global.isRider)) {
                        requestUpdateRiderLngLat();
                    }
                }

                if (Global.isMerchant != null) {
                    if ("1".equals(Global.isMerchant)) {
                        //查询商家订单
                        requestQueryPageList();
                    }
                }
            }
        }).start();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerTime = SystemClock.elapsedRealtime() + 30 * 1000;

        Intent intent1 = new Intent(this, LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent1, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 上传骑手经纬度
     */
    private void requestUpdateRiderLngLat() {
        ParamInfo paramInfo = new ParamInfo();
        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");
        paramInfo.memberCode = Global.memberCode;
        paramInfo.longitude = longitude;
        paramInfo.latitude = latitude;

        LogUtil.d(TAG, "paramInfo.longitude --> " + paramInfo.longitude);
        LogUtil.d(TAG, "paramInfo.latitude --> " + paramInfo.latitude);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateRiderLngLat(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "上传坐标：" + JSON.toJSONString(response.data));
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    /**
     * 查询商家待处理订单
     */
    private void requestQueryPageList() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.size = "1000";
        paramInfo.page = "1";
        paramInfo.merchantCode = Global.merchantCode;
        paramInfo.merchantOrderStatus = "poad";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryPageList(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "订单列表：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    List<Data> data1 = data.data;
                    if (data1 != null) {

                        LogUtil.d(TAG, "data1.size() --> " + data1.size());
                        LogUtil.d(TAG, "count --> " + count);
                        if (data1.size() > count) {
                            //如果未处理订单比30秒前的未处理订单数量多了，就说明有新订单了，就播报音频
                            //播放语音提示
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    showSound(R.raw.order_remind);
                                }
                            }).start();
                            count = data1.size();
                        }
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    /**
     * 语音提示
     *
     * @param raw
     */
    protected void showSound(int raw) {
        MediaPlayer mediaPlayer = null;
        mediaPlayer = MediaPlayer.create(BaseApplication.getAppContext(), raw);
        mediaPlayer.setVolume(1f, 1f);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
            }
        });
    }

}

