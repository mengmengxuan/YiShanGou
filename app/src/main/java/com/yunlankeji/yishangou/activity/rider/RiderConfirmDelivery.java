package com.yunlankeji.yishangou.activity.rider;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
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

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/2
 * Describe:骑手确认订单页面
 */
public class RiderConfirmDelivery extends BaseActivity {
    private static final String TAG = "RiderConfirmDelivery";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_delivery_name_tv)
    TextView mDeliveryNameTv;//
    @BindView(R.id.m_delivery_phone_tv)
    TextView mDeliveryPhoneTv;//
    @BindView(R.id.m_delivery_address_tv)
    TextView mDeliveryAddressTv;//
    @BindView(R.id.m_call_delivery_phone_iv)
    ImageView mCallDeliveryPhoneIv;//
    @BindView(R.id.m_receive_name_tv)
    TextView mReceiveNameTv;//
    @BindView(R.id.m_receive_phone_tv)
    TextView mReceivePhoneTv;//
    @BindView(R.id.m_call_receive_phone_iv)
    ImageView mCallReceivePhoneIv;//
    @BindView(R.id.m_receive_address_tv)
    TextView mReceiveAddressTv;//
    @BindView(R.id.m_price_tv)
    TextView mPriceTv;//
    @BindView(R.id.m_total_distance_tv)
    TextView mTotalDistanceTv;//
    @BindView(R.id.m_commit_tv)
    TextView mCommitTv;//

    private String id;
    private String mSendPhone;
    private String mReceivePhone;
    private String mOrderNumber;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_confirm_delivery;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("确认送达");
        id = getIntent().getStringExtra("id");
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
                    //取出订单号
                    mOrderNumber = data.orderNumber;

                    //发货人姓名
                    mDeliveryNameTv.setText(data.sendName);
                    //发货人电话
                    //取出发货人电话
                    mSendPhone = data.sendPhone;
                    mDeliveryPhoneTv.setText(data.sendPhone);
                    //发货人地址
                    mDeliveryAddressTv.setText(data.sendAdress);

                    //收货人姓名
                    mReceiveNameTv.setText(data.receiveName);
                    //收货人电话
                    //取出收货人电话
                    mReceivePhone = data.receivePhone;
                    mReceivePhoneTv.setText(data.receivePhone);
                    //收货人地址
                    mReceiveAddressTv.setText(data.receiveAdress);

                    //本次收款
                    mPriceTv.setText("￥" + data.shippingAccount);

                    //总行程
                    mTotalDistanceTv.setText("总行程" + ConstantUtil.setFormat("0.00",
                            Double.parseDouble(data.distance) / 1000 + "") + "km");

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

    @OnClick({R.id.m_back_iv, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_commit_tv:
                //完成
                requestUpdateMemberOrderStatus();
                break;
        }
    }

    /**
     * 完成
     */
    private void requestUpdateMemberOrderStatus() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
//        paramInfo.orderStatus = "5";
        paramInfo.riderStatus = "1";
        paramInfo.orderNumber = mOrderNumber;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMemberOrderStatus(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "完成：" + JSON.toJSONString(response.data));
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

}
