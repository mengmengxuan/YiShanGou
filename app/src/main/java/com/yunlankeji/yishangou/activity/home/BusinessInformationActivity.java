package com.yunlankeji.yishangou.activity.home;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
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

//商家信息
public class BusinessInformationActivity extends BaseActivity {
    private static final String TAG = "BusinessInformationActivity";

    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_merchant_name_tv)
    TextView mMerchantNameTv;
    @BindView(R.id.m_address_tv)
    TextView mAddressTv;
    @BindView(R.id.m_monthly_sale_tv)
    TextView mMonthlySaleTv;
    @BindView(R.id.m_distance_tv)
    TextView mDistanceTv;
    @BindView(R.id.m_merchant_logo_iv)
    ImageView mMerchantLogoIv;
    @BindView(R.id.m_work_time_tv)
    TextView mWorkTimeTv;

    private String merchantCode;
    private String licenseUrl;
    private String merchantPhone;

    @Override
    public int setLayout() {
        return R.layout.activity_business_information;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mRootCl.setBackgroundColor(getResources().getColor(R.color.color_transparent));
        String businessName = getIntent().getStringExtra("businessName");
        mTitleTv.setText(businessName);

        merchantCode = getIntent().getStringExtra("merchantCode");
    }

    @Override
    public void initData() {
        super.initData();
        requestMerchant();
    }

    @OnClick({R.id.m_back_iv, R.id.m_business_aptitude_rl, R.id.m_connect_merchant_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_business_aptitude_rl:
                doActivity(this, BusinessAptitudeActivity.class,licenseUrl,"licenseUrl");
                break;
            case R.id.m_connect_merchant_tv:
                showConnectDialog("联系商家", merchantPhone);
                break;
        }
    }

    //联系商家(骑手)
    private void showConnectDialog(String title, String phoneNum) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption(title)
                .setMessage("是否立即联系？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phoneNum);
                        intent.setData(data);
                        startActivity(intent);
                    }
                }).show();
    }


    /**
     * 获取商家信息
     */
    private void requestMerchant() {
        showLoading();
        String latitude = (String) SPUtils.get(this, "latitude", "");
        String longitude = (String) SPUtils.get(this, "longitude", "");

        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = merchantCode;
        paramInfo.latitude = latitude;
        paramInfo.longitude = longitude;

        LogUtil.d(TAG, "  paramInfo.merchantCode --> " + paramInfo.merchantCode);

        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "商家信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                //商铺logo
                Glide.with(BusinessInformationActivity.this)
                        .load(data.merchantLogo)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(10)))
                        .into(mMerchantLogoIv);

                //商铺名称
                mMerchantNameTv.setText(data.merchantName);

                //月销
                if (data.saleCount!=null){
                    mMonthlySaleTv.setText("月销" + data.saleCount);
                }else{
                    mMonthlySaleTv.setText("月销0");
                }
                //位置
                mAddressTv.setText(data.location);

                //距离
                mDistanceTv.setText(data.distance + "km");

                licenseUrl = data.licenseUrl;
                merchantPhone = data.phone;
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
