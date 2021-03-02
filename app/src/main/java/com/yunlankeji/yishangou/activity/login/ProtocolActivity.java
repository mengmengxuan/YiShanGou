package com.yunlankeji.yishangou.activity.login;

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

public class ProtocolActivity extends BaseActivity {

    private static final String TAG = "ProtocolActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_protocol_tv)
    TextView mProtocolTv;//隐私内容

    @Override
    public int setLayout() {
        return R.layout.activity_protocol;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("详情");
    }

    @Override
    public void initData() {
        //获取隐私协议
        requestUpdateSystemVersion();
    }

    /**
     * 获取隐私协议
     */
    private void requestUpdateSystemVersion() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = "206";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestUpdateSystemVersion(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "隐私协议：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    RichText.fromHtml(data.propertyValue).into(mProtocolTv);
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
