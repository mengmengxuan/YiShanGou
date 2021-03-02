package com.yunlankeji.yishangou.activity.order;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
 * Create by Snooker on 2021/1/11
 * Describe:跑腿订单详情
 */
public class RunErrandsOrderDetailActivity extends BaseActivity {
    private static final String TAG = "RunErrandsOrderDetailActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_map_ll)
    LinearLayout mMapLl;
    @BindView(R.id.m_delivery_status_ll)
    LinearLayout mDeliveryStatusLl;
    @BindView(R.id.m_order_status_tv)
    TextView mOrderStatusTv;
    @BindView(R.id.m_tip_tv)
    TextView mTipTv;
    @BindView(R.id.m_status_tv)
    TextView mStatusTv;
    @BindView(R.id.m_address_from_tv)
    TextView m_address_from_tv;
    @BindView(R.id.m_address_to_tv)
    TextView m_address_to_tv;
    @BindView(R.id.m_price_tv)
    TextView m_price_tv;
    @BindView(R.id.m_goods_type_tv)
    TextView m_goods_type_tv;
    @BindView(R.id.m_stroke_tv)
    TextView m_stroke_tv;
    @BindView(R.id.m_weight_tv)
    TextView m_weight_tv;
    @BindView(R.id.m_remark_tv)
    TextView m_remark_tv;
    @BindView(R.id.m_order_number_tv)
    TextView m_order_number_tv;
    @BindView(R.id.m_create_time_tv)
    TextView m_create_time_tv;
    @BindView(R.id.m_pay_type_tv)
    TextView m_pay_type_tv;

    private String orderNumber;
    private String mSendDistance;
    private String mReceiveDistance;
    private String mOrderStatus;
    private String riderPhone;

    @Override
    public int setLayout() {
        return R.layout.activity_run_errands_order_detail;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("订单详情");

        orderNumber = getIntent().getStringExtra("orderNumber");

    }

    @Override
    public void initData() {
        requestQueryOrderDetail();
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

                    //跑腿单
                    //不显示地图
                    mMapLl.setVisibility(View.GONE);
                    //显示状态
                    mDeliveryStatusLl.setVisibility(View.VISIBLE);
                    mOrderStatusTv.setText("订单已完成");
                    mTipTv.setVisibility(View.GONE);
                    mStatusTv.setText("订单已完成");
                    mStatusTv.setTextColor(getResources().getColor(R.color.color_999999));
                    //获取骑手电话
                    riderPhone = data.riderPhone;

                    //发货地
                    m_address_from_tv.setText(data.sendAdress);
                    //收货地
                    m_address_to_tv.setText(data.receiveAdress);
                    //物品
                    m_goods_type_tv.setText(data.goodsType);
                    //行程
                    m_stroke_tv.setText(ConstantUtil.setFormat("0.00", Double.parseDouble(data.distance) / 1000 + "") + "km");
                    //重量
                    m_weight_tv.setText(data.weight + "kg");
                    //价格
                    m_price_tv.setText("￥" + data.shippingAccount);

                    //备注
                    m_remark_tv.setText(data.remark);
                    //订单编号
                    m_order_number_tv.setText(data.orderNumber);
                    //下单时间
                    m_create_time_tv.setText(ConstantUtil.convertDate(Long.parseLong(data.createDt) / 1000 + "",
                            "yyyy.MM.dd HH:mm:ss"));

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

    @OnClick({R.id.m_back_iv, R.id.m_connect_rider_new_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_connect_rider_new_ll:
                showConnectDialog("联系骑手", riderPhone);
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
}
