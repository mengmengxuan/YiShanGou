package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.globle.RequestCode;
import com.yunlankeji.yishangou.globle.ResultCode;
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
 * Create by Snooker on 2020/12/25
 * Describe:提现页面
 */
public class WithdrawActivity extends BaseActivity {
    private static final String TAG = "WithdrawActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.part_line)
    View partLine;//partLine
    @BindView(R.id.m_alipay_rl)
    RelativeLayout mAlipayRl;
    @BindView(R.id.m_wechat_rl)
    RelativeLayout mWechatRl;
    @BindView(R.id.m_bank_card_rl)
    RelativeLayout mBankCardIvRl;
    @BindView(R.id.m_is_alipay_cb)
    CheckBox mIsAlipayCb;
    @BindView(R.id.m_is_wechat_cb)
    CheckBox mIsWechatCb;
    @BindView(R.id.m_is_bank_card_cb)
    CheckBox mIsBankCardCb;
    @BindView(R.id.m_amount_et)
    EditText mAmountEt;

    @Override
    public int setLayout() {
        return R.layout.activity_withdraw;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("提现");

        //支付宝
        mIsAlipayCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsAlipayCb.setChecked(isChecked);
                if (isChecked) {
                    mIsWechatCb.setChecked(false);
                    mIsBankCardCb.setChecked(false);
                }
            }
        });

        //微信
        mIsWechatCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsWechatCb.setChecked(isChecked);
                if (isChecked) {
                    mIsAlipayCb.setChecked(false);
                    mIsBankCardCb.setChecked(false);
                }
            }
        });

        //银行卡
        mIsBankCardCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsBankCardCb.setChecked(isChecked);
                if (isChecked) {
                    mIsWechatCb.setChecked(false);
                    mIsAlipayCb.setChecked(false);
                }
            }
        });
    }

    @OnClick({R.id.m_back_iv, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_commit_tv:
                String amount = mAmountEt.getText().toString().trim() + "";
                if (TextUtils.isEmpty(amount) || 0 >= Double.parseDouble(amount)) {
                    ToastUtil.showShort("提现金额必须大于0");
                    return;
                }

                boolean isAlipayCbChecked = mIsAlipayCb.isChecked();//支付宝
                boolean isWechatCbChecked = mIsWechatCb.isChecked();//微信
                boolean isBankCardCbChecked = mIsBankCardCb.isChecked();//银行卡
                intent.putExtra("amount", amount);
                if (isAlipayCbChecked) {
                    //选中的是支付宝
                    intent.setClass(this, AlipayWithdrawActivity.class);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_WITHDRAW);
                } else if (isWechatCbChecked) {
                    //选中的是微信
                    intent.setClass(this, WechatWithdrawActivity.class);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_WITHDRAW);
                } else if (isBankCardCbChecked) {
                    //选中的是银行卡
                    intent.setClass(this, BankCardWithdrawActivity.class);
                    startActivityForResult(intent, RequestCode.REQUEST_CODE_WITHDRAW);
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_WITHDRAW && resultCode == ResultCode.RESULT_CODE_WITHDRAW) {
            //提现
            finish();
        }

    }
}
