package com.yunlankeji.yishangou.activity.runerrands;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/3
 * Describe:正在赶来页面
 */
public class ComingActivity extends BaseActivity implements AMap.InfoWindowAdapter {

    private static final String TAG = "ComingActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.map_view)
    MapView mapView;//
    @BindView(R.id.m_head_iv)
    ImageView mHeadIv;//
    @BindView(R.id.m_rider_name_tv)
    TextView mRiderNameTv;//
    @BindView(R.id.m_rider_phone_tv)
    TextView mRiderPhoneTv;//
    @BindView(R.id.m_call_phone_ll)
    LinearLayout mCallPhoneLl;//
    @BindView(R.id.m_cancel_order_tv)
    TextView mCancelOrderTv;//

    private AMap aMap;
    private String orderNumber;
    private String phoneNum;
    private Handler handler = new Handler();
    private Marker mRiderMarker;
    private String mSendDistance;
    private String mReceiveDistance;
    private String mOrderStatus;
    private Runnable runnable;
    private Marker mSendMarker;
    private Marker mReceiveMarker;

    @Override
    public int setLayout() {
        return R.layout.activity_coming;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("正在赶来");

        orderNumber = getIntent().getStringExtra("orderNumber");

        //每隔10秒获取一次骑手的经纬度
        runnable = new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "获取订单详情，拿骑手经纬度");
                //获取订单详情
                requestQueryOrderDetail();
                handler.postDelayed(this, 10 * 1000);
            }
        };
        handler.postDelayed(runnable, 0);

        aMap = mapView.getMap();
        aMap.setInfoWindowAdapter(this);
    }

    @OnClick({R.id.m_back_iv, R.id.m_cancel_order_tv, R.id.m_call_phone_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_cancel_order_tv:

                break;
            case R.id.m_call_phone_ll:
                showConnectDialog(phoneNum);
                break;
        }
    }

    /**
     * 订单详情
     */
    private void requestQueryOrderDetail() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = orderNumber;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryOrderDetail(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "订单详情：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {

                    //骑手距发货地的距离
                    mSendDistance = data.sendDistance;
                    //骑手距收货地的距离
                    mReceiveDistance = data.receiveDistance;
                    //订单状态
                    mOrderStatus = data.orderStatus;

                    //添加骑手地址的marker
                    if (!TextUtils.isEmpty(data.riderLatitude)) {
                        if (mRiderMarker != null) {
                            mRiderMarker.remove();
                        }
                        addRiderAddressMarkersToMap(Double.parseDouble(data.riderLatitude),
                                Double.parseDouble(data.riderLongitude));
                    }

                    if (data.orderStatus.equals("2")) {
                        //正在赶来（取货）
                        if (mSendMarker != null) {
                            mSendMarker.remove();
                        }
                        //添加发货地址的marker
                        addSendAddressMarkersToMap(Double.parseDouble(data.sendLatitude), Double.parseDouble(data.sendLongitude));
                    } else if (data.payStatus.equals("0")) {
                        //未支付
                        doActivity(ComingActivity.this, CheckOrderActivity.class, data.orderNumber, "orderNumber");
                        finish();
                    } else if (data.orderStatus.equals("3")) {
                        //正在赶来（送货）
                        //添加收货地址的marker
                        if (mReceiveMarker != null) {
                            mReceiveMarker.remove();
                        }
                        addReceiveAddressMarkersToMap(Double.parseDouble(data.receiveLatitude), Double.parseDouble(data.receiveLongitude));
                    }

                    if (!TextUtils.isEmpty(data.riderLatitude) && !TextUtils.isEmpty(data.riderLongitude)) {
                        LatLng latLng = new LatLng(Double.parseDouble(data.riderLatitude), Double.parseDouble(data.riderLongitude));

                        if (aMap != null) {
                            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                        }
                    }
                    
                    //骑手头像
                    Glide.with(ComingActivity.this)
                            .load(R.mipmap.icon_rider_logo_default)
                            .into(mHeadIv);
                    //骑手姓名
                    mRiderNameTv.setText(data.riderName);
                    //骑手电话
                    phoneNum = data.riderPhone;
                    mRiderPhoneTv.setText(data.riderPhone);
                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShortForNet(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    private void showConnectDialog(String phoneNum) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption("联系骑手")
                .setMessage("是否立即联系？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phoneNum);
                        intent.setData(data);
                        startActivity(intent);
                    }
                }).show();
    }

    /**
     * 添加收货地址的marker
     *
     * @param receiveLatitude
     * @param receiveLongitude
     */
    private void addReceiveAddressMarkersToMap(double receiveLatitude, double receiveLongitude) {
        LatLng CHENGDU = new LatLng(receiveLatitude, receiveLongitude);
        mReceiveMarker = aMap.addMarker(new MarkerOptions().position(CHENGDU).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.icon_user_marker))
                .draggable(false));
    }

    /**
     * 添加发货地址的marker
     *
     * @param sendLatitude
     * @param sendLongitude
     */
    private void addSendAddressMarkersToMap(double sendLatitude, double sendLongitude) {
        LatLng CHENGDU = new LatLng(sendLatitude, sendLongitude);
        mSendMarker = aMap.addMarker(new MarkerOptions().position(CHENGDU).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.icon_user_marker))
                .draggable(false));

    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = LayoutInflater.from(ComingActivity.this).inflate(R.layout.infowindow_rider_distance, null);
        //创建我们需要的标记样式
        //然后进行赋值
        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口，将自定义的infoWindow和Marker关联起来
     */
    private void render(Marker marker, View infoWindow) {
        TextView mRiderDistanceTv = infoWindow.findViewById(R.id.m_rider_distance_tv);
        if (mOrderStatus.equals("2")) {
            // 正在赶来（取货）
            mRiderDistanceTv.setText("配送员距您" + ConstantUtil.setFormat("0.00", mSendDistance) + "km");
        } else if (mOrderStatus.equals("3")) {
            // 正在赶来（送货）
            mRiderDistanceTv.setText("配送员距您" + ConstantUtil.setFormat("0.00", mReceiveDistance) + "km");
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 添加骑手地址的marker
     *
     * @param riderLatitude
     * @param riderLongitude
     */
    private void addRiderAddressMarkersToMap(double riderLatitude, double riderLongitude) {
        LatLng CHENGDU = new LatLng(riderLatitude, riderLongitude);
        mRiderMarker = aMap.addMarker(new MarkerOptions().position(CHENGDU).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.icon_rider_marker))
                .draggable(false));

        getInfoWindow(mRiderMarker);
        mRiderMarker.showInfoWindow();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }

        handler.removeCallbacks(runnable);
    }
}
