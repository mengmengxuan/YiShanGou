package com.yunlankeji.yishangou.activity.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;

import com.alipay.sdk.app.PayTask;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
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

//支付页面
public class PayActivity extends BaseActivity {

    private static final String TAG = "PayActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_select_wx_iv)
    ImageView mSelectWxIv;//wx
    @BindView(R.id.m_select_ali_iv)
    ImageView mSelectAliIv;//ali
    @BindView(R.id.m_total_price_tv)
    TextView mTotalPriceTv;//

    private int payType = 0;//0微信支付1支付宝支付
    private String orderNum;
    private String ids;
    private String totalPrice;

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
                        //销毁立即支付页、提交订单页、店铺详情页
                        RxBus.get().post(ZLBusAction.Pay_Success, "Pay_Success");
//                        showAlert(WaitPayOrderDetailActivity.this, "支付成功");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        showAlert(PayActivity.this, payResult.getMemo());
                    }
                    break;
            }
        }
    };

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
        return R.layout.activity_pay;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("立即支付");

        orderNum = getIntent().getStringExtra("orderNum");
        ids = getIntent().getStringExtra("ids");
        totalPrice = getIntent().getStringExtra("totalPrice");
    }

    @Override
    public void initData() {
        mTotalPriceTv.setText(totalPrice);
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Pay_Success)})
    public void paySuccess(String status) {
        if ("Pay_Success".equals(status)) {
            finish();
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_wx_pay_rl, R.id.m_ali_pay_rl, R.id.m_pay_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_wx_pay_rl://微信支付
                payType = 0;
                mSelectWxIv.setVisibility(View.VISIBLE);
                mSelectAliIv.setVisibility(View.GONE);
                break;
            case R.id.m_ali_pay_rl://支付宝支付
                payType = 1;
                mSelectWxIv.setVisibility(View.GONE);
                mSelectAliIv.setVisibility(View.VISIBLE);
                break;
            case R.id.m_pay_tv://支付宝支付
                if (payType == 0) {
                    //微信支付
                    requestWeChatPay();
                } else if (payType == 1) {
                    //支付宝支付
                    requestAliPay();
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
        paramInfo.cartIds = ids;
        paramInfo.orderNumber = orderNum;

        Call<ResponseBean<WeChatPayBean>> call = NetWorkManager.getInstance().getRequest().requestWeChatPay(paramInfo);
        HttpRequestUtil.httpRequestWeChatPay(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "微信支付：" + JSON.toJSONString(response.data));

                if (IsInstallWeChatOrAliPay.isWeixinAvilible(PayActivity.this)) {
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
        paramInfo.cartIds = ids;
        paramInfo.orderNumber = orderNum;

        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestAliPay(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "支付宝支付：" + JSON.toJSONString(response.data));

                if (IsInstallWeChatOrAliPay.checkAliPayInstalled(PayActivity.this)) {
                    Data data = (Data) response.data;
                    String orderInfo = data.orderStr;

                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(PayActivity.this);
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
