package com.yunlankeji.yishangou.activity.order;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.OrderFoodAdapter;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class OrderDetailActivity extends BaseActivity implements AMap.InfoWindowAdapter {

    private static final String TAG = "OrderDetailActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_order_food_rv)
    RecyclerView mOrderFoodRv;//订单商品列表
    @BindView(R.id.m_delivery_status_ll)
    LinearLayout mDeliveryStatusLl;
    @BindView(R.id.m_map_ll)
    LinearLayout mMapLl;
    @BindView(R.id.m_get_connect_ll)
    LinearLayout mGetConnectLl;
    @BindView(R.id.m_order_status_tv)
    TextView mOrderStatusTv;
    @BindView(R.id.m_tip_tv)
    TextView mTipTv;
    @BindView(R.id.m_rider_name_tv)
    TextView mRiderNameTv;
    @BindView(R.id.m_business_name_tv)
    TextView mBusinessNameTv;
    @BindView(R.id.m_status_tv)
    TextView mStatusTv;
    @BindView(R.id.m_delivery_fee_tv)
    TextView mDeliveryFeeTv;
    @BindView(R.id.m_remark_tv)
    TextView mRemarkTv;
    @BindView(R.id.m_order_number_tv)
    TextView mOrderNumberTv;
    @BindView(R.id.m_create_time_tv)
    TextView mCreateTimeTv;
    @BindView(R.id.m_packing_fee_tv)
    TextView mPackingFeeTv;
    @BindView(R.id.m_connect_business_ll)
    LinearLayout mConnectBusinessLl;
    @BindView(R.id.map_view)
    MapView mapView;

    List<Data> foodList = new ArrayList<>();
    private String orderNumber;
    private String businessPhone;
    private String riderPhone;
    private Data order;
    private AMap aMap;
    private Runnable runnable;
    private Handler handler = new Handler();
    private String mSendDistance;
    private String mReceiveDistance;
    private String mOrderStatus;
    private Marker mRiderMarker;
    private Marker mSendMarker;
    private Marker mReceiveMarker;

    @Override
    public int setLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("订单详情");
        aMap = mapView.getMap();

        orderNumber = getIntent().getStringExtra("orderNumber");
    }

    @Override
    public void initData() {
        super.initData();
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

    @OnClick({R.id.m_back_iv, R.id.m_connect_business_ll, R.id.m_connect_business_new_ll, R.id.m_connect_rider_ll, R.id.m_connect_rider_new_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_connect_business_ll://联系商家
                showConnectDialog("联系商家", businessPhone);
                break;
            case R.id.m_connect_business_new_ll://联系商家
                showConnectDialog("联系商家", businessPhone);
                break;
            case R.id.m_connect_rider_ll://联系骑手
                if (TextUtils.isEmpty(riderPhone)) {
                    ToastUtil.showShort("正在等待骑手接单");
                } else {
                    showConnectDialog("联系骑手", riderPhone);
                }
                break;
            case R.id.m_connect_rider_new_ll://联系骑手
                if (TextUtils.isEmpty(riderPhone)) {
                    ToastUtil.showShort("正在等待骑手接单");
                } else {
                    showConnectDialog("联系骑手", riderPhone);
                }
                break;
        }
    }

    //联系商家(骑手)
    private void showConnectDialog(String title, String phoneNum) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption(title)
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
                LogUtil.d(TAG, "订单列表：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {

                    //骑手距发货地的距离
                    mSendDistance = data.sendDistance;
                    //骑手距收货地的距离
                    mReceiveDistance = data.receiveDistance;
                    //订单状态
                    mOrderStatus = data.orderStatus;

                    //根据orderType判断是外卖单还是跑腿单
                    if (data.orderType.equals("0")) {
                        //外卖单
                        OrderFoodAdapter orderFoodAdapter = new OrderFoodAdapter(OrderDetailActivity.this);
                        orderFoodAdapter.setItems(data.detailList);
                        mOrderFoodRv.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
                        mOrderFoodRv.setAdapter(orderFoodAdapter);
                        businessPhone = data.sendPhone;
                        riderPhone = data.riderPhone;

                        mBusinessNameTv.setText(data.merchantName);
                        mPackingFeeTv.setText("￥" + data.packingMoney);
                        mDeliveryFeeTv.setText("￥" + data.shippingAccount);
                        mRemarkTv.setText(data.remark);
                        mOrderNumberTv.setText(data.orderNumber);
                        mCreateTimeTv.setText(ConstantUtil.convertDate(Long.parseLong(data.createDt) / 1000 + "",
                                "yyyy.MM.dd HH:mm:ss"));

                        //0 待派单  1 待接单  2 待取货  3 代配送  4 待收货  5 已完成  6 已取消
                        switch (data.orderStatus) {
                            case "0":
                                //不显示地图
                                mMapLl.setVisibility(View.GONE);
                                //隐藏联系骑手联系商家按钮
                                mGetConnectLl.setVisibility(View.GONE);
                                //显示状态
                                mDeliveryStatusLl.setVisibility(View.VISIBLE);
                                mOrderStatusTv.setText("商家准备中");
                                mTipTv.setText("商家准备中，请耐心等待");
                                mStatusTv.setText("进行中");
                                mStatusTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                                break;
                            case "1":
                                //不显示地图
                                mMapLl.setVisibility(View.GONE);
                                //隐藏联系骑手联系商家按钮
                                mGetConnectLl.setVisibility(View.GONE);
                                //显示状态
                                mDeliveryStatusLl.setVisibility(View.VISIBLE);
                                mOrderStatusTv.setText("等待接单");
                                mTipTv.setText("正在等待骑手接单,请耐心等待");
                                mStatusTv.setText("进行中");
                                mStatusTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                                break;
                            case "2"://待取货
                                //显示地图
                                mMapLl.setVisibility(View.VISIBLE);
                                //隐藏联系骑手联系商家按钮
                                mGetConnectLl.setVisibility(View.GONE);
                                //隐藏状态
                                mDeliveryStatusLl.setVisibility(View.GONE);
                                mStatusTv.setText("进行中");
                                mRiderNameTv.setText("配送员" + data.riderName + "正在为您配送中");

                                //显示骑手与商家的距离
                                //显示骑手的marker
                                if (mRiderMarker != null) {
                                    mRiderMarker.remove();
                                }
                                addRiderAddressMarkersToMap(Double.parseDouble(data.riderLatitude),
                                        Double.parseDouble(data.riderLongitude));

                                //显示商家的marker，也就是发货地的marker
                                if (mSendMarker != null) {
                                    mSendMarker.remove();
                                }
                                //添加发货地址的marker
                                addSendAddressMarkersToMap(Double.parseDouble(data.sendLatitude), Double.parseDouble(data.sendLongitude));

                                break;
                            case "3":
                            case "4":
                                //显示地图
                                mMapLl.setVisibility(View.VISIBLE);
                                //隐藏联系骑手联系商家按钮
                                mGetConnectLl.setVisibility(View.GONE);
                                //隐藏状态
                                mDeliveryStatusLl.setVisibility(View.GONE);
                                mStatusTv.setText("进行中");
                                mRiderNameTv.setText("配送员" + data.riderName + "正在为您配送中");

                                //显示骑手与买家的距离
                                //显示骑手的marker
                                if (mRiderMarker != null) {
                                    mRiderMarker.remove();
                                }
                                addRiderAddressMarkersToMap(Double.parseDouble(data.riderLatitude),
                                        Double.parseDouble(data.riderLongitude));

                                //显示买家的marker，也就是收货地的marker
                                if (mReceiveMarker != null) {
                                    mReceiveMarker.remove();
                                }
                                //添加发货地址的marker
                                addSendAddressMarkersToMap(Double.parseDouble(data.receiveLatitude),
                                        Double.parseDouble(data.receiveLongitude));
                                break;
                            case "5":
                                //不显示地图
                                mMapLl.setVisibility(View.GONE);
                                //显示状态
                                mDeliveryStatusLl.setVisibility(View.VISIBLE);
                                mOrderStatusTv.setText("订单已完成");
                                mTipTv.setText("感谢您的信任，期待再次光临");
                                mStatusTv.setText("已完成");
                                mStatusTv.setTextColor(getResources().getColor(R.color.color_999999));
                                mGetConnectLl.setVisibility(View.VISIBLE);
                                break;
                            case "6":
                                //不显示地图
                                mMapLl.setVisibility(View.GONE);
                                break;
                        }

                        if (!TextUtils.isEmpty(data.riderLatitude) && !TextUtils.isEmpty(data.riderLongitude)) {
                            LatLng latLng = new LatLng(Double.parseDouble(data.riderLatitude), Double.parseDouble(data.riderLongitude));

                            if (aMap != null) {
                                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
                            }
                        }

                    } else if (data.orderType.equals("1")) {
                        //跑腿单
                        //不显示地图
                        mMapLl.setVisibility(View.GONE);
                        //显示状态
                        mDeliveryStatusLl.setVisibility(View.VISIBLE);
                        mOrderStatusTv.setText("订单已完成");
                        mTipTv.setVisibility(View.GONE);
                        mStatusTv.setText("已完成");
                        mStatusTv.setTextColor(getResources().getColor(R.color.color_999999));
                        mGetConnectLl.setVisibility(View.VISIBLE);
                        mConnectBusinessLl.setVisibility(View.GONE);

                    }

              /*
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
                    } else if (data.orderStatus.equals("3")) {
                        //正在赶来（送货）
                        //添加收货地址的marker
                        if (mReceiveMarker != null) {
                            mReceiveMarker.remove();
                        }
                        addReceiveAddressMarkersToMap(Double.parseDouble(data.receiveLatitude), Double.parseDouble(data.receiveLongitude));
                    }

                    LatLng latLng = new LatLng(Double.parseDouble(data.riderLatitude), Double.parseDouble(data.riderLongitude));

                    if (aMap != null) {
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f));
                    }

                    order = data;
                    // 0未支付 1待接单 2待取货 3待配送 4待收货 5已完成 6已取消
                    if (order.orderStatus.equals("1")) {
                        mDeliveryStatusLl.setVisibility(View.VISIBLE);
                        mOrderStatusTv.setText("订单已支付");
                        mTipTv.setText("正在等待骑手接单,请耐心等待");
                        mStatusTv.setText("进行中");
                        mStatusTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                        mGetConnectLl.setVisibility(View.VISIBLE);
                    } else if (order.orderStatus.equals("6")) {
                        mDeliveryStatusLl.setVisibility(View.VISIBLE);
                        mOrderStatusTv.setText("订单已取消");
                        mStatusTv.setText("已取消");
                        mStatusTv.setTextColor(getResources().getColor(R.color.color_999999));
                    } else if (order.orderStatus.equals("5")) {
                        mDeliveryStatusLl.setVisibility(View.VISIBLE);
                        mOrderStatusTv.setText("订单已完成");
                        mStatusTv.setText("已完成");
                        mStatusTv.setTextColor(getResources().getColor(R.color.color_999999));
                        mGetConnectLl.setVisibility(View.VISIBLE);
                    } else {
                        mStatusTv.setText("进行中");
                        mStatusTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                        mMapLl.setVisibility(View.VISIBLE);
                        mRiderNameTv.setText("配送员" + order.riderName + "正在为您配送中");
                    }

                    mBusinessNameTv.setText(order.merchantName);
                    mDeliveryFeeTv.setText("￥" + order.shippingAccount);
                    mRemarkTv.setText(order.remark);
                    mOrderNumberTv.setText(order.orderNumber);
                    mCreateTimeTv.setText(ConstantUtil.convertDate(Long.parseLong(order.createDt) / 1000 + "",
                            "yyyy.MM.dd HH:mm:ss"));

                    OrderFoodAdapter orderFoodAdapter = new OrderFoodAdapter(OrderDetailActivity.this);
                    orderFoodAdapter.setItems(order.detailList);
                    mOrderFoodRv.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
                    mOrderFoodRv.setAdapter(orderFoodAdapter);
                    businessPhone = order.sendPhone;
                    riderPhone = order.riderPhone;*/
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

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.infowindow_rider_distance, null);
        //创建我们需要的标记样式
        //然后进行赋值
        render(marker, infoWindow);
        return infoWindow;
    }

    private void render(Marker marker, View infoWindow) {
        TextView mRiderDistanceTv = infoWindow.findViewById(R.id.m_rider_distance_tv);
        if (mOrderStatus.equals("2")) {
            // 正在赶来（取货）
            mRiderDistanceTv.setText("配送员距商家" + ConstantUtil.setFormat("0.00", mSendDistance) + "km");
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
}
