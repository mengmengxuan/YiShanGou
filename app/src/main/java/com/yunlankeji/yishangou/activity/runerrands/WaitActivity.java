package com.yunlankeji.yishangou.activity.runerrands;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class WaitActivity extends BaseActivity {

    private static final String TAG = "WaitActivity";
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

    private String orderNumber;
    private Data order;

    @Override
    public int setLayout() {
        return R.layout.activity_wait_result;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("等待接单");

    }

    @OnClick({R.id.m_back_iv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
        }
    }

}
