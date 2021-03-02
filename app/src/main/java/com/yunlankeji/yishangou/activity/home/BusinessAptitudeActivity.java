package com.yunlankeji.yishangou.activity.home;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.utils.ConstantUtil;

import butterknife.BindView;
import butterknife.OnClick;

//商家资质
public class BusinessAptitudeActivity extends BaseActivity {

    private static final String TAG = "BusinessAptitudeActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_merchant_licence_iv)
    ImageView mMerchantLicenceIv;



    @Override
    public int setLayout() {
        return R.layout.activity_business_aptitude;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_transparent));
        mTitleTv.setText("商家资质");

        String licenseUrl = getIntent().getStringExtra("licenseUrl");
        Glide.with(BusinessAptitudeActivity.this)
                .load(licenseUrl)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                .into(mMerchantLicenceIv);

    }

    @OnClick({R.id.m_back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
        }
    }
}
