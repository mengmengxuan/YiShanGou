package com.yunlankeji.yishangou.activity.rider;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
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
 * Describe:去取货页面
 */
public class RiderPickUpActivity extends BaseActivity implements RouteSearch.OnRouteSearchListener {
    private static final String TAG = "RiderPickUpActivity";
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
    @BindView(R.id.m_address_tv)
    TextView mAddressTv;//
    @BindView(R.id.m_merchant_name_tv)
    TextView mMerchantNameTv;//
    @BindView(R.id.m_commit_tv)
    TextView mCommitTv;//

    private RouteSearch mRouteSearch;
    private AMap aMap;
    private String id;
    private String mSendPhone;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_pick_up;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {

        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("去取货");
        aMap = mapView.getMap();

        //发货地址
        String sendLatitude = getIntent().getStringExtra("sendLatitude");
        String sendLongitude = getIntent().getStringExtra("sendLongitude");
        //添加发货地址的marker
        addSendAddressMarkersToMap(Double.parseDouble(sendLatitude), Double.parseDouble(sendLongitude));

        //收货地址
        String receiveLatitude = getIntent().getStringExtra("receiveLatitude");
        String receiveLongitude = getIntent().getStringExtra("receiveLongitude");
        //添加收货地址的marker
        addReceiveAddressMarkersToMap(Double.parseDouble(receiveLatitude), Double.parseDouble(receiveLongitude));

        //骑手位置（取手机当前位置）
        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");

        //订单id
        id = getIntent().getStringExtra("id");

        LatLonPoint mStartPoint = new LatLonPoint(Double.parseDouble(sendLatitude), Double.parseDouble(sendLongitude));
        LatLonPoint mEndPoint = new LatLonPoint(Double.parseDouble(receiveLatitude), Double.parseDouble(receiveLongitude));

        LatLng latLng = new LatLng(Double.parseDouble(sendLatitude), Double.parseDouble(sendLongitude));

        if (aMap != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        }

        /*//初始化 RouteSearch 对象
        mRouteSearch = new RouteSearch(this);
        //设置数据回调监听器
        mRouteSearch.setRouteSearchListener(this);
        //设置搜索参数
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo);
        //发送请求
        mRouteSearch.calculateRideRouteAsyn(query);*/
    }

    @Override
    public void initData() {
        //获取订单详情
        requestQueryRiderOrderDetail();

    }

    /**
     * 获取订单详情
     */
    private void requestQueryRiderOrderDetail() {
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
                    //骑手头像
                    Glide.with(RiderPickUpActivity.this)
                            .load(R.mipmap.icon_rider_logo_default)
                            .into(mHeadIv);

                    //地址
                    mLocationTv.setText(data.sendAdress);

                    //去某地取货
                    mAddressTv.setText(data.sendAdress);

                    //店铺名称
                    mMerchantNameTv.setText(data.merchantName);

                    //电话
                    mSendPhone = data.sendPhone;
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
     * 添加发货地址的marker
     *
     * @param sendLatitude
     * @param sendLongitude
     */
    private void addSendAddressMarkersToMap(double sendLatitude, double sendLongitude) {
        LatLng CHENGDU = new LatLng(sendLatitude, sendLongitude);
        Marker marker = aMap.addMarker(new MarkerOptions().position(CHENGDU).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.icon_starting_point))
                .draggable(false));
    }

    @OnClick({R.id.m_back_iv, R.id.m_commit_tv, R.id.m_call_phone_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_call_phone_ll://拨打电话
                if (!TextUtils.isEmpty(mSendPhone)) {
                    showConnectDialog(mSendPhone);
                }
                break;
            case R.id.m_commit_tv://确认取货
                Intent intent = new Intent();
                intent.setClass(RiderPickUpActivity.this, RiderConfirmOrderActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void showConnectDialog(String phoneNum) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption("联系商家")
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

    /**
     * 公交路线
     *
     * @param busRouteResult
     * @param errorCode
     */
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int errorCode) {

    }

    /**
     * 驾车路线
     *
     * @param driveRouteResult
     * @param errorCode
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {

    }

    /**
     * 步行路线
     *
     * @param walkRouteResult
     * @param errorCode
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {

    }

    /**
     * 骑行路线
     *
     * @param rideRouteResult
     * @param errorCode
     */
    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int errorCode) {
        //解析result获取算路结果，可参考官方demo
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
