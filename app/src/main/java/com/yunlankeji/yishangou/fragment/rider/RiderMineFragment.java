package com.yunlankeji.yishangou.fragment.rider;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.MyWalletActivity;
import com.yunlankeji.yishangou.activity.rider.RiderInformationActivity;
import com.yunlankeji.yishangou.activity.rider.RiderOrderActivity;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2021/1/2
 * Describe:骑手"我的"页面
 */
public class RiderMineFragment extends BaseFragment {

    private static final String TAG = "MerchantCenterFragment";
    @BindView(R.id.m_my_wallet_rl)
    RelativeLayout mMyWalletRl;
    @BindView(R.id.m_receive_order_rl)
    RelativeLayout mReceiveOrderRl;
    @BindView(R.id.m_person_data_rl)
    RelativeLayout mPersonDataRl;
    @BindView(R.id.m_rider_logo_iv)
    ImageView mRiderLogoIv;
    @BindView(R.id.m_rider_name_tv)
    TextView mRiderNameTv;
    @BindView(R.id.m_rider_phone_tv)
    TextView mRiderPhoneTv;
    private Data riderInfo;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_rider_mine;
    }

    @Override
    protected void initView() {

    }

   /* @Override
    protected void initData() {
        //获取商家信息
        requestQueryRider();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        requestQueryRider();
    }

    /**
     * 查询骑手信息
     */
    private void requestQueryRider() {
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestQueryRider(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                LogUtil.d(TAG, "骑手信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;

                riderInfo = data;

                //头像
                if (!TextUtils.isEmpty(Global.logo)) {
                    Glide.with(getActivity())
                            .load(Global.logo)
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(mRiderLogoIv);
                }

                //商家名称
                mRiderNameTv.setText(data.realName);

                //电话
                mRiderPhoneTv.setText(data.phone);
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

    @OnClick({R.id.m_my_wallet_rl, R.id.m_receive_order_rl, R.id.m_person_data_rl})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_my_wallet_rl://我的钱包
                intent.setClass(getActivity(), MyWalletActivity.class);
                intent.putExtra("from", "rider");
                startActivity(intent);
                break;
            case R.id.m_receive_order_rl://配送订单
                doActivity(RiderOrderActivity.class);
                break;
            case R.id.m_person_data_rl://个人信息
                intent.setClass(getActivity(), RiderInformationActivity.class);
                intent.putExtra("riderInfo", riderInfo);
                startActivity(intent);
                break;
        }

    }
}
