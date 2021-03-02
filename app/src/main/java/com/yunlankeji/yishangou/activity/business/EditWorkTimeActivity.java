package com.yunlankeji.yishangou.activity.business;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import butterknife.BindView;

public class EditWorkTimeActivity extends BaseActivity {

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root

    private Boolean isEdit;// false新增 true修改
    private String id;

    @Override
    public int setLayout() {
        return R.layout.activity_edit_time;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("新增时间");

        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            mTitleTv.setText("修改时间");
//            mSureTv.setText("确认修改");
            Data time = (Data) getIntent().getSerializableExtra("time");
            id = time.id;
        }
    }
}
