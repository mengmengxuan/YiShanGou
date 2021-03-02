package com.yunlankeji.yishangou.activity.business;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

//商家资料
public class BusinessEditInformationActivity extends BaseActivity {

    private static final String TAG = "BusinessEditInformationActivity";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_real_name_tv)
    TextView mRealNameTv;//
    @BindView(R.id.m_phone_tv)
    TextView mPhoneTv;//
    @BindView(R.id.m_merchant_name_tv)
    TextView mMerchantNameTv;//
    @BindView(R.id.m_location_tv)
    TextView mLocationTv;//
    @BindView(R.id.m_detail_address_tv)
    TextView mDetailAddressTv;//
    @BindView(R.id.m_id_card_num_tv)
    TextView mIdCardNumTv;//
    @BindView(R.id.iv_positive)
    ImageView ivPositive;//
    @BindView(R.id.iv_other_side)
    ImageView ivOtherSide;//
    @BindView(R.id.m_health_card_iv)
    ImageView mHealthCardIv;//
    @BindView(R.id.m_merchant_licence_iv)
    ImageView mMerchantLicenceIv;//
    @BindView(R.id.m_merchant_logo_iv)
    ImageView mMerchantLogoIv;//
    private List<Data> categoryItems = new ArrayList<>();

    @Override
    public int setLayout() {
        return R.layout.activity_business_edit_information;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("商家资料");

    }

    @Override
    public void initData() {
        //获取商家信息
        requestQueryMyMerchant();

        //获取分类
        requestHomeCategory();
    }

    @OnClick({R.id.m_back_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv://返回
                finish();
                break;
        }
    }

    /**
     * 获取分类
     */
    private void requestHomeCategory() {
        ParamInfo paramInfo = new ParamInfo();
        Call<ResponseBean<List<Data>>> call = NetWorkManager.getInstance().getRequest().requestHomeCategory(paramInfo);
        HttpRequestUtil.httpRequestForList(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "首页分类：" + JSON.toJSONString(response.data));
                List<Data> data = (List<Data>) response.data;
                if (data != null && data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        if (i < 10) {
                            categoryItems.add(data.get(i));
                        }
                    }
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

    /**
     * 查询商家信息，用于回显
     */
    private void requestQueryMyMerchant() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryMyMerchant(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "商家信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {

                    //法人真实姓名
                    mRealNameTv.setText(data.realName);
                    //手机号
                    mPhoneTv.setText(data.phone);
                    //店铺名
                    mMerchantNameTv.setText(data.merchantName);
                    //店铺地址
                    mLocationTv.setText(data.location);
                    //详细地址
                    mDetailAddressTv.setText(data.adress);
                    //身份证号
                    mIdCardNumTv.setText(data.idCardNo);
                    //身份证正面
                    Glide.with(BusinessEditInformationActivity.this)
                            .load(data.idCardFrontUrl)
                            .into(ivPositive);
                    //身份证背面
                    Glide.with(BusinessEditInformationActivity.this)
                            .load(data.idCardReverseUrl)
                            .into(ivOtherSide);
                    //健康证
                    Glide.with(BusinessEditInformationActivity.this)
                            .load(data.healthUrl)
                            .into(mHealthCardIv);
                    //营业执照
                    Glide.with(BusinessEditInformationActivity.this)
                            .load(data.licenseUrl)
                            .into(mMerchantLicenceIv);
                    //店铺头像
                    Glide.with(BusinessEditInformationActivity.this)
                            .load(data.merchantLogo)
                            .into(mMerchantLogoIv);
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

}
