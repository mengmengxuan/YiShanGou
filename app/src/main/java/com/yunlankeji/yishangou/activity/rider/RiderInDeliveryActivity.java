package com.yunlankeji.yishangou.activity.rider;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
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
import com.yunlankeji.yishangou.utils.GPSUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/2
 * Describe:送货中页面
 */
public class RiderInDeliveryActivity extends BaseActivity implements RouteSearch.OnRouteSearchListener {
    private static final String TAG = "RiderInDeliveryActivity";
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
    @BindView(R.id.m_location_tv)
    TextView mLocationTv;//
    @BindView(R.id.m_detail_address_tv)
    TextView mDetailAddressTv;//
    @BindView(R.id.m_call_phone_ll)
    LinearLayout mCallPhoneLl;//
    @BindView(R.id.m_receive_name_tv)
    TextView mReceiveNameTv;//
    @BindView(R.id.m_address_tv)
    TextView mAddressTv;//
    @BindView(R.id.m_estimated_distance_tv)
    TextView mEstimatedDistanceTv;//
    @BindView(R.id.m_moved_tv)
    TextView mMovedTv;//
    @BindView(R.id.m_commit_tv)
    TextView mCommitTv;//

    private String id;
    private String mReceivePhone;
    private AMap aMap;
    private RouteSearch mRouteSearch;

    // 定时任务实现
    private Handler handler = new Handler();
    private LocationManager locationManager;
    private String mOrderNumber;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_in_delivery;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("送货中");
        aMap = mapView.getMap();

        id = getIntent().getStringExtra("id");

    }

    @Override
    public void initData() {
        //获取订单详情
        requestQueryRiderOrderDetail();
    }

    /**
     * 添加骑手地址的marker
     *
     * @param latitude
     * @param longitude
     */
    private void addRiderAddressMarkersToMap(double latitude, double longitude) {
        LatLng CHENGDU = new LatLng(latitude, longitude);
        aMap.addMarker(new MarkerOptions().position(CHENGDU).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.icon_starting_point))
                .draggable(false));
    }

    /**
     * 添加收货地址的marker
     *
     * @param receiveLatitude
     * @param receiveLongitude
     */
    private void addReceiveAddressMarkersToMap(double receiveLatitude, double receiveLongitude) {
        LatLng CHENGDU = new LatLng(receiveLatitude, receiveLongitude);
        aMap.addMarker(new MarkerOptions().position(CHENGDU).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.icon_ending_point))
                .draggable(false));
    }

    /**
     * 获取订单详情
     */
    private void requestQueryRiderOrderDetail() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        Call<ResponseBean<Data>> call =
                NetWorkManager.getInstance().getRequest().requestQueryRiderOrderDetail(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "骑手订单详情：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    //收货地址
                    //添加收货地址的marker
                    addReceiveAddressMarkersToMap(Double.parseDouble(data.receiveLatitude),
                            Double.parseDouble(data.receiveLongitude));

                    //骑手位置（取手机当前位置）
                    String latitude = (String) SPUtils.get(RiderInDeliveryActivity.this, "latitude", "");
                    String longitude = (String) SPUtils.get(RiderInDeliveryActivity.this, "longitude", "");
                    //添加骑手地址的marker
                    addRiderAddressMarkersToMap(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    LatLonPoint mStartPoint = new LatLonPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    LatLonPoint mEndPoint = new LatLonPoint(Double.parseDouble(data.receiveLatitude), Double.parseDouble(data.receiveLongitude));

                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    if (aMap != null) {
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                    }

                    //在地图上绘制两点之间的线
//                    //初始化 RouteSearch 对象
//                    mRouteSearch = new RouteSearch(RiderInDeliveryActivity.this);
//                    //设置数据回调监听器
//                    mRouteSearch.setRouteSearchListener(RiderInDeliveryActivity.this);
//                    //设置搜索参数
//                    final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
//                    RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo);
//                    //发送请求
//                    mRouteSearch.calculateRideRouteAsyn(query);

                    //取出订单号
                    mOrderNumber = data.orderNumber;

                    //骑手头像
                    Glide.with(RiderInDeliveryActivity.this)
                            .load(R.mipmap.icon_rider_logo_default)
                            .into(mHeadIv);

                    //收货地址
                    mLocationTv.setText(data.receiveAdress);

                    //收货人姓名
                    mReceiveNameTv.setText(data.receiveName);

                    //收货人电话
                    mReceivePhone = data.receivePhone;

                    //去某地送货
                    mAddressTv.setText(data.receiveAdress);

                    //预计路程
                    mEstimatedDistanceTv.setText("预计路程" + ConstantUtil.setFormat("0.00",
                            Double.parseDouble(data.distance) / 1000 + "") + "km");

                    //已行驶
                    //根据骑手的经纬度，和起点的经纬度，计算已行驶距离
                    computeMovedDistance(data.sendLatitude, data.sendLongitude);

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

    /**
     * 计算已行驶距离
     *
     * @param sendLatitude
     * @param sendLongitude
     */
    private void computeMovedDistance(String sendLatitude, String sendLongitude) {
        //每隔5秒获取一次自己的经纬度
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "计算已行驶距离");
                //要执行的方法
                //获取骑手的经纬度
                getRiderLocation();

                //骑手位置（取手机当前位置）
                String latitude = (String) SPUtils.get(RiderInDeliveryActivity.this, "latitude", "");
                String longitude = (String) SPUtils.get(RiderInDeliveryActivity.this, "longitude", "");

                String distance = GPSUtil.getDistance(sendLongitude, sendLatitude, longitude, latitude);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMovedTv != null) {
                            mMovedTv.setText("已行驶" + ConstantUtil.setFormat("0.00", distance) + "km");
                        }
                    }
                });

                handler.postDelayed(this, 10 * 1000);
            }
        };

        handler.postDelayed(runnable, 1000);

    }

    /**
     * 获取骑手的经纬度
     */
    private void getRiderLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        saveLocation(location);
    }

    private Criteria getCriteria() {
        // TODO Auto-generated method stub
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setSpeedRequired(false);
        c.setCostAllowed(false);
        c.setBearingRequired(false);
        c.setAltitudeRequired(false);
        c.setPowerRequirement(Criteria.POWER_LOW);
        return c;
    }

    private void saveLocation(Location location) {
        if (location != null) {
            String latitude = location.getLatitude() + "";
            String longitude = location.getLongitude() + "";
            SPUtils.put(this, "latitude", latitude);
            SPUtils.put(this, "longitude", longitude);
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_call_phone_ll, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_call_phone_ll://拨打电话
                if (!TextUtils.isEmpty(mReceivePhone)) {
                    showConnectDialog(mReceivePhone);
                }
                break;
            case R.id.m_commit_tv://确认送达

                //确认送达
                requestUpdateMemberOrderStatus();

                break;
        }
    }

    /**
     * 确认送达
     */
    private void requestUpdateMemberOrderStatus() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderStatus = "4";
        paramInfo.orderNumber = mOrderNumber;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMemberOrderStatus(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "确认送达：" + JSON.toJSONString(response.data));
                Intent intent = new Intent();
                intent.setClass(RiderInDeliveryActivity.this, RiderConfirmDelivery.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });

    }

    private void showConnectDialog(String phoneNum) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption("联系买家")
                .setMessage("是否立即联系？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
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
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int errorCode) {
        //解析result获取算路结果，可参考官方demo
        LogUtil.d(TAG, "骑行路线：" + JSON.toJSONString(rideRouteResult));
        if (errorCode == 1000) {//1000代表成功
            List<RidePath> paths = rideRouteResult.getPaths();

            if (paths != null && paths.size() > 0) {

                RidePath ridePath = paths.get(0);
                List<LatLonPoint> polyline = ridePath.getPolyline();
                List<LatLng> allPoints = new ArrayList<>();

                for (LatLonPoint latLonPoint : polyline) {
                    LatLng latLng = new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
                    allPoints.add(latLng);
                }

                if (aMap != null) {
                    aMap.addPolyline((new PolylineOptions())
                            .color(Color.parseColor("#28C19A"))
                            .useGradient(true)
                            .geodesic(false)
                            .width(1f)
                            .addAll(allPoints)
                            .width(20f));

                }
            }
        }
    }
}
