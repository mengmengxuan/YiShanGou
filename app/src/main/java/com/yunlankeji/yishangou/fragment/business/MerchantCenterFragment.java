package com.yunlankeji.yishangou.fragment.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.business.BusinessHostActivity;
import com.yunlankeji.yishangou.activity.business.WorkTimeActivity;
import com.yunlankeji.yishangou.activity.home.BusinessInformationActivity;
import com.yunlankeji.yishangou.activity.mine.EasyPayActivity;
import com.yunlankeji.yishangou.activity.mine.GoodsManagerActivity;
import com.yunlankeji.yishangou.activity.mine.MyWalletActivity;
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

import javax.crypto.Cipher;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * 商铺管理
 */
public class MerchantCenterFragment extends BaseFragment {

    private static final String TAG = "MerchantCenterFragment";
    @BindView(R.id.m_merchant_info_rl)
    RelativeLayout mMerchantInfoRl;
    @BindView(R.id.m_my_wallet_rl)
    RelativeLayout mMyWalletRl;
    @BindView(R.id.m_goods_manager_rl)
    RelativeLayout mGoodsManagerRl;
    @BindView(R.id.m_busy_time_rl)
    RelativeLayout mBusyTimeRl;
    @BindView(R.id.rlPackagingFee)
    RelativeLayout rlPackagingFee;
    @BindView(R.id.m_closed_switch)
    Switch mClosedSwitch;
    @BindView(R.id.m_look_detail_tv)
    TextView mLookDetailTv;
    @BindView(R.id.tvPackagingFee)
    TextView tvPackagingFee;
    @BindView(R.id.m_merchant_logo_iv)
    ImageView mMerchantLogoIv;
    @BindView(R.id.m_merchant_name_tv)
    TextView mMerchantNameTv;
    @BindView(R.id.m_merchant_phone_tv)
    TextView mMerchantPhoneTv;

    private String isBusiness;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_merchant_center;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(getActivity(), mMerchantInfoRl);

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        //获取商家信息
        requestQueryMyMerchant();
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

                //头像
                Glide.with(getActivity())
                        .load(data.merchantLogo)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(mMerchantLogoIv);

                //商家名称
                mMerchantNameTv.setText(data.merchantName);

                //电话
                mMerchantPhoneTv.setText(data.phone);

                //包装费
                tvPackagingFee.setText(data.packingMoney);

                //是否打烊
                if (data.isBusiness.equals("0")) {
                    mClosedSwitch.setChecked(true);
                } else {
                    mClosedSwitch.setChecked(false);
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

    @OnClick({R.id.m_my_wallet_rl, R.id.m_look_detail_tv, R.id.m_goods_manager_rl, R.id.m_busy_time_rl,
            R.id.rlPackagingFee, R.id.m_closed_switch})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_look_detail_tv:
                intent.setClass(getActivity(), BusinessHostActivity.class);
                intent.putExtra("isHost", true);
                startActivity(intent);
                break;
            case R.id.m_my_wallet_rl://我的钱包
                intent.setClass(getActivity(), MyWalletActivity.class);
                intent.putExtra("from", "merchant");
                startActivity(intent);
                break;
            case R.id.m_goods_manager_rl://商品管理
                intent.setClass(getActivity(), GoodsManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.m_busy_time_rl://设置忙碌时间
                intent.setClass(getActivity(), WorkTimeActivity.class);
                startActivity(intent);
                break;
            case R.id.rlPackagingFee://打包费
                //显示修改打包费的弹窗
                showPackagingFeeDialog();
                break;
            case R.id.m_closed_switch://打烊开关
                boolean checked = mClosedSwitch.isChecked();
                if (checked) {
                    //0 开
                    isBusiness = "0";
                } else {
                    //1 关
                    isBusiness = "1";
                }
                requestUpdateMerchant();
                break;
        }
    }

    /**
     * 显示修改包装费的弹窗
     */
    private void showPackagingFeeDialog() {
        Dialog dialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_packaging_fee, null);
        dialog.setContentView(view);

        EditText etPackagingFee = view.findViewById(R.id.etPackagingFee);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        TextView tvConfirm = view.findViewById(R.id.tvConfirm);

        //取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //确认
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //包装费
                String packagingFee = etPackagingFee.getText().toString();
                //修改包装费
                updatePackagingFee(packagingFee);
                dialog.dismiss();
            }
        });

        //获取当前Activity所在的窗体
        Window window = dialog.getWindow();

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = wm.getDefaultDisplay();

        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.width = (int) (defaultDisplay.getWidth() * 0.9);
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);

        dialog.show();

        dialog.setCanceledOnTouchOutside(true);

    }

    private void updatePackagingFee(String packagingFee) {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = Global.merchantCode;
        paramInfo.packingMoney = packagingFee;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMerchant(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "包装费：" + JSON.toJSONString(response.data));
                requestQueryMyMerchant();
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

    private void requestUpdateMerchant() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.merchantCode = Global.merchantCode;
        paramInfo.isBusiness = isBusiness;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMerchant(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "打烊开关：" + JSON.toJSONString(response.data));
                requestQueryMyMerchant();

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
