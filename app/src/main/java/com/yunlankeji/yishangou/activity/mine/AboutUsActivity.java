package com.yunlankeji.yishangou.activity.mine;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.appcompat.widget.AppCompatImageView;

import com.alibaba.fastjson.JSON;
import com.personal.baseutils.utils.Utils;
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
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.zzhoujay.richtext.RichText;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * 关于我们
 */
public class AboutUsActivity extends BaseActivity {

    private static final String TAG = "AboutUsActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_about_us_tv)
    TextView mAboutUsTv;
    @BindView(R.id.m_version_name_tv)
    TextView mVersionNameTv;

    @Override
    public int setLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("关于我们");
    }

    @Override
    public void initData() {
        requestAboutUs();

        String versionName = Utils.getVersionName(this);
        if (!TextUtils.isEmpty(versionName)) {
            mVersionNameTv.setText("V " + versionName);
        }
    }

    /**
     * 关于我们
     */
    private void requestAboutUs() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = "208";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestAboutUs(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "关于我们：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    RichText.fromHtml(data.propertyValue).into(mAboutUsTv);
                }
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

    @OnClick({R.id.m_back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
        }
    }
}
