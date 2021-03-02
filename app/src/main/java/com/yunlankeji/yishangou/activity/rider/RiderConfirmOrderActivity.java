package com.yunlankeji.yishangou.activity.rider;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/2
 * Describe:骑手确认订单页面
 */
public class RiderConfirmOrderActivity extends BaseActivity {
    private static final String TAG = "RiderConfirmOrderActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root

    @BindView(R.id.m_send_name_tv)
    TextView mSendNameTv;//
    @BindView(R.id.m_send_phone_tv)
    TextView mSendPhoneTv;//
    @BindView(R.id.m_send_address_tv)
    TextView mSendAddressTv;//
    @BindView(R.id.m_receive_name_tv)
    TextView mReceiveNameTv;//
    @BindView(R.id.m_receive_phone_tv)
    TextView mReceivePhoneTv;//
    @BindView(R.id.m_receive_address_tv)
    TextView mReceiveAddressTv;//
    @BindView(R.id.m_total_distance_tv)
    TextView mTotalDistanceTv;//
    @BindView(R.id.m_weight_tv)
    TextView mWeightTv;//
    @BindView(R.id.m_less_iv)
    ImageView mLessIv;//
    @BindView(R.id.m_add_iv)
    ImageView mAddIv;//
    @BindView(R.id.m_total_price_tv)
    TextView mTotalPriceTv;//
    @BindView(R.id.m_remark_tv)
    TextView mRemarkTv;//
    @BindView(R.id.m_weight_rl)
    RelativeLayout mWeightRl;//

    private String id;
    private String mOrderNumber;
    private String mOrderType;

    @Override
    public int setLayout() {
        return R.layout.activity_rider_confirm_order;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("确认订单");

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

                    //重量（只有跑腿订单有重量）
                    mOrderType = data.orderType;
                    if ("0".equals(data.orderType)) {
                        //外卖
                        mWeightRl.setVisibility(View.GONE);
                    } else if ("1".equals(data.orderType)) {
                        //跑腿
                        mWeightRl.setVisibility(View.VISIBLE);
                    }

                    //发货人姓名
                    mSendNameTv.setText(data.sendName);

                    //发货人电话
                    mSendPhoneTv.setText(data.sendPhone);

                    //发货人地址
                    mSendAddressTv.setText(data.sendAdress);

                    //收货人姓名
                    mReceiveNameTv.setText(data.receiveName);

                    //收货人电话
                    mReceivePhoneTv.setText(data.receivePhone);

                    //收货人地址
                    mReceiveAddressTv.setText(data.receiveAdress);

                    //总路程
                    mTotalDistanceTv.setText(ConstantUtil.setFormat("0.00", Double.parseDouble(data.distance) / 1000 + "") + "公里");

                    //总金额
                    if (TextUtils.isEmpty(data.orderAccount)) {
                        mTotalPriceTv.setText("￥0.0");
                    } else {
                        mTotalPriceTv.setText("￥" + data.orderAccount);
                    }

                    //备注
                    mRemarkTv.setText(data.remark);

                    //订单号
                    mOrderNumber = data.orderNumber;

                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                com.yunlankeji.yishangou.utils.ToastUtil.showShortForNet(msg);
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

    @OnClick({R.id.m_back_iv, R.id.m_less_iv, R.id.m_add_iv, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_less_iv://重量减
                int weight1 = Integer.parseInt(mWeightTv.getText().toString());
                if (weight1 <= 1) {
                    weight1 = 1;
                } else {
                    weight1--;
                }
                mWeightTv.setText(weight1 + "");

                requestGetMoney(mWeightTv.getText().toString());

                break;
            case R.id.m_add_iv://重量加
                int weight2 = Integer.parseInt(mWeightTv.getText().toString());
                weight2++;
                mWeightTv.setText(weight2 + "");

                requestGetMoney(mWeightTv.getText().toString());

                break;
            case R.id.m_commit_tv://提交订单
                String weight = mWeightTv.getText().toString();
                //确认取货
                requestUpdateMemberOrderStatus(weight);
                break;
        }
    }

    /**
     * 加件商品重量
     *
     * @param weight
     */
    private void requestGetMoney(String weight) {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = mOrderNumber;
        paramInfo.weight = weight;

        LogUtil.d(TAG, "weight --> " + weight);

        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestGetMoney(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "加减重量：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;
                if (data != null) {

                    //重量（只有跑腿订单有重量）
                    mOrderType = data.orderType;
                    if ("0".equals(data.orderType)) {
                        //外卖
                        mWeightRl.setVisibility(View.GONE);
                    } else if ("1".equals(data.orderType)) {
                        //跑腿
                        mWeightRl.setVisibility(View.VISIBLE);
                    }

                    //总金额
                    if (TextUtils.isEmpty(data.orderAccount)) {
                        mTotalPriceTv.setText("￥0.0");
                    } else {
                        mTotalPriceTv.setText("￥" + data.orderAccount);
                    }

                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShort(msg);

            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShort(msg);

            }
        });

    }

    /**
     * 确认取货
     *
     * @param weight
     */
    private void requestUpdateMemberOrderStatus(String weight) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderStatus = "3";
        if ("1".equals(mOrderType)) {
            //跑腿
            paramInfo.weight = weight;
        }
        paramInfo.orderNumber = mOrderNumber;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMemberOrderStatus(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "确认取货：" + JSON.toJSONString(response.data));

                ToastUtil.showShort("取货成功");

                Intent intent = new Intent();
                intent.setClass(RiderConfirmOrderActivity.this, RiderInDeliveryActivity.class);
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

}

