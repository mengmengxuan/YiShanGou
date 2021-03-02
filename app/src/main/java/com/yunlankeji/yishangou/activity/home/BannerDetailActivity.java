package com.yunlankeji.yishangou.activity.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.zzhoujay.richtext.RichText;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/26
 * Describe:轮播详情页面
 */
public class BannerDetailActivity extends BaseActivity {
    private static final String TAG = "BannerDetailActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_banner_detail_tv)
    TextView mBannerDetailTv;//

    private String id;

    @Override
    public int setLayout() {
        return R.layout.activity_banner_detail;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("活动详情");
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_F36C17));
        mTitleTv.setTextColor(getResources().getColor(R.color.white));
        mBackIv.setImageResource(R.mipmap.icon_arrow_white_left);

        id = getIntent().getStringExtra("id");
    }

    @Override
    public void initData() {
        //轮播图详情
        requestBannerDetail();
    }

    /**
     * 轮播图详情
     */
    private void requestBannerDetail() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = id;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestBannerDetail(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "轮播图详情：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    RichText.fromHtml(data.detail).into(mBannerDetailTv);
                }
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();

            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
            }
        });

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
