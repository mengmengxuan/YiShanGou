package com.yunlankeji.yishangou.activity.runerrands;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
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
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/3
 * Describe:已送达页面
 */
public class RunErrandsSendedActivity extends BaseActivity {

    private static final String TAG = "RunErrandsSended";
    @BindView(R.id.m_back_iv)
    ImageView mBackIv;//返回按钮
    @BindView(R.id.m_title_tv)
    TextView mTitleTv;//标题名称
    @BindView(R.id.m_root_cl)
    LinearLayout mRootCl;//root
    @BindView(R.id.m_head_iv)
    ImageView mHeadIv;//
    @BindView(R.id.m_rider_name_tv)
    TextView mRiderNameTv;//
    @BindView(R.id.m_rider_phone_tv)
    TextView mRiderPhoneTv;//
    @BindView(R.id.m_call_phone_ll)
    LinearLayout mCallPhoneLl;//
    @BindView(R.id.m_commit_tv)
    TextView mCommitTv;//
    private String phoneNum;
    private String orderNumber;

    @Override
    public int setLayout() {
        return R.layout.activity_run_errands_sended;
    }

    @Override
    protected void initView() {
        ConstantUtil.setStatusBar(this, mRootCl);
        mTitleTv.setText("已送达");

        orderNumber = getIntent().getStringExtra("orderNumber");

    }

    @Override
    public void initData() {
        requestQueryOrderDetail();
    }

    @OnClick({R.id.m_back_iv, R.id.m_call_phone_ll, R.id.m_commit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.m_back_iv:
                finish();
                break;
            case R.id.m_call_phone_ll:
                showConnectDialog(phoneNum);
                break;
            case R.id.m_commit_tv:
                //确认送达
                requestUpdateMemberOrderStatus();
                break;
        }
    }

    private void showConnectDialog(String phoneNum) {
        DeleteDialog tagDialog = new DeleteDialog(this);
        tagDialog.setCaption("联系骑手")
                .setMessage("是否立即联系？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
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
     * 订单详情
     */
    private void requestQueryOrderDetail() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderNumber = orderNumber;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryOrderDetail(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "订单详情：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                if (data != null) {

                    //骑手头像
                    Glide.with(RunErrandsSendedActivity.this)
                            .load(R.mipmap.icon_rider_logo_default)
                            .into(mHeadIv);
                    //骑手姓名
                    mRiderNameTv.setText(data.riderName);
                    //骑手电话
                    phoneNum = data.riderPhone;
                    mRiderPhoneTv.setText(data.riderPhone);
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
     * 确认送达
     */
    private void requestUpdateMemberOrderStatus() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.orderStatus = "5";
        paramInfo.orderNumber = orderNumber;
        Call<ResponseBean> call = NetWorkManager.getInstance().getRequest().requestUpdateMemberOrderStatus(paramInfo);
        HttpRequestUtil.httpRequest(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "完成：" + JSON.toJSONString(response.data));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }

            @Override
            public void onDefeat(String code, String msg) {
                hideLoading();
                ToastUtil.showShort(msg);
                LogUtil.d(TAG, "请求失败");
            }
        });

    }
}
