package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hwangjr.rxbus.RxBus;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.globle.ResultCode;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.ZLBusAction;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/25
 * Describe:
 */
public class BankCardWithdrawActivity extends BaseActivity {

    private static final String TAG = "BankCardWithdrawActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.part_line)
    View partLine;//partLine

    @BindView(R.id.m_bank_card_name_et)
    EditText mBankCardNameEt;
    @BindView(R.id.m_alipay_account_et)
    EditText mAlipayAccountEt;
    @BindView(R.id.m_account_bank_et)
    EditText mAccountBankEt;

    private String amount;
    private String bankCardName;
    private String bankCardCode;
    private String bankName;

    @Override
    public int setLayout() {
        return R.layout.acticity_bankcard_withdraw;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("银行卡");

        amount = getIntent().getStringExtra("amount");
    }

    @OnClick({R.id.m_back_iv, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_commit_tv:
                bankCardName = mBankCardNameEt.getText().toString().trim();
                bankCardCode = mAlipayAccountEt.getText().toString().trim();
                bankName = mAccountBankEt.getText().toString().trim();
                if (TextUtils.isEmpty(bankCardName)) {
                    ToastUtil.showShort("请输入您的用户名");
                } else if (TextUtils.isEmpty(bankCardCode)) {
                    ToastUtil.showShort("请输入您的银行卡号");
                } else if (TextUtils.isEmpty(bankName)) {
                    ToastUtil.showShort("请输入您的开户行");
                } else {
                    requestAddMemberCash();
                }
                break;
        }
    }

    //提现
    private void requestAddMemberCash() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.memberName = Global.memberName;
        paramInfo.cashAccount = amount;
        paramInfo.cashType = "3";
        paramInfo.bankCardName = bankCardName;
        paramInfo.bankCardCode = bankCardCode;
        paramInfo.bankName = bankName;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestAddMemberCash(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "提现：" + JSON.toJSONString(response.data));

                ToastUtil.showShort("提现成功");
                setResult(ResultCode.RESULT_CODE_WITHDRAW);

                RxBus.get().post(ZLBusAction.Refresh_Withdraw_Record, "Refresh_Withdraw_Record");
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
