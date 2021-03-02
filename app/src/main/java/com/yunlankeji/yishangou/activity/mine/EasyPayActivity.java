package com.yunlankeji.yishangou.activity.mine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.BaseActivity;
import com.yunlankeji.yishangou.BaseApplication;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.MainActivity;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.dialog.ProtocolDialog;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.service.LocationService;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class EasyPayActivity extends BaseActivity {

    private static final String TAG = "EasyPayActivity";
    @BindView(R.id.m_back_iv)
    AppCompatImageView mBackIv;
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;
    @BindView(R.id.m_no_quota_ll)
    LinearLayout mNoQuotaLl;
    @BindView(R.id.m_no_quota_iv)
    ImageView mNoQuotaIv;
    private Boolean isShowDialog;

    @Override
    public int setLayout() {
        return R.layout.activity_easy_pay;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("易闪付");
//        isShowDialog = (Boolean) SPUtils.get(BaseApplication.getAppContext(), "isShowDialog", true);

        //暂无额度图标
        Glide.with(this)
                .load(R.mipmap.icon_no_quota)
                .into(mNoQuotaIv);
    }

    @Override
    public void initData() {
        //获取易闪付协议
        requestUpdateSystemVersion();
    }

    /**
     * 获取易闪付协议
     */
    private void requestUpdateSystemVersion() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.id = "207";
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestUpdateSystemVersion(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "易闪付协议：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;

                if (!Global.agreeStatus.equals("1")) {
                    //显示隐私权限弹窗
                    showProtocolDialog(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });
    }

    /**
     * 隐私条款与协议
     *
     * @param data
     */
    private void showProtocolDialog(Data data) {
        ProtocolDialog protocolDialog = new ProtocolDialog(EasyPayActivity.this);
        protocolDialog.setCanceledOnTouchOutside(false);
        protocolDialog.setCaption("隐私条款与协议")
                .setMessage(data.propertyValue)
                .setNegativeButton("取消", new ProtocolDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton("确定", new ProtocolDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        if (protocolDialog.getCheck()) {
                            dialog.dismiss();
//                            SPUtils.put(BaseApplication.getAppContext(), "isShowDialog", false);
                            //修改协议是否同意状态
                            requestUpdateAgreeStatus();

                        } else {
                            ToastUtil.showShort("请先勾选同意");
                        }
                    }
                }).show();

    }

    /**
     * 修改易闪付协议是否同意状态
     */
    private void requestUpdateAgreeStatus() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        paramInfo.agreeStatus = "1";
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateAgreeStatus(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "同意易闪付协议：" + JSON.toJSONString(response.data));

                //获取用户信息，修改同意状态
                requestMemberInfo();

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

    private void requestMemberInfo() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestMemberInfo(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "用户信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {
                    SPUtils.put(EasyPayActivity.this, "userInfo", data);
                    Global.agreeStatus = data.agreeStatus;
                    Global.balanceAccount = data.balanceAccount;
                    Global.createDt = data.createDt;
                    Global.id = data.id;
                    Global.inviteCode = data.inviteCode;
                    Global.isMerchant = data.isMerchant;
                    Global.isRider = data.isRider;
                    Global.logo = data.logo;
                    Global.memberCode = data.memberCode;
                    Global.memberName = data.memberName;
                    Global.parentOneCode = data.parentOneCode;
                    Global.parentSecondCode = data.parentSecondCode;
                    Global.phone = data.phone;
                    Global.pwd = data.pwd;
                    Global.totalAccount = data.totalAccount;
                    Global.merchantCode = data.merchantCode;
                    Global.merchantName = data.merchantName;
                    Global.riderCode = data.riderCode;
                    Global.agreeStatus = data.agreeStatus;
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
