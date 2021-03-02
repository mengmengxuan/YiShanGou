package com.yunlankeji.yishangou.activity.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.adapter.WithdrawalsRecordAdapter;
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
import com.yunlankeji.yishangou.utils.ZLBusAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/25
 * Describe:
 */
public class MyWalletActivity extends BaseActivity {
    private static final String TAG = "MyWalletActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.part_line)
    View partLine;//partLine
    @BindView(R.id.m_reward_tv)
    TextView mRewardTv;
    @BindView(R.id.m_withdraw_tv)
    TextView mWithdrawTv;
    @BindView(R.id.m_withdrawals_record_rv)
    RecyclerView mWithdrawalsRecordRv;
    private WithdrawalsRecordAdapter mWithdrawalsRecordAdapter;
    private List<Data> items = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_my_wallet;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("我的钱包");
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mBackIv.setImageResource(R.mipmap.icon_arrow_white_left);
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_transparent));

        String from = getIntent().getStringExtra("from");//mine  merchant

        mWithdrawalsRecordAdapter = new WithdrawalsRecordAdapter(this);
        mWithdrawalsRecordRv.setLayoutManager(new LinearLayoutManager(this));
        mWithdrawalsRecordAdapter.setItems(items);
        mWithdrawalsRecordRv.setAdapter(mWithdrawalsRecordAdapter);

    }

    @Override
    public void initData() {
        requestMemberInfo();
        //提现记录
        requestGetMemberCash();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Refresh_Withdraw_Record)})
    public void refreshWithdrawRecord(String status) {
        if (status.equals("Refresh_Withdraw_Record")) {
            //刷新提现记录
            if (items != null) {
                items.clear();
            }
            requestGetMemberCash();
        }
    }

    @OnClick({R.id.m_back_iv, R.id.m_withdraw_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_withdraw_tv://提现
                intent.setClass(this, WithdrawActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 钱包查询
     */
    private void requestGetMemberCash() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestGetMemberCash(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商家入驻：" + JSON.toJSONString(response));
                List<Data> cashList = (List<Data>) response.data;
                items.addAll(cashList);
                mWithdrawalsRecordAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showLongForNet(msg);
                LogUtil.d(TAG, msg);
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showLongForNet(msg);
                LogUtil.d(TAG, msg);
            }
        });
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Request_User_Info)})
    public void requestUserInfo(String status) {
        if (status.equals("Request_User_Info")) {
            //获取用户信息
            requestMemberInfo();
        }
    }

    private void requestMemberInfo() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestMemberInfo(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "用户信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                SPUtils.put(MyWalletActivity.this, "userInfo", data);
                mRewardTv.setText(data.balanceAccount);
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
