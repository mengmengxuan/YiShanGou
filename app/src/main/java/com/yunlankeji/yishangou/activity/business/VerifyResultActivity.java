package com.yunlankeji.yishangou.activity.business;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.RiderSettleActivity;
import com.yunlankeji.yishangou.activity.rider.RiderOrderCenterActivity;
import com.yunlankeji.yishangou.globle.Global;
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

public class VerifyResultActivity extends BaseActivity {

    private static final String TAG = "VerifyResultActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_icon_iv)
    ImageView mIconIv;//审核图标
    @BindView(R.id.m_status_tv)
    TextView mStatusTv;//审核结果
    @BindView(R.id.m_reason_tv)
    TextView mReasonTv;//审核描述
    @BindView(R.id.m_push_again_tv)
    TextView mPushAgainTv;

    private String from;//1 审核成功 2 审核中  3 审核失败
    private String page;//rider 骑手页面  business 商家页面

    @Override
    public int setLayout() {
        return R.layout.activity_verify_result;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("审核结果");

        from = getIntent().getStringExtra("from");
        page = getIntent().getStringExtra("page");
    }

    @Override
    public void initData() {
        if (from != null) {
            if (from.equals("1")) {
                //审核成功
                if (page != null) {
                    if (page.equals("rider")) {
                        Glide.with(this).load(R.mipmap.icon_success).into(mIconIv);
                        mStatusTv.setText("审核成功");
                        mReasonTv.setText("审核通过，您可以抢单配送了");
                        mPushAgainTv.setText("进入抢单大厅");
                    } else if (page.equals("business")) {
                        Glide.with(this).load(R.mipmap.icon_success).into(mIconIv);
                        mStatusTv.setText("审核成功");
                        mReasonTv.setText("审核通过，您可以经营自己的店铺了");
                        mPushAgainTv.setText("进入店铺");
                    } else if (page.equals("home_rider")) {
                        Glide.with(this).load(R.mipmap.icon_success).into(mIconIv);
                        mStatusTv.setText("审核成功");
                        mReasonTv.setText("审核通过，您可以抢单配送了");
                        mPushAgainTv.setVisibility(View.GONE);
                    } else if (page.equals("home_business")) {
                        Glide.with(this).load(R.mipmap.icon_success).into(mIconIv);
                        mStatusTv.setText("审核成功");
                        mReasonTv.setText("审核通过，您可以经营自己的店铺了");
                        mPushAgainTv.setVisibility(View.GONE);
                    }
                }
            } else if (from.equals("2")) {
                //审核中
                if (page != null) {
                    if (page.equals("rider") || page.equals("home_rider")) {
                        Glide.with(this).load(R.mipmap.icon_wait).into(mIconIv);
                        mStatusTv.setText("审核中");
                        mReasonTv.setText("预计1个工作日内完成审核，请耐心等待");
                        mPushAgainTv.setVisibility(View.GONE);
                    } else if (page.equals("business") || page.equals("home_business")) {
                        Glide.with(this).load(R.mipmap.icon_wait).into(mIconIv);
                        mStatusTv.setText("审核中");
                        mReasonTv.setText("预计1个工作日内完成审核，请耐心等待");
                        mPushAgainTv.setVisibility(View.GONE);
                    }
                }
            } else if (from.equals("3")) {
                //审核失败
                //获取商家信息
                if (page != null) {
                    if (page.equals("rider") || page.equals("home_rider")) {
                        //获取骑手信息，取出拒绝原因
                        requestQueryRider();
                        Glide.with(this).load(R.mipmap.icon_failed).into(mIconIv);
                        mStatusTv.setText("审核失败");
                        mPushAgainTv.setText("重新上传");
                    } else if (page.equals("business") || page.equals("home_business")) {
                        //获取商家信息，取出拒绝原因
                        requestQueryMyMerchant();
                        Glide.with(this).load(R.mipmap.icon_failed).into(mIconIv);
                        mStatusTv.setText("审核失败");
                        mPushAgainTv.setText("重新上传");
                    }
                }
            }
        }
    }

    /**
     * 获取骑手信息，取出拒绝原因
     */
    private void requestQueryRider() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryRider(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "骑手信息：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;
                //显示失败原因
                mReasonTv.setText(data.failReason);
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
     * 显示拒绝原因
     */
    private void requestQueryMyMerchant() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryMyMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "商家信息：" + JSON.toJSONString(response.data));

                Data data = (Data) response.data;
                //显示失败原因
                mReasonTv.setText(data.failReason);
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

    @OnClick({R.id.m_back_iv, R.id.m_push_again_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
            case R.id.m_push_again_tv:
                if (from != null) {
                    if (from.equals("1")) {
                        //审核成功，点击跳转到店铺订单页面
                        if (page != null) {
                            if (page.equals("rider")) {
                                intent.setClass(this, RiderOrderCenterActivity.class);
                                startActivity(intent);
                                finish();
                                SPUtils.put(this, "jumpToRider", true);
                            } else if (page.equals("business")) {
                                intent.setClass(this, BusinessOrderCenterActivity.class);
                                startActivity(intent);
                                finish();
                                SPUtils.put(this, "jumpToStore", true);
                            }
                        }

                    } else if (from.equals("3")) {
                        //审核失败，点击跳转到商家入驻页面
                        if (page != null) {
                            if (page.equals("rider")) {
                                intent.setClass(this, RiderSettleActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (page.equals("business")) {
                                intent.setClass(this, BusinessHostActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                    }
                }

                break;
        }
    }
}
