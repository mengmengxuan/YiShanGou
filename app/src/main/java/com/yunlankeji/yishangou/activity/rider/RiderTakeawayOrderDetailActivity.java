package com.yunlankeji.yishangou.activity.rider;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.hwangjr.rxbus.RxBus;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.OrderFoodAdapter;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.ZLBusAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/2
 * Describe:骑手外卖订单详情
 */
public class RiderTakeawayOrderDetailActivity extends BaseActivity {
    private static final String TAG = "RiderTakeawayOrderDetailActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_delivery_price_tv)
    TextView mDeliveryPriceTv;//
    @BindView(R.id.m_distance_tv)
    TextView mDistanceTv;//
    @BindView(R.id.m_publish_time_tv)
    TextView mPublishTimeTv;//
    @BindView(R.id.m_delivery_name_tv)
    TextView mDeliveryNameTv;//
    @BindView(R.id.m_delivery_phone_tv)
    TextView mDeliveryPhoneTv;//
    @BindView(R.id.m_delivery_address_tv)
    TextView mDeliveryAddressTv;//
    @BindView(R.id.m_receive_name_tv)
    TextView mReceiveNameTv;//
    @BindView(R.id.m_receive_phone_tv)
    TextView mReceivePhoneTv;//
    @BindView(R.id.m_receive_address_tv)
    TextView mReceiveAddressTv;//
    @BindView(R.id.m_delivery_fee_tv)
    TextView mDeliveryFeeTv;//
    @BindView(R.id.m_remark_tv)
    TextView mRemarkTv;//
    @BindView(R.id.m_business_name_tv)
    TextView mBusinessNameTv;//
    @BindView(R.id.m_packing_fee_tv)
    TextView mPackingFeeTv;//
    @BindView(R.id.m_order_food_rv)
    RecyclerView mOrderFoodRv;//

    private String id;
    private String receiveDistance;
    private String releaseTime;
    private List<Data> detailList = new ArrayList<>();
    private OrderFoodAdapter mOrderFoodAdapter;
    private Data mData;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_takeaway_order_detail;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("订单详情");

        id = getIntent().getStringExtra("id");
        receiveDistance = getIntent().getStringExtra("receiveDistance");
        releaseTime = getIntent().getStringExtra("releaseTime");

        //距离你
        if (TextUtils.isEmpty(receiveDistance)) {
            mDistanceTv.setText("0km");
        } else {
            mDistanceTv.setText(ConstantUtil.setFormat("0.00", (Double.parseDouble(receiveDistance) / 1000) + "") +
                    "km");
        }

        //发布时间
        if (!TextUtils.isEmpty(releaseTime)) {
            if (Double.parseDouble(releaseTime) < 1) {
                mPublishTimeTv.setText("刚刚");
            } else if (Double.parseDouble(releaseTime) < 60) {
                mPublishTimeTv.setText(releaseTime + "分钟前");
            } else {
                //小时
                double hour = Double.parseDouble(releaseTime) / 60;
                mPublishTimeTv.setText(ConstantUtil.setFormat("0.00", String.valueOf(hour)) + "小时前");
            }
        }

        mOrderFoodAdapter = new OrderFoodAdapter(this);
        mOrderFoodAdapter.setItems(detailList);
        mOrderFoodRv.setLayoutManager(new LinearLayoutManager(this));
        mOrderFoodRv.setAdapter(mOrderFoodAdapter);
    }

    @Override
    public void initData() {
        //骑手订单详情
        requestQueryRiderOrderDetail();
    }

    /**
     * 骑手订单详情
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

                mData = (Data) response.data;
                //配送价格
                if (TextUtils.isEmpty(mData.shippingAccount)) {
                    mDeliveryPriceTv.setText("￥0");
                } else {
                    mDeliveryPriceTv.setText("￥" + mData.shippingAccount);
                }
                //发货人姓名
                if (!TextUtils.isEmpty(mData.sendName)) {
                    mDeliveryNameTv.setText(mData.sendName);
                }

                //发货人电话
                if (!TextUtils.isEmpty(mData.sendPhone)) {
                    mDeliveryPhoneTv.setText(mData.sendPhone);
                }

                //发货人地址
                if (!TextUtils.isEmpty(mData.sendAdress)) {
                    mDeliveryAddressTv.setText(mData.sendAdress);
                }

                //收货人姓名
                if (!TextUtils.isEmpty(mData.receiveName)) {
                    mReceiveNameTv.setText(mData.receiveName);
                }

                //收货人电话
                if (!TextUtils.isEmpty(mData.receivePhone)) {
                    mReceivePhoneTv.setText(mData.receivePhone);
                }

                //收货人地址
                if (!TextUtils.isEmpty(mData.receiveAdress)) {
                    mReceiveAddressTv.setText(mData.receiveAdress);
                }

                //店铺名称
                if (!TextUtils.isEmpty(mData.merchantName)) {
                    mBusinessNameTv.setText(mData.merchantName);
                }

                //商品
                detailList.addAll(mData.detailList);
                mOrderFoodAdapter.setItems(detailList);
                mOrderFoodAdapter.notifyDataSetChanged();

                //打包费
                if (TextUtils.isEmpty(mData.packingMoney)) {
                    mPackingFeeTv.setText("￥0");
                } else {
                    mPackingFeeTv.setText("￥" + mData.packingMoney);
                }

                //配送费
                if (!TextUtils.isEmpty(mData.shippingAccount)) {
                    mDeliveryFeeTv.setText("￥" + mData.shippingAccount);
                }

                //备注
                if (!TextUtils.isEmpty(mData.remark)) {
                    mRemarkTv.setText(mData.remark);
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
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_commit_tv:
                //确认抢单
                requestGrabbingOrder();
                break;
        }
    }

    /**
     * 抢单
     */
    private void requestGrabbingOrder() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        paramInfo.riderCode = Global.riderCode;
        LogUtil.d(TAG, "paramInfo.id --> " + paramInfo.id);
        LogUtil.d(TAG, "paramInfo.riderCode --> " + paramInfo.riderCode);

        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestGrabbingOrder(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "确认抢单：" + JSON.toJSONString(response.data));
                ToastUtil.showShort("抢单成功");

                //跳转到去取货页面
                Intent intent = new Intent();
                intent.setClass(RiderTakeawayOrderDetailActivity.this, RiderPickUpActivity.class);
                intent.putExtra("sendLatitude", mData.sendLatitude);
                intent.putExtra("sendLongitude", mData.sendLongitude);
                intent.putExtra("receiveLatitude", mData.receiveLatitude);
                intent.putExtra("receiveLongitude", mData.receiveLongitude);
                intent.putExtra("id", mData.id);
                startActivity(intent);

                RxBus.get().post(ZLBusAction.Refresh_Rider_Order_List, "Refresh_Rider_Order_List");
                finish();
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

}
