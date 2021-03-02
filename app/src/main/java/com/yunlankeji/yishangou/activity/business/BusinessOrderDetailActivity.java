package com.yunlankeji.yishangou.activity.business;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hwangjr.rxbus.RxBus;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.OrderFoodAdapter;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.dialog.LoadingDialog;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class BusinessOrderDetailActivity extends AppCompatActivity {

    private static final String TAG = "BusinessOrderDetailActivity";

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
    @BindView(R.id.m_name_tv)
    TextView mNameTv;
    @BindView(R.id.m_phone_tv)
    TextView mPhoneTv;
    @BindView(R.id.m_address_tv)
    TextView mAddressTv;
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
    @BindView(R.id.map)
    MapView mapView;

    List<Data> foodList = new ArrayList<>();
    private String orderNumber;
    private String receivePhone;
    private String riderPhone;
    private Data order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_business_order_detail);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);
        mapView.getMap().moveCamera(CameraUpdateFactory.zoomTo(18));
        ConstantUtil.setStatusBarFullTransparent(this);
        //设置状态栏文字颜色及图标为深色
        getWindow().getDecorView().setSystemUiVisibility(View
                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        initView();
        initData();
    }

    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("订单详情");
        orderNumber = getIntent().getStringExtra("orderNumber");

    }

    public void initData() {
        requestQueryOrderDetail();
    }

    @OnClick({R.id.m_back_iv, R.id.m_connect_business_ll, R.id.m_connect_business_new_ll, R.id.m_connect_rider_ll, R.id.m_connect_rider_new_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_connect_business_ll://联系商家
                showConnectDialog("联系买家", receivePhone);
                break;
            case R.id.m_connect_business_new_ll://联系商家
                showConnectDialog("联系买家", receivePhone);
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
                LogUtil.d(TAG, "订单列表：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    order = data;
                    // 0待派单 1待接单 2待取货 3待配送 4待收货 5已完成 6已取消

                    if (order.orderStatus.equals("0")) {
                        mDeliveryStatusLl.setVisibility(View.VISIBLE);
                        mOrderStatusTv.setText("订单已支付");
                        mTipTv.setText("正在等待派单");
                        mStatusTv.setText("待派单");
                        mStatusTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                        mGetConnectLl.setVisibility(View.VISIBLE);
                    } else if (order.orderStatus.equals("1")) {
                        mDeliveryStatusLl.setVisibility(View.VISIBLE);
                        mOrderStatusTv.setText("订单已派单");
                        mTipTv.setText("正在等待骑手接单,请耐心等待");
                        mStatusTv.setText("待接单");
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
                        mStatusTv.setText("配送中");
                        mStatusTv.setTextColor(getResources().getColor(R.color.color_F36C17));
                        mMapLl.setVisibility(View.VISIBLE);
                        mRiderNameTv.setText("配送员" + order.riderName + "正在为您配送中");
                    }
                    mNameTv.setText(order.receiveName);
                    mPhoneTv.setText(order.receivePhone);
                    mAddressTv.setText(order.receiveAdress);
                    if (TextUtils.isEmpty(order.packingMoney)) {
                        mPackingFeeTv.setText("￥0");
                    } else {
                        mPackingFeeTv.setText("￥" + order.packingMoney);
                    }
                    mDeliveryFeeTv.setText("￥" + order.shippingAccount);
                    mRemarkTv.setText(order.remark);
                    mOrderNumberTv.setText(order.orderNumber);
                    mCreateTimeTv.setText(ConstantUtil.convertDate(Long.parseLong(order.createDt) / 1000 + "",
                            "yyyy.MM.dd HH:mm:ss"));

                    OrderFoodAdapter orderFoodAdapter = new OrderFoodAdapter(BusinessOrderDetailActivity.this);
                    orderFoodAdapter.setItems(order.detailList);
                    mOrderFoodRv.setLayoutManager(new LinearLayoutManager(BusinessOrderDetailActivity.this));
                    mOrderFoodRv.setAdapter(orderFoodAdapter);
                    receivePhone = order.receivePhone;
                    riderPhone = order.riderPhone;

                    if ((!TextUtils.isEmpty(order.receiveLatitude)) && !(TextUtils.isEmpty(order.receiveLongitude))) {
                        setReceivePosition(Double.parseDouble(order.receiveLatitude), Double.parseDouble(order.receiveLongitude));
                    }
                    if ((!TextUtils.isEmpty(order.riderLatitude)) && !(TextUtils.isEmpty(order.riderLongitude))) {
                        setRiderPosition(Double.parseDouble(order.riderLatitude), Double.parseDouble(order.riderLongitude));
                    }
                    if ((!TextUtils.isEmpty(order.sendLatitude)) && !(TextUtils.isEmpty(order.sendLongitude))) {
                        setSendPosition(Double.parseDouble(order.sendLatitude), Double.parseDouble(order.sendLongitude));
                    }
                }

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

    public void setSendPosition(double latitude, double longitude) {
        LatLng sendLatLng = new LatLng(latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.title("商家");
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.position(sendLatLng);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_position));
        //设置覆盖物比例
        markerOption.anchor(0.5f, 0.5f);
        Marker sendMarker = mapView.getMap().addMarker(markerOption);
        sendMarker.showInfoWindow();
    }

    public void setReceivePosition(double latitude, double longitude) {
        LatLng receiveLatLng = new LatLng(22.56686, 114.170988);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.title("买家");
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.position(receiveLatLng);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_position));
        //设置覆盖物比例
        markerOption.anchor(0.5f, 0.5f);
        Marker receiveMarker = mapView.getMap().addMarker(markerOption);
        receiveMarker.showInfoWindow();
    }

    public void setRiderPosition(double latitude, double longitude) {
        LatLng riderLatLng = new LatLng(22.56686, 114.170988);
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.title("骑手");
        markerOption.draggable(false);//设置Marker可拖动
        markerOption.position(riderLatLng);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_map_position));
        //设置覆盖物比例
        markerOption.anchor(0.5f, 0.5f);
        Marker riderMarker = mapView.getMap().addMarker(markerOption);
        riderMarker.showInfoWindow();
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
        mapView.onDestroy();
    }

}
