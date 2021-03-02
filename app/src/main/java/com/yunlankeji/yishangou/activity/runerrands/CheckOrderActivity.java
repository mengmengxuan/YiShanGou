package com.yunlankeji.yishangou.activity.runerrands;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.order.OrderDetailActivity;
import com.yunlankeji.yishangou.bean.PayResult;
import com.yunlankeji.yishangou.bean.WeChatPayBean;
import com.yunlankeji.yishangou.network.Api;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.IsInstallWeChatOrAliPay;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.ZLBusAction;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CheckOrderActivity extends BaseActivity {

    private static final String TAG = "CheckOrderActivity";
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
    @BindView(R.id.m_distance_tv)
    TextView mDistanceTv;//
    @BindView(R.id.m_weight_tv)
    TextView mWeightTv;//
    @BindView(R.id.m_remark_tv)
    TextView mRemarkTv;//
    @BindView(R.id.m_price_tv)
    TextView mPriceTv;//
    @BindView(R.id.m_commit_tv)
    TextView mCommitTv;//
    @BindView(R.id.m_is_alipay_cb)
    CheckBox mIsAlipayCb;//
    @BindView(R.id.m_is_wechat_cb)
    CheckBox mIsWechatCb;//

    private String orderNumber;
    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    LogUtil.d(TAG, "PayResult:" + JSON.toJSONString(payResult));
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showShort("支付成功");
                        //获取订单详情并跳转页面
                        requestQueryOrderDetailAndJumpActivity();

//                        doActivity(CheckOrderActivity.this, ComingActivity.class, orderNumber, "orderNumber");
                        finish();
//                        showAlert(WaitPayOrderDetailActivity.this, "支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(CheckOrderActivity.this, payResult.getMemo());
                    }
                    break;
            }
        }
    };

    /**
     * 跳转页面
     */
    private void requestQueryOrderDetailAndJumpActivity() {

        showLoading();
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
                    //0 待派单  1 待接单  2 待取货  3 代配送  4 待收货  5 已完成  6 已取消
                    switch (data.orderStatus) {
                        case "1"://待接单
                            doActivity(CheckOrderActivity.this, WaitActivity.class);
                            break;
                        case "2"://待取货
                        case "3"://待配送
                            doActivity(CheckOrderActivity.this, ComingActivity.class, orderNumber, "orderNumber");
                            break;
                        case "4"://待收货，跳转到已送达页面
                            doActivity(CheckOrderActivity.this, RunErrandsSendedActivity.class, orderNumber, "orderNumber");
                            break;
                        case "5"://已完成，跳转到详情页面
                            doActivity(CheckOrderActivity.this, OrderDetailActivity.class, orderNumber, "orderNumber");
                            break;
                        case "6":
                            break;
                    }
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

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("确认", null)
                .setOnDismissListener(onDismiss)
                .show();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_check_order;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("确认订单");

        orderNumber = getIntent().getStringExtra("orderNumber");

        //支付宝
        mIsAlipayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsWechatCb.setChecked(false);
                }
            }
        });

        //微信
        mIsWechatCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mIsAlipayCb.setChecked(false);
                }
            }
        });

    }

    @Override
    public void initData() {
        //获取订单详情
        requestQueryOrderDetail();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Pay_Success)})
    public void paySuccess(String status) {
        if ("Pay_Success".equals(status)) {
            finish();
        }
    }

    /**
     * 获取订单详情
     */
    private void requestQueryOrderDetail() {
        showLoading();
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
                    mDistanceTv.setText(ConstantUtil.setFormat("0.00", Double.parseDouble(data.distance) / 1000 + "") +
                            "公里");

                    //重量
                    mWeightTv.setText(data.weight + "kg");

                    //备注
                    mRemarkTv.setText(data.remark);

                    //价格
                    mPriceTv.setText(data.orderAccount);
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
            case R.id.m_commit_tv://立即支付

                boolean isAlipayCbChecked = mIsAlipayCb.isChecked();
                boolean isWechatCbChecked = mIsWechatCb.isChecked();

                if (!isAlipayCbChecked && !isWechatCbChecked) {
                    ToastUtil.showShort("请选择支付方式");
                    return;
                }

                if (isAlipayCbChecked && !isWechatCbChecked) {
                    //支付宝支付
                    requestAliPay();
                }

                if (isWechatCbChecked && !isAlipayCbChecked) {
                    //微信支付
                    requestWeChatPay();
                }
                break;
        }
    }

    /**
     * 微信支付
     */
    private void requestWeChatPay() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = orderNumber;

        Call<ResponseBean<WeChatPayBean>> call = NetWorkManager.getInstance().getRequest().requestWeChatPay(paramInfo);
        HttpRequestUtil.httpRequestWeChatPay(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "微信支付：" + JSON.toJSONString(response.data));

                if (IsInstallWeChatOrAliPay.isWeixinAvilible(CheckOrderActivity.this)) {
                    WeChatPayBean data = (WeChatPayBean) response.data;

                    WeChatPayBean.WeChatPayData orderInfo = data.data;
                    try {
                        IWXAPI api = WXAPIFactory.createWXAPI(BaseApplication.getAppContext(), Api.WeChat.AppId);
                        api.registerApp(Api.WeChat.AppId);
                        PayReq payReq = new PayReq();

                        payReq.appId = orderInfo.appId;
                        payReq.nonceStr = orderInfo.nonceStr;
                        payReq.packageValue = orderInfo.packageMsg;
                        payReq.partnerId = orderInfo.partnerId;
                        payReq.prepayId = orderInfo.prepayId;
                        payReq.sign = orderInfo.sign;
                        payReq.timeStamp = orderInfo.timeStamp;
//                        payReq.extData = mMemberCode;
//                    payReq.signType = orderInfo.signType;
                        api.sendReq(payReq);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort("请先安装微信应用！");
                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                LogUtil.d(TAG, "请求失败");
                ToastUtil.showShortForNet(msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                LogUtil.d(TAG, "请求失败");
                ToastUtil.showShortForNet(msg);
            }
        });
    }

    /**
     * 支付宝支付
     */
    private void requestAliPay() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = orderNumber;

        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestAliPay(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "支付宝支付：" + JSON.toJSONString(response.data));

                if (IsInstallWeChatOrAliPay.checkAliPayInstalled(CheckOrderActivity.this)) {
                    Data data = (Data) response.data;
                    String orderInfo = data.orderStr;

                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(CheckOrderActivity.this);
                            Map<String, String> result = alipay.payV2(orderInfo, true);

                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = result;
                            mHandler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    ToastUtil.showShort("请先安装支付宝应用！");
                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                LogUtil.d(TAG, "请求失败");
                ToastUtil.showShortForNet(msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                LogUtil.d(TAG, "请求失败");
                ToastUtil.showShortForNet(msg);
            }
        });

    }

}
