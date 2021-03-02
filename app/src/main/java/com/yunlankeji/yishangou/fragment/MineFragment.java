package com.yunlankeji.yishangou.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.personal.baseutils.utils.Utils;
import com.yunlankeji.yishangou.BaseFragment;
import com.yunlankeji.yishangou.R;
import com.yunlankeji.yishangou.activity.mine.RiderSettleActivity;
import com.yunlankeji.yishangou.activity.rider.RiderOrderCenterActivity;
import com.yunlankeji.yishangou.activity.business.BusinessHostActivity;
import com.yunlankeji.yishangou.activity.business.BusinessOrderCenterActivity;
import com.yunlankeji.yishangou.activity.business.VerifyResultActivity;
import com.yunlankeji.yishangou.activity.mine.AboutUsActivity;
import com.yunlankeji.yishangou.activity.mine.EditDataActivity;
import com.yunlankeji.yishangou.activity.mine.MyInvitationActivity;
import com.yunlankeji.yishangou.activity.mine.MyWalletActivity;
import com.yunlankeji.yishangou.activity.mine.ReceiveAddressActivity;
import com.yunlankeji.yishangou.activity.mine.ShareActivity;
import com.yunlankeji.yishangou.activity.mine.EasyPayActivity;
import com.yunlankeji.yishangou.dialog.DeleteDialog;
import com.yunlankeji.yishangou.globle.Global;
import com.yunlankeji.yishangou.network.HttpRequestUtil;
import com.yunlankeji.yishangou.network.NetWorkManager;
import com.yunlankeji.yishangou.network.callback.HttpRequestCallback;
import com.yunlankeji.yishangou.network.responsebean.Data;
import com.yunlankeji.yishangou.network.responsebean.ParamInfo;
import com.yunlankeji.yishangou.network.responsebean.ResponseBean;
import com.yunlankeji.yishangou.service.LocationService;
import com.yunlankeji.yishangou.utils.ConstantUtil;
import com.yunlankeji.yishangou.utils.DataCleanManager;
import com.yunlankeji.yishangou.utils.LogUtil;
import com.yunlankeji.yishangou.utils.SPUtils;
import com.yunlankeji.yishangou.utils.ToastUtil;
import com.yunlankeji.yishangou.utils.ZLBusAction;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

/**
 * Create by Snooker on 2020/12/21
 * Describe:我的
 */
public class MineFragment extends BaseFragment {

    private static final String TAG = "MineFragment";
    @BindView(R.id.m_version_name_tv)
    TextView mVerTv;//版本号
    @BindView(R.id.m_memory_use_tv)
    TextView mMemoryUseTv;//缓存大小
    @BindView(R.id.m_head_iv)
    ImageView mHeadIv;//头像
    @BindView(R.id.m_name_tv)
    TextView mNameTv;//姓名
    @BindView(R.id.m_phone_tv)
    TextView mPhoneTv;//电话
    @BindView(R.id.m_invite_code_tv)
    TextView mInviteCodeTv;//邀请码

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        super.initView();
        String versionName = Utils.getVersionName(getActivity());
        if (!TextUtils.isEmpty(versionName)) {
            mVerTv.setText("V " + versionName);
        }

        try {
            mMemoryUseTv.setText(DataCleanManager.getTotalCacheSize(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取用户信息
        requestMemberInfo();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,
            tags = {@Tag(ZLBusAction.Request_User_Info)})
    public void requestUserInfo(String status) {
        if (status.equals("Request_User_Info")) {
            //获取用户信息
            requestMemberInfo();
        }
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
                SPUtils.put(getActivity(), "userInfo", data);

                //头像
                Glide.with(getActivity())
                        .load(data.logo)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .placeholder(R.mipmap.icon_defult_head)
                        .into(mHeadIv);

                //姓名
                if (TextUtils.isEmpty(data.memberName)) {
                    mNameTv.setText("暂无昵称");
                } else {
                    mNameTv.setText(data.memberName);
                }

                //电话
                if (TextUtils.isEmpty(data.phone)) {
                    mPhoneTv.setText("暂无电话");
                } else {
                    mPhoneTv.setText(data.phone);
                }

                //邀请码
                if (TextUtils.isEmpty(data.inviteCode)) {
                    mInviteCodeTv.setText("暂无邀请码");
                } else {
                    mInviteCodeTv.setText("邀请码：" + data.inviteCode);
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

    @OnClick({R.id.m_edit_data_tv, R.id.m_my_invite_ll, R.id.m_rider_ll, R.id.m_business_ll, R.id.m_my_wallet_ll,
            R.id.m_ysf_rl, R.id.m_address_center_rl, R.id.m_share_rl, R.id.m_about_us_rl,
            R.id.m_clear_rl, R.id.m_login_out_tv})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.m_edit_data_tv://修改资料
                intent.setClass(getActivity(), EditDataActivity.class);
                startActivity(intent);
                break;
            case R.id.m_my_invite_ll://我的邀请
                intent.setClass(getActivity(), MyInvitationActivity.class);
                startActivity(intent);
                break;
            case R.id.m_rider_ll://骑手入口

                //获取用户信息中的骑手状态
                requestRiderStatus();

                break;
            case R.id.m_business_ll://商家入口

                //获取用户信息中的商家状态
                requestBusinessStatus();

                break;
            case R.id.m_my_wallet_ll://我的钱包
                intent.setClass(getActivity(), MyWalletActivity.class);
                intent.putExtra("from", "mine");
                startActivity(intent);
                break;
            case R.id.m_ysf_rl://易闪付
                intent.setClass(getActivity(), EasyPayActivity.class);
                startActivity(intent);
                break;
            case R.id.m_address_center_rl://地址管理
                intent.setClass(getActivity(), ReceiveAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.m_share_rl://分享海报
                intent.setClass(getActivity(), ShareActivity.class);
                startActivity(intent);
                break;
            case R.id.m_about_us_rl://关于我们
                intent.setClass(getActivity(), AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.m_clear_rl://清理缓存
                showClearDialog();
                break;
            case R.id.m_login_out_tv://退出登录
                showLoginOutDialog();
                break;
        }
    }

    /**
     * 获取骑手状态
     */
    private void requestRiderStatus() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestMemberInfo(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "用户信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                SPUtils.put(getActivity(), "userInfo", data);

                Intent intent = new Intent();
                if ("0".equals(data.isRider)) {
                    //不是骑手，跳转到骑手入驻页面
                    intent.setClass(getActivity(), RiderSettleActivity.class);
                    startActivity(intent);
                } else if ("1".equals(data.isRider)) {
                    //是骑手，获取入驻状态，
                    //判断是否需要跳转至审核成功页面
                    Boolean jumpToStore = (Boolean) SPUtils.get(getActivity(), "jumpToRider", false);
                    if (jumpToStore) {
                        //直接进入骑手订单大厅
                        intent.setClass(getActivity(), RiderOrderCenterActivity.class);
                    } else {
                        //审核成功
                        intent.setClass(getActivity(), VerifyResultActivity.class);
                        intent.putExtra("from", "1");
                        intent.putExtra("page", "rider");
                    }
                    startActivity(intent);

                } else if ("2".equals(data.isRider)) {
                    //审核中
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "2");
                    intent.putExtra("page", "rider");
                    startActivity(intent);

                } else if ("3".equals(data.isRider)) {
                    //审核失败
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "3");
                    intent.putExtra("page", "rider");
                    startActivity(intent);
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
     * 获取商家状态
     */
    private void requestBusinessStatus() {
        showLoading();
        ParamInfo paramInfo = new ParamInfo();
        paramInfo.memberCode = Global.memberCode;
        Call<ResponseBean<Data>> call = NetWorkManager.getInstance().getRequest().requestMemberInfo(paramInfo);
        HttpRequestUtil.httpRequestForData(call, new HttpRequestCallback() {
            @Override
            public void onSuccess(ResponseBean response) {
                hideLoading();
                LogUtil.d(TAG, "用户信息：" + JSON.toJSONString(response.data));
                Data data = (Data) response.data;
                SPUtils.put(getActivity(), "userInfo", data);

                Intent intent = new Intent();
                if ("0".equals(data.isMerchant)) {
                    //不是商家，跳转到商家入驻页面
                    intent.setClass(getActivity(), BusinessHostActivity.class);
                    startActivity(intent);
                } else if ("1".equals(data.isMerchant)) {
                    //是商家，获取入驻状态，
                    //判断是否需要跳转至审核成功页面
                    Boolean jumpToStore = (Boolean) SPUtils.get(getActivity(), "jumpToStore", false);
                    if (jumpToStore) {
                        //直接进入店铺
                        intent.setClass(getActivity(), BusinessOrderCenterActivity.class);
                    } else {
                        //审核成功
                        intent.setClass(getActivity(), VerifyResultActivity.class);
                        intent.putExtra("from", "1");
                        intent.putExtra("page", "business");
                    }
                    startActivity(intent);

                } else if ("2".equals(data.isMerchant)) {
                    //审核中
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "2");
                    intent.putExtra("page", "business");
                    startActivity(intent);

                } else if ("3".equals(data.isMerchant)) {
                    //审核失败
                    intent.setClass(getActivity(), VerifyResultActivity.class);
                    intent.putExtra("from", "3");
                    intent.putExtra("page", "business");
                    startActivity(intent);
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

    //清理缓存弹窗
    private void showClearDialog() {
        DeleteDialog tagDialog = new DeleteDialog(getActivity());
        tagDialog.setCaption("清除缓存")
                .setMessage("是否确定清除缓存？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        DataCleanManager.clearAllCache(getActivity());
                        mMemoryUseTv.setText("0K");
                        ToastUtil.showShort("清理完成");
                        dialog.dismiss();
                    }
                }).show();
    }

    //退出登录
    private void showLoginOutDialog() {
        DeleteDialog tagDialog = new DeleteDialog(getActivity());
        tagDialog.setCaption("退出登录")
                .setMessage("是否确定退出登录？")
                .setNegativeButton("取消", new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", R.color.white, R.color.color_F36C17, new DeleteDialog.OnStatusListener() {
                    @Override
                    public void OnStatus(Dialog dialog) {
                        ConstantUtil.goLoginAndClearUserInfo(getActivity());
                        dialog.dismiss();
                    }
                }).show();
    }
}
